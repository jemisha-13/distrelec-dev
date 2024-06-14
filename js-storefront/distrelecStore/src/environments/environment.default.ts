// Default env config for dev and prod builds
export const environment = {
  // * Storefront configuration
  occBaseUrl: 'https://localhost:9002/',
  recaptchaKey: '6LfjPCwUAAAAAH85BRYNleY5CCafloqqRoAw-afk',
  googleOptimizeKey: 'OPT-MRRJ7X4',
  localMaxAgeTtl: 300, // cache-control > max-age

  // basic auth
  internalIps:
    '^ ?(::ffff:)?(127.0.0.1|93.94.66.0|93.94.71.94|195.190.81.182|43.252.86.30' +
    '|115.160.212.154|43.227.20.174|52.59.57.47|100.104.28.5|100.104.28.6|14.143.47.206' +
    '|193.16.224.([1-9]|1[0-4]|3[3-9]|4[0-6])|52.249.186.59|52.174.140.38|18.159.242.159)$',
  skipBasicAuthSites: [
    'dev.distrelec.ch',
    'dev.elfadistrelec.lv',
    'pretest.distrelec.ch',
    'pretest.elfadistrelec.lv',
    'test.distrelec.ch',
    'test.elfadistrelec.lv',
    'dev2.distrelec.ch',
    'dev2.elfadistrelec.lv',
    'tech.distrelec.ch',
    'tech.elfadistrelec.lv',
  ],
  basicAuthUsers: [
    ['distrelec', 'e12b711a0c9f01fe62cee86d78c9a9e7'],
    ['automation', '82c6f80dfccd3407a36d88a51894cf68'],
    ['extuser', '99b22a82960dd8119399d51d19f9b4b4'],
    ['sap', '978884c889d0cc8d66db0af93f22601e'],
    ['exportfeed01', '37d0a8c8a0e2c8763aac1bc15b9ed594'],
  ],

  // * SSR server configuration
  debugLogging: true,
  forceCsr: false,
  useGzipCompression: false, // Enable to get more accurate performance measurements when running Lighthouse locally
  performanceTestMode: false, // Enable SSR Cache and Gzip Compression
  runHybrisBackend: false,
  cacheSsrOutput: false,
  useExternalCache: false,
  externalCacheContainerName: null,
  externalCacheConnectionString: null,
};
