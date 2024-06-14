import { environment as defaultEnv } from './environment.default';

let localEnv = {};
try {
  localEnv = require('./environment.local.ts')?.environment;
} catch {}

export const environment = {
  ...defaultEnv,
  occBaseUrl: null, // Configured via meta tag on CCV2: https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/eaef8c61b6d9477daf75bff9ac1b7eb4/1f30af7b54c0493096911bf404d0967d.html?locale=en-US
  production: true,
  cacheSsrOutput: true,
  useExternalCache: true,
  ...localEnv,
};
