import 'zone.js/node';

import {
  ngExpressEngine as engine,
  REQUEST,
  RESPONSE,
  NgExpressEngineDecorator,
  DefaultExpressServerLogger,
} from '@spartacus/setup/ssr';
import { APP_BASE_HREF } from '@angular/common';
import * as helmet from 'helmet';
import * as express from 'express';
import type { Request, Response } from 'express';
import { join } from 'path';
import { existsSync } from 'fs';
import { createProxyMiddleware } from 'http-proxy-middleware';
import * as bodyParser from 'body-parser';
import * as cookieParser from 'cookie-parser';
import * as url from 'url';
import type { ClientRequest } from 'http';

import { environment } from '@environment';
import { AppServerModule } from './src/main.server';
import { getRuleAction, RuleAction } from './src/server/rewrite-rules';
import { distBasicAuth } from './src/server/basic-auth';
import { getPreferredLanguage } from './src/server/language';
import { isMaintenanceActiveOnSite, shouldDisplayMaintenance, showMaintenancePage } from './src/server/maintenance';
import { getSiteId } from './src/server/site-id-resolver';
import {
  appendCacheControl,
  bodyToQueryParams,
  isSmarteditRequest,
  reEncodeParams,
  resolveAccStorefrontUrl,
  resolveApiUrl,
  resolveMediaUrl,
  resolveSmarteditHostname,
} from './src/server/utils';
import { CSP_OPTIONS } from './src/server/content-security-policy';
import { renderingStrategyResolver, renderKeyResolver } from './src/server/render-resolvers';
import { SsrOptimizationOptionsCentralizedCache } from './src/server/ssr-rendering-centralized-cache/ssr-optimization-options-centralized-cache';
import { NgExpressEngineDecoratorCentralizedCache } from './src/server/ssr-rendering-centralized-cache/ng-express-engine-decorator-centralized-cache';
import { SERVER_CONNECTION_TIMEOUT, SERVER_CONNECTION_TIMEOUT_LONG } from './src/server/constants';

const useGzipCompression = environment.useGzipCompression || environment.performanceTestMode;
const displayMaintenanceForLocal = true;
const runHybrisBackend = environment.runHybrisBackend;

let apiUrl: string;

const https = require('https'); // Doesn't work as import due to read-only property
https.globalAgent = new https.Agent({
  keepAlive: true,
  timeout: 305 * 1000, // 305 sec - longer than 5 min
  rejectUnauthorized: !environment.runHybrisBackend,
});

const ssrOptimizationOptions: SsrOptimizationOptionsCentralizedCache = {
  debug: environment.debugLogging,
  timeout: SERVER_CONNECTION_TIMEOUT,
  forcedSsrTimeout: 10000,
  //maxRenderTime: 300000, - leave default maxRenderTime
  reuseCurrentRendering: false,
  concurrency: 20,
  cache: environment.cacheSsrOutput, // If true, output will remain in cache after it has been cached by CDN. If false, cache contains only renders that finished after CSR fallback
  externalCache: environment.useExternalCache,
  compressCache: true,
  cacheSize: 1000, // has no effect for external cache
  ttl: 10800 * 1000, // 3 hour default if not defined in CACHE_TTL_BY_PAGETYPE
  logger: new DefaultExpressServerLogger(),
  renderingStrategyResolver,
  renderKeyResolver,
};

const ngExpressEngine = ssrOptimizationOptions?.externalCache
  ? NgExpressEngineDecoratorCentralizedCache.get(engine, ssrOptimizationOptions)
  : NgExpressEngineDecorator.get(engine, ssrOptimizationOptions);

// The Express app is exported so that it can be used by serverless Functions.
export function app(): express.Express {
  const server = express();

  const distFolder = join(process.cwd(), 'dist/distrelecStore/browser');
  const indexHtml = existsSync(join(distFolder, 'index.original.html')) ? 'index.original.html' : 'index';
  server.use(bodyParser.urlencoded({ extended: true }));

  if (useGzipCompression) {
    const compression = require('compression');
    server.use(compression());
  }

  //Using middleware so we can do req.cookies
  server.use(cookieParser());

  server.use(distBasicAuth(environment));

  server.set('trust proxy', 'loopback');

  // Our Universal express-engine (found @ https://github.com/angular/universal/tree/master/modules/express-engine)
  server.engine(
    'html',
    ngExpressEngine({
      bootstrap: AppServerModule,
      inlineCriticalCss: false,
    }),
  );

  server.use(helmet.contentSecurityPolicy(CSP_OPTIONS));

  server.set('view engine', 'html');
  server.set('views', distFolder);

  // Serve static files from /browser
  server.get(
    '*.*',
    express.static(distFolder), // cache-control is appended by ccv2 - security - response header sets
  );

  server.use(
    '/ariba/login',
    createProxyMiddleware({
      target: '',
      changeOrigin: true,
      secure: !runHybrisBackend,
      timeout: SERVER_CONNECTION_TIMEOUT,
      proxyTimeout: SERVER_CONNECTION_TIMEOUT,
      router: (req: Request) => resolveAccStorefrontUrl(req),
      onProxyReq: (proxyReq: ClientRequest, req: Request, res: Response) => reEncodeParams(proxyReq, req, res),
    }),
  );

  server.use(
    '/DisplayOCIParameters',
    createProxyMiddleware({
      target: '',
      changeOrigin: true,
      secure: !runHybrisBackend,
      timeout: SERVER_CONNECTION_TIMEOUT,
      proxyTimeout: SERVER_CONNECTION_TIMEOUT,
      router: (req: Request) => resolveAccStorefrontUrl(req),
      onProxyReq: (proxyReq: ClientRequest, req: Request, res: Response) => reEncodeParams(proxyReq, req, res),
    }),
  );

  server.use('/ociEntry*', bodyParser.text(), async (req, res) => {
    if (req.method === 'POST' && req.body) {
      if (req.get('Content-Type') === 'text/plain') {
        res.redirect(`${resolveAccStorefrontUrl(req)}/ociEntry?${req.body}`);
        return;
      }

      if (req.get('Content-Type') === 'application/x-www-form-urlencoded') {
        const queryParams = bodyToQueryParams(req.body);
        res.redirect(`${resolveAccStorefrontUrl(req)}/ociEntry?${queryParams}`);
        return;
      }
    }

    res.redirect(`${resolveAccStorefrontUrl(req)}${req.originalUrl}`);
  });

  server.use(
    '/medias',
    createProxyMiddleware({
      target: '',
      changeOrigin: true,
      secure: !runHybrisBackend,
      timeout: SERVER_CONNECTION_TIMEOUT,
      proxyTimeout: SERVER_CONNECTION_TIMEOUT,
      router: (req: Request) => resolveMediaUrl(req),
    }),
  );

  server.use('/cart/pdf', createApiProxyMiddleware());
  server.use('/compliance-document', createApiProxyMiddleware());
  server.use('/invoice-document-url', createApiProxyMiddleware());

  server.use(
    '/robots.txt',
    createProxyMiddleware({
      target: '',
      changeOrigin: true,
      secure: !runHybrisBackend,
      timeout: SERVER_CONNECTION_TIMEOUT,
      proxyTimeout: SERVER_CONNECTION_TIMEOUT,
      router: (req: Request) => resolveApiUrl(req),
      pathRewrite: async (path, req) => {
        if (apiUrl == null) {
          apiUrl = resolveApiUrl(req);
        }
        const siteId = await getSiteId(req, apiUrl);
        return `/rest/v2/${siteId}${path}`;
      },
    }),
  );

  server.use(
    '/sitemap*',
    createProxyMiddleware({
      target: '',
      changeOrigin: true,
      secure: !runHybrisBackend,
      timeout: SERVER_CONNECTION_TIMEOUT,
      proxyTimeout: SERVER_CONNECTION_TIMEOUT,
      router: (req: Request) => resolveMediaUrl(req),
      pathRewrite: async (path, req) => {
        if (apiUrl == null) {
          apiUrl = resolveApiUrl(req);
        }
        const siteId = await getSiteId(req, apiUrl);
        return `/Web/sitemap/${siteId}${path}`;
      },
    }),
  );

  server.get('/', async (req, res) => {
    const siteId = await getSiteId(req, resolveApiUrl(req));
    const lang = getPreferredLanguage(req, siteId);
    const query = req.originalUrl.includes('?') ? req.originalUrl.slice(req.originalUrl.indexOf('?')) : '';
    res.redirect('/' + lang + '/' + query);
  });

  // All regular routes use the Universal engine
  server.get('*', async (req, res) => {
    if (isSmarteditRequest(req)) {
      res.setHeader('X-Frame-Options', 'ALLOW-FROM ' + resolveSmarteditHostname(req));
    }

    if (apiUrl == null) {
      apiUrl = resolveApiUrl(req);
    }

    const isMaintenanceActive = await isMaintenanceActiveOnSite(req, apiUrl);
    if (isMaintenanceActive) {
      const showMaintenance = await shouldDisplayMaintenance(req, apiUrl, displayMaintenanceForLocal);
      if (showMaintenance) {
        // serve maintenance page
        appendCacheControl(true, req, res);
        await showMaintenancePage(req, res, apiUrl);
        return;
      }
    }

    const ruleAction: RuleAction = await getRuleAction(req, apiUrl);
    if (ruleAction != null) {
      if (ruleAction.status === 301 || ruleAction.status === 302) {
        const redirectURL = ruleAction.URL.startsWith('/') ? `${req.baseUrl}${ruleAction.URL}` : ruleAction.URL;
        const originalUrl = req.originalUrl;
        const query =
          originalUrl.indexOf('?') > 0 && redirectURL.indexOf('?') === -1
            ? originalUrl.slice(originalUrl.indexOf('?'))
            : '';

        if (ruleAction.status === 301 && (ruleAction.cache === undefined || ruleAction.cache)) {
          appendCacheControl(isMaintenanceActive, req, res);
        }

        res.redirect(ruleAction.status, redirectURL + query);
      } else {
        res.sendStatus(ruleAction.status);
      }
      return;
    }

    res.render(
      indexHtml,
      {
        req,
        providers: [
          { provide: APP_BASE_HREF, useValue: req.baseUrl },
          { provide: REQUEST, useValue: req },
          { provide: RESPONSE, useValue: res },
        ],
      },
      (err, html) => {
        const disableCache = isMaintenanceActive || html.includes('id="error-not-found-title"');
        appendCacheControl(disableCache, req, res);
        res.status(200).send(html);
      },
    );
  });

  server.use('/checkout/payment/success', (req, res) => {
    res.redirect(
      url.format({
        pathname: '/checkout/payment/success',
        query: req.body,
      }),
    );
  });

  server.use('/checkout/payment/failure', (req, res) => {
    res.redirect(
      url.format({
        pathname: '/checkout/payment/failure',
        query: req.body,
      }),
    );
  });

  return server;
}

function createApiProxyMiddleware() {
  return createProxyMiddleware({
    target: '',
    changeOrigin: true,
    router: (req: Request) => resolveApiUrl(req),
    secure: !runHybrisBackend,
    timeout: SERVER_CONNECTION_TIMEOUT_LONG,
    proxyTimeout: SERVER_CONNECTION_TIMEOUT_LONG,
    pathRewrite: async (path: string, req: Request) => {
      if (apiUrl == null) {
        apiUrl = resolveApiUrl(req);
      }
      const siteId = await getSiteId(req, apiUrl);
      let requestPath;
      if (path.startsWith('/cart/pdf')) {
        const cartGuid = req.query.cartGuid as string;
        const lang = req.query.lang as string;
        requestPath = `/rest/v2/${siteId}/cart/pdf/${cartGuid}?lang=${lang}`;
      } else {
        requestPath = '/rest/v2/' + siteId + path.replace('__', '.');
        if (!path.includes('?')) {
          let lang;
          if (req.cookies?.siteContext) {
            const parsedSiteContext = JSON.parse(req.cookies.siteContext);
            lang = parsedSiteContext.lang;
          }
          if (lang) {
            const l = lang.indexOf('_') !== -1 ? lang.substr(0, lang.indexOf('_')) : lang;
            requestPath = requestPath + '?lang=' + l;
          }
        }
      }
      console.log('Proxying request to: ' + apiUrl + requestPath);
      return requestPath;
    },
  });
}

function run(): void {
  const port = process.env.PORT || 4200;

  // Start up the Node server
  const server = app();
  server.listen(port, () => {
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

// Webpack will replace 'require' with '__webpack_require__'
// '__non_webpack_require__' is a proxy to Node 'require'
// The below code is to ensure that the server is run only when not requiring the bundle.
declare const __non_webpack_require__: NodeRequire; // eslint-disable-line @typescript-eslint/naming-convention
const mainModule = __non_webpack_require__.main;
const moduleFilename = (mainModule && mainModule.filename) || '';
if (moduleFilename === __filename || moduleFilename.includes('iisnode')) {
  run();
}

export * from './src/main.server';
