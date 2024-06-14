/* eslint-disable @typescript-eslint/member-ordering */
import { NgExpressEngineInstance } from '@spartacus/setup/ssr/engine-decorator/ng-express-engine-decorator';
import { CentralizedRenderingCache } from './centralized-rendering-cache';
import {
  DefaultExpressServerLogger,
  defaultSsrOptimizationOptions,
  EXPRESS_SERVER_LOGGER,
  ExpressServerLogger,
  ExpressServerLoggerContext,
  getDefaultRenderKey,
  RenderingEntry,
  RenderingStrategy,
  SsrCallbackFn,
} from '@spartacus/setup/ssr';
import * as fs from 'node:fs';
import { Request, Response } from 'express';
import { AzureBlobStorageService } from '../external-cache/azure-blob-storage/azure-blob-storage-service';
import { preprocessRequestForLogger } from './request-context-centralized-cache';
import { getLoggableSsrOptimizationOptions } from './get-loggable-ssr-optimization-options';
import {
  ExternalCacheStatus,
  SsrOptimizationOptionsCentralizedCache,
} from './ssr-optimization-options-centralized-cache';
import { ExternalCacheService } from '../external-cache/external-cache-service';
import { CACHE_TTL_BY_PAGETYPE } from '../constants';
import { getPageType, resolveBlobConfig } from '../utils';

/**
 * This class is based on OptimizedSsrEngine, but it uses a centralized cache for rendering results of all SSR nodes.
 * `renderingCache` is lazily initialized because we need to resolve the connection string asynchronously, so it does not
 * extend OptimizedSsrEngine or there will be two instances.
 *
 * A number of functions are duplicated from OptimizedSsrEngine because of this.
 *
 * The logic is changed slightly from the base class to support asynchronous fetching of rendered HTML from the cache.
 *
 * if the 'cache' is set to true then rendered pages are stored in azure blob storage to be served on next request.
 * If the `cache` is set to `false`, the response is evicted as soon as the first successful response is successfully returned.
 */
export class OptimizedSsrEngineCentralizedCache {
  protected currentConcurrency = 0;
  protected renderingCache: CentralizedRenderingCache; // Lazy-initialize this because we resolve the connection string asynchronously
  private readonly logger: ExpressServerLogger;
  private readonly templateCache = new Map<string, string>();

  /**
   * When the config `reuseCurrentRendering` is enabled, we want to perform
   * only one render for one rendering key and reuse the html result
   * for all the pending requests for the same rendering key.
   * Therefore, we need to store the callbacks for all the pending requests
   * and invoke them with the html after the render completes.
   *
   * This Map should be used only when `reuseCurrentRendering` config is enabled.
   * It's indexed by the rendering keys.
   */
  private readonly renderCallbacks = new Map<string, SsrCallbackFn[]>();

  get engineInstance(): NgExpressEngineInstance {
    return this.renderResponse.bind(this);
  }

  constructor(
    protected expressEngine: NgExpressEngineInstance,
    protected ssrOptions?: SsrOptimizationOptionsCentralizedCache,
  ) {
    this.ssrOptions = ssrOptions
      ? {
          ...defaultSsrOptimizationOptions,
          // overrides the default options
          ...ssrOptions,
        }
      : undefined;

    this.logger = ssrOptions?.logger || new DefaultExpressServerLogger();
    this.logOptions();
  }

  protected logOptions(): void {
    if (!this.ssrOptions) {
      return;
    }

    const loggableSsrOptions = getLoggableSsrOptimizationOptions(this.ssrOptions);

    this.log(`[spartacus] SSR optimization engine initialized`, true, {
      options: loggableSsrOptions,
    });
  }

  /**
   * When SSR page can not be returned in time, we're returning index.html of
   * the CSR application.
   * The CSR application is returned with the "Cache-Control: no-store" response-header. This notifies external cache systems to not use the CSR application for the subsequent request.
   */
  protected fallbackToCsr(response: Response, filePath: string, callback: SsrCallbackFn): void {
    response.set('Cache-Control', 'no-store');
    callback(undefined, this.getDocument(filePath));
  }

  protected getRenderingKey(request: Request): string {
    return this.ssrOptions?.renderKeyResolver
      ? this.ssrOptions.renderKeyResolver(request)
      : getDefaultRenderKey(request);
  }

  protected getRenderingStrategy(request: Request): RenderingStrategy {
    return this.ssrOptions?.renderingStrategyResolver
      ? this.ssrOptions.renderingStrategyResolver(request)
      : RenderingStrategy.DEFAULT;
  }

  /**
   * When returns true, the server side rendering should be performed.
   * When returns false, the CSR fallback should be returned.
   *
   * We should not render, when there is already
   * a pending rendering for the same rendering key
   * (unless the `reuseCurrentRendering` config option is enabled)
   * OR when the concurrency limit is exceeded.
   */
  protected async shouldRender(request: Request, cacheResponse: ExternalCacheStatus): Promise<boolean> {
    const renderingKey = this.getRenderingKey(request);

    const isRendering = cacheResponse === ExternalCacheStatus.RENDERING_IN_PROCESS;
    const isRenderingOnThisNode = this.renderingCache.isRendering(renderingKey);
    const concurrencyLimitExceeded = this.isConcurrencyLimitExceeded(renderingKey, isRenderingOnThisNode);
    const fallBack = isRendering && (!this.ssrOptions?.reuseCurrentRendering || !isRenderingOnThisNode);

    if (fallBack) {
      this.log(
        `CSR fallback: rendering in progress on ${isRenderingOnThisNode ? 'this node' : 'a different node'} (${
          request?.originalUrl
        })`,
        true,
        { request },
      );
    } else if (concurrencyLimitExceeded) {
      this.log(`CSR fallback: Concurrency limit exceeded (${this.ssrOptions?.concurrency})`, true, { request });
    }

    return (
      (!fallBack && !concurrencyLimitExceeded && this.getRenderingStrategy(request) !== RenderingStrategy.ALWAYS_CSR) ||
      this.getRenderingStrategy(request) === RenderingStrategy.ALWAYS_SSR
    );
  }

  /**
   * Checks for the concurrency limit
   *
   * @returns true if rendering this request would exceed the concurrency limit
   */
  private isConcurrencyLimitExceeded(_renderingKey: string, isRendering: boolean | undefined): boolean {
    // If we can reuse a pending render for this request, we don't take up a new concurrency slot.
    // In that case we don't exceed the concurrency limit even if the `currentConcurrency`
    // already reaches the limit.
    if (this.ssrOptions?.reuseCurrentRendering && isRendering) {
      return false;
    }

    return this.ssrOptions?.concurrency ? this.currentConcurrency >= this.ssrOptions.concurrency : false;
  }

  /**
   * Returns true, when the `timeout` option has been configured to non-zero value OR
   * when the rendering strategy for the given request is ALWAYS_SSR.
   * Otherwise, it returns false.
   */
  protected shouldTimeout(request: Request): boolean {
    return !!this.ssrOptions?.timeout || this.getRenderingStrategy(request) === RenderingStrategy.ALWAYS_SSR;
  }

  /**
   * Returns the timeout value.
   *
   * In case of the rendering strategy ALWAYS_SSR, it returns the config `forcedSsrTimeout`.
   * Otherwise, it returns the config `timeout`.
   */
  protected getTimeout(request: Request): number {
    return this.getRenderingStrategy(request) === RenderingStrategy.ALWAYS_SSR
      ? this.ssrOptions?.forcedSsrTimeout ?? 60000
      : this.ssrOptions?.timeout ?? 0;
  }

  /**
   * Handles the request and invokes the given `callback` with the result html / error.
   *
   * The result might be ether:
   * - a CSR fallback with a basic `index.html` content
   * - a result rendered by the original Angular Universal express engine
   * - a result from the in-memory cache (which was previously rendered by Angular Universal express engine).
   */
  protected async renderResponse(filePath: string, options: any, callback: SsrCallbackFn): Promise<void> {
    preprocessRequestForLogger(options.req, this.logger);

    const request: Request = options.req;
    const response: Response = options.req.res;

    if (!this.renderingCache) {
      await this.initCentralizedRenderingCache(request);
    }

    try {
      const cacheResponse = await this.returnCachedRenderFromCentralizedCache(request, callback);
      if (cacheResponse === ExternalCacheStatus.CACHE_EXISTS) {
        this.log(`Render from cache (${request?.originalUrl})`, true, {
          request,
        });
        return;
      }

      const shouldRenderResponse = await this.shouldRender(request, cacheResponse);
      if (!shouldRenderResponse) {
        this.fallbackToCsr(response, filePath, callback);
        return;
      }

      let requestTimeout: ReturnType<typeof setTimeout> | undefined;
      if (this.shouldTimeout(request)) {
        // establish timeout for rendering
        const timeout = this.getTimeout(request);
        requestTimeout = setTimeout(() => {
          requestTimeout = undefined;
          this.fallbackToCsr(response, filePath, callback);
          this.log(`SSR rendering exceeded timeout ${timeout}, fallbacking to CSR for ${request?.originalUrl}`, false, {
            request,
          });
        }, timeout);
      } else {
        // Here we respond with the fallback to CSR, but we don't `return`.
        // We let the actual rendering task to happen in the background
        // to eventually store the rendered result in the cache.
        this.fallbackToCsr(response, filePath, callback);
      }

      const renderingKey = this.getRenderingKey(request);
      const renderCallback: SsrCallbackFn = (err, html): void => {
        if (requestTimeout) {
          // if request is still waiting for render, return it
          clearTimeout(requestTimeout);
          callback(err, html);

          this.log(`Request is resolved with the SSR rendering result (${request?.originalUrl})`, true, {
            request,
          });

          if (this.shouldPersistInCache(request)) {
            this.renderingCache.store(renderingKey, err, html);
          } else {
            // Clear the in-progress render placeholder
            this.renderingCache.clear(renderingKey);
          }
        } else {
          // Store the render for future use in case rendering takes more than timeout
          this.renderingCache.store(renderingKey, err, html);
        }
      };

      this.handleRenderForCentralizedCache({
        filePath,
        options,
        renderCallback,
        request,
      });
    } catch (error) {
      this.log(
        `Error in initializing centralized rendering cache, fallbacking to CSR for ${request?.originalUrl}`,
        false,
        { request, error: error.toString() },
      );
      this.fallbackToCsr(response, filePath, callback);
    }
  }

  protected log(message: string, debug = true, context: ExpressServerLoggerContext): void {
    if (debug || this.ssrOptions?.debug) {
      this.logger.log(message, context || {});
    }
  }

  /** Retrieve the document from the cache or the filesystem */
  protected getDocument(filePath: string): string {
    let doc = this.templateCache.get(filePath);

    if (!doc) {
      doc = fs.readFileSync(filePath, 'utf-8');
      this.templateCache.set(filePath, doc);
    }

    return doc;
  }

  private handleRenderForCentralizedCache({
    filePath,
    options,
    renderCallback,
    request,
  }: {
    filePath: string;
    options: any;
    renderCallback: SsrCallbackFn;
    request: Request;
  }): void {
    if (!this.ssrOptions?.reuseCurrentRendering) {
      this.startRenderForCentralizedCache({
        filePath,
        options,
        renderCallback,
        request,
      });
      return;
    }

    const renderingKey = this.getRenderingKey(request);
    if (!this.renderCallbacks.has(renderingKey)) {
      this.renderCallbacks.set(renderingKey, []);
    }
    this.renderCallbacks.get(renderingKey)?.push(renderCallback);

    if (!this.renderingCache.isRendering(renderingKey)) {
      this.startRenderForCentralizedCache({
        filePath,
        options,
        request,
        renderCallback: (err, html) => {
          // Share the result of the render with all awaiting requests for the same key:

          // Note: we access the Map at the moment of the render finished (don't store value in a local variable),
          //       because in the meantime something might have deleted the value (i.e. when `maxRenderTime` passed).
          this.renderCallbacks.get(renderingKey)?.forEach((cb) => cb(err, html)); // pass the shared result to all waiting rendering callbacks
          this.renderCallbacks.delete(renderingKey);
        },
      });
    }

    this.log(`Request is waiting for the SSR rendering to complete (${request?.originalUrl})`, true, { request });
  }

  private startRenderForCentralizedCache({
    filePath,
    options,
    renderCallback,
    request,
  }: {
    filePath: string;
    options: any;
    renderCallback: SsrCallbackFn;
    request: Request;
  }): void {
    const renderingKey = this.getRenderingKey(request);

    // Setting the timeout for hanging renders that might not ever finish due to various reasons.
    // After the configured `maxRenderTime` passes, we consider the rendering task as hanging,
    // and release the concurrency slot and forget all callbacks waiting for the render's result.
    let maxRenderTimeout: ReturnType<typeof setTimeout> | undefined = setTimeout(() => {
      this.renderingCache.clear(renderingKey);
      maxRenderTimeout = undefined;
      this.currentConcurrency--;
      if (this.ssrOptions?.reuseCurrentRendering) {
        this.renderCallbacks.delete(renderingKey);
      }
      this.log(`Rendering of ${request?.originalUrl} was not able to complete. This might cause memory leaks!`, false, {
        request,
      });
    }, this.ssrOptions?.maxRenderTime ?? 300000); // 300000ms == 5 minutes

    this.log(`Rendering started (${request?.originalUrl})`, true, { request });
    this.renderingCache.setAsRendering(renderingKey);
    this.currentConcurrency++;

    options = {
      ...options,
      providers: [
        {
          provide: EXPRESS_SERVER_LOGGER,
          useValue: this.logger,
        },
      ],
    };

    this.expressEngine(filePath, options, (err, html) => {
      if (!maxRenderTimeout) {
        // ignore this render's result because it exceeded maxRenderTimeout
        this.log(
          `Rendering of ${request.originalUrl} completed after the specified maxRenderTime, therefore it was ignored.`,
          false,
          { request },
        );
        return;
      }
      clearTimeout(maxRenderTimeout);

      this.log(`Rendering completed (${request?.originalUrl})`, true, {
        request,
      });
      this.currentConcurrency--;

      renderCallback(err, html);
    });
  }

  protected async returnCachedRenderFromCentralizedCache(
    request: Request,
    callback: SsrCallbackFn,
  ): Promise<ExternalCacheStatus> {
    const key = this.getRenderingKey(request);

    let cacheStatus: ExternalCacheStatus = ExternalCacheStatus.CACHE_NOT_EXISTS;
    const cacheEntry = await this.renderingCache.getFromCentralizedCache(key);
    if (cacheEntry !== undefined) {
      if (cacheEntry.rendering) {
        this.log(`Rendering is already in progress (${request?.originalUrl})`, false, {
          key,
          cacheModified: new Date(cacheEntry.time),
          request,
        });
        cacheStatus = ExternalCacheStatus.RENDERING_IN_PROCESS;
      } else if (cacheEntry.html?.length > 0) {
        if (this.isFresh(request, cacheEntry)) {
          callback(cacheEntry.err, cacheEntry.html);
          cacheStatus = ExternalCacheStatus.CACHE_EXISTS;
        } else {
          this.log(`Cached response is stale (${request?.originalUrl})`, false, {
            key,
            cacheModified: new Date(cacheEntry.time),
            request,
          });
          cacheStatus = ExternalCacheStatus.CACHE_NOT_EXISTS;
        }
      } else if (!cacheEntry.rendering) {
        this.log('Concurrent rendering has expired', false, { key, request });
        cacheStatus = ExternalCacheStatus.CACHE_NOT_EXISTS;
      } else {
        this.log('Request does not exist in cache', false, { key, request });
      }
    }

    if (cacheEntry && !this.shouldPersistInCache(request)) {
      // Evict from cache after response is returned. It will be cached on CDN.
      this.renderingCache.clear(key);
    }
    return cacheStatus;
  }

  private shouldPersistInCache(request: Request): boolean {
    const cacheTtl = this.getCacheTtl(request);
    return this.ssrOptions?.cache && cacheTtl > 0;
  }

  private getCacheTtl(request: Request): number {
    // If TTL by pageType is 0, keep fallback renders for default TTL and return them to CDN on the next request
    return CACHE_TTL_BY_PAGETYPE[getPageType(request)] || this.ssrOptions.ttl;
  }

  private isFresh(request: Request, cacheEntry: RenderingEntry): boolean {
    const ttl = this.getCacheTtl(request);
    const age = Date.now() - cacheEntry.time;
    return age < ttl;
  }

  private async initCentralizedRenderingCache(request: Request): Promise<void> {
    const blobStorageConfig = await resolveBlobConfig(request);
    const cacheStorageService: ExternalCacheService = new AzureBlobStorageService(this.ssrOptions, blobStorageConfig);

    this.renderingCache = new CentralizedRenderingCache(this.logger, this.ssrOptions, cacheStorageService);
  }
}
