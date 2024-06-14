export const SERVER_CONNECTION_TIMEOUT = 5000;
export const SERVER_CONNECTION_TIMEOUT_LONG = 30_000;

export const PATH_PDP = /^.*\/p\/\d+(\?.*)?$/;
export const PATH_HOMEPAGE = /^\/(.{2}\/?)?$/;
export const PATH_CMS = /^.*\/cms\/.*$/;
export const PATH_NEW = /^.*\/new$/;
export const PATH_PRODUCT_FAMILY = /^.*\/pf\/.*$/;
export const PATH_MANUFACTURER = /^.*\/manufacturer\/.*$/;
export const PATH_CATEGORY = /^.*\/c\/.*$/;
export const PATH_CLEARANCE = /^.*\/clearance$/;

export const SSR_PATHS: RegExp[] = [
  PATH_HOMEPAGE,
  PATH_PDP,
  PATH_CMS,
  PATH_NEW,
  PATH_PRODUCT_FAMILY,
  PATH_MANUFACTURER,
  PATH_CATEGORY,
  PATH_CLEARANCE,
];

export const LONG_CACHE_PATHS: RegExp[] = [PATH_PDP, PATH_PRODUCT_FAMILY, PATH_MANUFACTURER, PATH_CATEGORY];

export type SsrPageType = 'PDP' | 'PLP' | 'CMS' | 'HOMEPAGE' | 'OTHER';

const ONE_DAY = 1000 * 60 * 60 * 24;
const THREE_HOURS = 1000 * 60 * 60 * 3;

export const CACHE_TTL_BY_PAGETYPE = {
  PDP: ONE_DAY,
  HOMEPAGE: THREE_HOURS,
  CMS: THREE_HOURS,
  PLP: ONE_DAY,
  OTHER: 0,
};

export const LANGUAGE_MAPPINGS = {
  ch: ['de', 'en', 'fr'],
  pl: ['en', 'pl'],
  de: ['en', 'de'],
  no: ['en', 'no'],
  nl: ['en', 'nl'],
  se: ['en', 'sv'],
  dk: ['en', 'da'],
  ex: ['en'],
  cz: ['cs'],
  it: ['it'],
  lv: ['lv'],
  li: ['lt'],
  hu: ['hu'],
  at: ['de'],
  lt: ['lt'],
  ro: ['ro'],
  sk: ['sk'],
  ee: ['et'],
  fi: ['fi', 'sv'],
  fr: ['fr'],
  be: ['nl', 'fr', 'en'],
  int: ['en'],
};

export const VALID_LANGUAGES = [
  'cs',
  'da',
  'de',
  'en',
  'et',
  'fi',
  'fr',
  'hu',
  'it',
  'lt',
  'lv',
  'nl',
  'no',
  'pl',
  'ro',
  'ru',
  'sk',
  'sv',
  'tr',
];

export const CONTEXT_URL_PATTERN = new RegExp('^/(' + VALID_LANGUAGES.join('|') + ')/');
