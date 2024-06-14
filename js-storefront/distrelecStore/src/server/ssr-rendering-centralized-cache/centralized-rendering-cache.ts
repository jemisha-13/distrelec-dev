import { ExpressServerLogger, ExpressServerLoggerContext, RenderingCache, RenderingEntry } from '@spartacus/setup/ssr';
import { ExternalCacheService } from '../external-cache/external-cache-service';
import {
  IS_RENDERING_CONTENT,
  SsrOptimizationOptionsCentralizedCache,
} from './ssr-optimization-options-centralized-cache';

/*
This rendering cache implementation is to use external cache storage to store SSR rendering result.
This doesn't maintain cache size considering unlimited cache can be stored in cache storage
*/
export class CentralizedRenderingCache extends RenderingCache {
  constructor(
    private loggerBlobStorage: ExpressServerLogger,
    private ssrOptions: SsrOptimizationOptionsCentralizedCache,
    private externalCacheService: ExternalCacheService,
  ) {
    super(ssrOptions);
  }

  /**
   * This method override OOB behaviour and to save blob with '' content to mark as rendering
   */
  override setAsRendering(key: string) {
    const content: string = IS_RENDERING_CONTENT;
    this.renders.set(key, { rendering: true });
    this.store(key, null, content);
  }

  /**
   * This store html response in centralized cache
   * this doesn't maintain cache size as idea with centralized cache is to have unlimited cache
   */
  override store(key: string, err: Error | null, html?: string) {
    if (html !== undefined) {
      let isRenderingMsg = false;
      if (html === IS_RENDERING_CONTENT) {
        isRenderingMsg = true;
      } else {
        this.renders.delete(key); // remove the rendering flag on this node
      }

      this.externalCacheService
        .upload(key, html)
        .then((uploadResponse) => {
          if (uploadResponse._response.status >= 200 && uploadResponse._response.status < 300) {
            if (isRenderingMsg) {
              this.log('In-progress render saved to centralized cache', false, { key });
            } else {
              this.log('Completed render saved to centralized cache', false, { key });
            }
          } else {
            this.log('Error in saving to centralized cache', false, {
              key,
              uploadResponse,
            });
          }
        })
        .catch((cacheError) => {
          console.error(cacheError);
          this.log('Exception in uploading response in centralized storage', false, { key, error: cacheError });
        });
    }
  }

  async getFromCentralizedCache(key: string): Promise<RenderingEntry | undefined> {
    const renderEntry: RenderingEntry = {};
    try {
      return await this.externalCacheService.get(key);
    } catch (error) {
      this.log('Error in fetching cached response from azure blob storage', false, {
        key,
        error: {
          message: error.message,
          stack: error.stack,
        },
      });
      return renderEntry;
    }
  }

  override clear(key: string) {
    this.renders.delete(key);

    this.externalCacheService
      .delete(key)
      .then((deleteResponse) => {
        if (deleteResponse.succeeded) {
          this.log(`Cached item deleted successfully`, false, {
            key,
          });
        } else {
          this.log('Error in deleting cached result from azure blob storage', false, { key });
        }
      })
      .catch((error) => {
        this.log('Error in deleting cached result from azure blob storage', false, { key, error });
      });
  }

  /**
   This method is copied from SSR optimization engine class
   */
  protected log(
    message: string,
    debug = true,
    //CXSPA-3680 - in a new major, let's make this argument required
    context?: ExpressServerLoggerContext,
  ): void {
    if (debug || this.ssrOptions?.debug) {
      this.loggerBlobStorage.log(message, context || {});
    }
  }
}
