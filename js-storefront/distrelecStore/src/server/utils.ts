import type { Request, Response } from 'express';
import type { ClientRequest } from 'http';
import { getPreferredLanguage } from './language';
import { environment } from '@environment';
import {
  LONG_CACHE_PATHS,
  PATH_CATEGORY,
  PATH_CLEARANCE,
  PATH_CMS,
  PATH_HOMEPAGE,
  PATH_MANUFACTURER,
  PATH_NEW,
  PATH_PDP,
  PATH_PRODUCT_FAMILY,
  SsrPageType,
} from './constants';
// eslint-disable-next-line @typescript-eslint/naming-convention
import * as QueryString from 'qs';
import fetch from 'node-fetch';
import { BlobStorageConfig } from './ssr-rendering-centralized-cache/ssr-optimization-options-centralized-cache';

const localMaxAgeTtl = environment.localMaxAgeTtl;
const apiDomain = environment.occBaseUrl?.replace('https://', '');
const runHybrisBackend = environment.runHybrisBackend;

export function getRequestUrl(req: Request): string {
  return getRequestOrigin(req) + req.originalUrl + getPreferredLanguage(req);
}

export function getRequestOrigin(req: Request): string {
  // If express is resolving and trusting X-Forwarded-Host, we want to take it
  // into an account to properly generate request origin.
  const trustProxyFn = req.app.get('trust proxy fn');
  let forwardedHost = req.get('X-Forwarded-Host');
  if (forwardedHost && trustProxyFn(req.connection.remoteAddress, 0)) {
    if (forwardedHost.indexOf(',') !== -1) {
      // Note: X-Forwarded-Host is normally only ever a
      //       single value, but this is to be safe.
      forwardedHost = forwardedHost.substring(0, forwardedHost.indexOf(',')).trimEnd();
    }
    return req.protocol + '://' + forwardedHost;
  } else {
    return req.protocol + '://' + req.get('host');
  }
}

export function isMonitor(req: Request): boolean {
  const userAgent = req.get('user-agent');

  return 'Incapsula Uptime Monitor' === userAgent;
}

export function isSmarteditRequest(req: Request): boolean {
  return req.originalUrl.startsWith('/cx-preview');
}

export function appendCacheControl(disableCache: boolean, req: Request, res: Response): void {
  const isCsrFallback = res.get('Cache-Control') === 'no-store'; // Set by OptimizedSsrEngine

  if (disableCache || isCsrFallback || isSmarteditRequest(req)) {
    res.set('Cache-Control', 'no-store, max-age=0, stale-if-error=0');
  } else {
    // cache only if maintenance is inactive
    let sMaxAge: number;
    if (LONG_CACHE_PATHS.find((longCachePath) => longCachePath.test(req.path))) {
      sMaxAge = 24 * 3600; // 24 hours
    } else {
      sMaxAge = 3 * 3600; // 3 hours
    }

    res.set(
      'Cache-Control',
      'public, max-age=' +
        localMaxAgeTtl +
        ', s-maxage=' +
        sMaxAge +
        ', stale-while-revalidate=86400, stale-if-error=0',
    );
  }
}

export function resolveAccStorefrontUrl(req: Request): string {
  const elements: string[] = req.hostname.split('.');
  let storefrontUrl: string;

  /* example request
   req = https://dev.distrelec.ch
   req.hostname = dev.distrelec.ch
   req.hostname.split('.') = ['dev', 'distrelec', 'ch']
  */

  //valid url
  if (new RegExp('(^|^[^:]+://|[].+.)(dev|dev2|distrelec|pretest|tech|test|elfa|local|www).').test(req.hostname)) {
    //handling local replacing port to match acc storefront
    if (elements.length === 2 && elements[1] === 'local') {
      storefrontUrl = `${req.hostname}:9002`;
    } else {
      //handling prod appending storefront in url
      if (elements[0] === 'www') {
        storefrontUrl = `storefront.${elements[1]}.${elements[2]}`;
      } else {
        // non prod - appending storefront in url
        storefrontUrl = `${elements[0]}.storefront.${elements[1]}.${elements[2]}`;
      }
    }
  }

  return `${req.protocol}://${storefrontUrl}`;
}

export function resolveApiUrl(req: Request): string {
  const domain = '.api.distrelec.com';
  return resolveApiMediaUrl(req, domain);
}

export function resolveMediaUrl(req: Request): string {
  const domain = '.media.distrelec.com';
  return resolveApiMediaUrl(req, domain);
}

export function resolveInternalUrl(req: Request): string {
  const domain = '-internal.distrelec.com';
  return resolveApiMediaUrl(req, domain);
}

function resolveApiMediaUrl(req: Request, suffix: string): string {
  const hostname: string = req.hostname;
  const elements: string[] = hostname.split('.');
  const prefix: string = elements[0];

  let targetUrl: string;

  if (elements[0] === 'localhost' || (elements.length === 2 && elements[1] === 'local')) {
    if (runHybrisBackend) {
      targetUrl = hostname + ':9002';
    } else {
      targetUrl = apiDomain;
    }
  } else if (prefix === 'www') {
    targetUrl = suffix.substring(1); // remove leading dot or hyphen char
  } else {
    targetUrl = prefix + suffix;
  }

  return 'https://' + targetUrl;
}

export function resolveSmarteditHostname(req: Request): string {
  const hostname: string = req.hostname;
  const localHostnameRegex = /^(elfa|distrelec|elfadistrelec)-[\w]+\.local$/;

  if (localHostnameRegex.test(hostname)) {
    return 'localhost';
  }

  const extractPrefixRegex = /^(?:int\.|(?:([\w]+)(?:-int)?\.)?)(?:distrelec|elfa|elfadistrelec)\.[\w]{2,3}$/;
  if (extractPrefixRegex.test(hostname)) {
    const groups = extractPrefixRegex.exec(hostname);
    const prefix = groups[1];
    return prefix ? prefix + '.hybris.distrelec.com' : 'hybris.distrelec.com';
  } else {
    console.error('Failed to extract environment prefix from hostname ' + hostname);
    return 'localhost';
  }
}

export async function resolveBlobConfig(req: Request): Promise<BlobStorageConfig> {
  if (environment.externalCacheConnectionString) {
    return {
      blobConnectionString: environment.externalCacheConnectionString,
      blobContainerName: environment.externalCacheContainerName ?? 'ssr-cache',
      deploymentTimestamp: Date.now(),
    };
  } else {
    const ssrConfigEndpoint = `${resolveInternalUrl(req)}/internal/configuration/ssrCache`;
    const response = await fetch(ssrConfigEndpoint);
    if (response.ok) {
      return (await response.json()) as BlobStorageConfig;
    } else {
      throw new Error(`Received status ${response.status} trying to fetch blob config from ${ssrConfigEndpoint}`);
    }
  }
}

export function reEncodeParams(proxyReq: ClientRequest, req: Request, res: Response) {
  if (req.method === 'POST' && req.body && req.get('Content-Type') === 'application/x-www-form-urlencoded') {
    // rewrite body as it is not by default
    const body = bodyToQueryParams(req.body);

    proxyReq.setHeader('content-type', 'application/x-www-form-urlencoded');
    proxyReq.setHeader('content-length', body.length);
    proxyReq.write(body);
    proxyReq.end();
  }
}

export function bodyToQueryParams(body: any) {
  return Object.keys(body)
    .map((key) => {
      if (Array.isArray(body[key])) {
        let result = '';
        body[key].forEach((element: string | number | boolean, index: number) => {
          if (index > 0) {
            result = result + '&';
          }
          result = result + encodeURIComponent(key) + '[' + (index + 1) + ']=' + encodeURIComponent(element);
        });
        return result;
      } else {
        return encodeURIComponent(key) + '=' + encodeURIComponent(body[key]);
      }
    })
    .join('&');
}

export function serializeQueryParams(queryParams: QueryString.ParsedQs): string {
  // Keys are ordered to ensure consistent cache keys
  // Multiple keys with the same value will be ordered by value
  const keys = Object.keys(queryParams).sort();

  const queryArray = keys.flatMap((key) => {
    const value = queryParams[key];
    if (Array.isArray(value)) {
      return value.sort().map((val) => `${encodeURIComponent(key)}=${encodeURIComponent(String(val))}`);
    } else if (typeof value === 'object' && value !== null) {
      // We shouldn't have any query params using [] syntax, but just in case we handle them here
      return Object.keys(value)
        .sort()
        .map(
          (subKey) =>
            `${encodeURIComponent(key)}[${encodeURIComponent(subKey)}]=${encodeURIComponent(String(value[subKey]))}`,
        );
    } else {
      return `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`;
    }
  });

  return queryArray.length > 0 ? `?${queryArray.join('&')}` : '';
}

export function extractPageId(req: Request, paramIdentifier: string): string {
  // For paths like `/c/{categoryCode}` and `/p/{productCode}` match on `c` or `p` and extract the ID
  const pathSegments = req.path.split('/');
  const paramIndex = pathSegments.indexOf(paramIdentifier);
  if (paramIndex > -1) {
    return pathSegments[paramIndex + 1];
  }

  return '';
}

export function getPageType(req: Request): SsrPageType {
  const path = req.path;
  if (PATH_PDP.test(path)) {
    return 'PDP';
  }

  if (PATH_CMS.test(path)) {
    return 'CMS';
  }

  if (PATH_HOMEPAGE.test(path)) {
    return 'HOMEPAGE';
  }

  if (
    [PATH_CATEGORY, PATH_MANUFACTURER, PATH_PRODUCT_FAMILY, PATH_NEW, PATH_CLEARANCE].find((pathRegex) =>
      pathRegex.test(path),
    )
  ) {
    return 'PLP';
  }

  return 'OTHER';
}
