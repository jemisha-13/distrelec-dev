import { RenderingStrategy } from '@spartacus/setup/ssr';
import type { Request } from 'express';
// eslint-disable-next-line @typescript-eslint/naming-convention
import * as QueryString from 'qs';
import { environment } from '@environment';
import {
  extractPageId,
  getPageType,
  getRequestOrigin,
  isMonitor,
  isSmarteditRequest,
  serializeQueryParams,
} from './utils';
import { CONTEXT_URL_PATTERN, SSR_PATHS } from './constants';
import { getRouteLanguage } from './language';
import { cyrb64Hash } from './hash';

const EXCLUDED_QUERY_PARAMS = [
  'cq_cmp',
  'cq_con',
  'cq_med',
  'cq_net',
  'cq_plt',
  'cq_src',
  'cq_term',
  'ext_cid',
  'gclid',
  'gclsrc',
  'int_cid',
  'msclkid',
  'ngsw-cache-bust',
  'pup_cid',
  'pup_id',
  'redirectQuery',
  'sid',
  'utm_campaign',
  'utm_content',
  'utm_medium',
  'utm_source',
  'utm_term',
];

export function renderingStrategyResolver(req: Request) {
  if (environment.forceCsr || isMonitor(req) || isSmarteditRequest(req)) {
    return RenderingStrategy.ALWAYS_CSR;
  }

  // Only use SSR for certain paths which have context parameter(s) (i.e. language), as the content will
  // vary based on cookies if context is not provided
  if (req.path.match(CONTEXT_URL_PATTERN) && SSR_PATHS.find((ssrPath) => ssrPath.test(req.path))) {
    return RenderingStrategy.DEFAULT;
  }

  return RenderingStrategy.ALWAYS_CSR;
}

/**
 * Resolve the render key for the given request.
 *
 * Render keys are used to identify the rendering task and to reuse the rendering result for subsequent requests.
 * The format is `siteId/pageType/identifier`. If blob storage is used, the key is also used as the blob name.
 * In blob storage `/` will create a folder structure. Identifiers are encoded prevent unnecessary folder creation.
 *
 * Identifier is based on path and query parameters, which are filtered, ordered and hashed to ensure consistent cache
 * keys.
 *
 * For PDP, the identifier is the product code and the language. SEO descriptions and query params are stripped from
 * the path to improve cache reuse.
 *
 * The maximum length of the key is 1024 characters for blob storage and 256 characters when using the Azurite Eumlator
 *
 * @param req
 */
export function renderKeyResolver(req: Request) {
  // Site ID resolution is async so use origin minus port (if present) as site identifier
  const origin = getRequestOrigin(req)
    .replace(/^https?:\/\//, '')
    .split(':')[0];

  const pageType = getPageType(req);

  if (pageType === 'PDP') {
    const productCode = extractPageId(req, 'p');
    const language = getRouteLanguage(req);
    return `${origin}/${pageType}/${productCode}-${language}`;
  }

  const serializedPath = req.path.slice(1).replaceAll('/', '_');
  const serializedQuery = serializeQueryParams(filterQueryParams(req.query));
  const hashedQuery = cyrb64Hash(serializedQuery);

  let renderKey = `${origin}/${pageType}/${serializedPath}__${hashedQuery}`;
  if (renderKey.length > 1024) {
    console.warn('Render key URL path too long, hashing path + query: ' + renderKey);
    renderKey = `${origin}/${pageType}/${cyrb64Hash(req.path + serializedQuery)}`;
  }

  return renderKey;
}

function filterQueryParams(queryParams: QueryString.ParsedQs) {
  return queryParams
    ? Object.keys(queryParams).reduce((acc, key) => {
        if (!EXCLUDED_QUERY_PARAMS.includes(key)) {
          acc[key] = queryParams[key];
        }
        return acc;
      }, {})
    : {};
}
