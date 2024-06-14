import fetch from 'node-fetch';
import { Request } from 'express';
import { getPreferredLanguage } from './language';
import { CONTEXT_URL_PATTERN } from './constants';

const language = require('./language');
const siteIdResolver = require('./site-id-resolver.ts');

const DISTRELEC_HOST_BIZ = /^.*\.distrelec\.biz$/;

const DISTRELEC_HOST_HU = /^.*\.distrelec\.hu$/;

const PATH_ROOT_PATTERN = /^\/$/;

const USER_AGENT_HEADER = 'user-agent';

const REFRESH_DYNAMIC_MAPPING_INTERVAL = 3_600;

const URL_MATCH_EXPRESSION_ENDSWITH = 'ENDSWITH';

interface RequestRule {
  getCode: () => string;
  match: (req: Request) => boolean;
  getURL?: (req: Request) => string;
  getStatus: () => number;
}

interface DynamicMappingRule {
  shortURL: string;
  destinationURL: string;
  urlMatchExpression: string;
  permanent: boolean;
}

interface SiteDynamicMappingRules {
  site: string;
  validTimeStamp?: Date;
  dynamicRules: Array<DynamicMappingRule>;
}

export interface RuleAction {
  readonly code: string;
  readonly URL?: string;
  readonly status: number;
  readonly cache?: boolean;
}

const rules: Array<RequestRule> = [
  // curl "http://distrelec-ch.local:4200/ishopWebFront/catalog/product.do?productNr=123456"
  {
    getCode: () => 'Dist_RR_00001',
    match: (req) =>
      /(?:^|&)productNr=([0-9]{6})/.test(getQueryParams(req)) &&
      /^\/ishopWebFront\/catalog\/product\.do.*$/.test(req.path),
    getStatus: () => 301,
    getURL: (req) => {
      const result = getQueryParams(req).match('(?:^|&)productNr=([0-9]{6})');
      return '/p/' + result[1];
    },
  },
  // curl "http://distrelec-ch.local:4200/ishopWebFront/search/luceneSearch.do?dispatch=find"
  {
    getCode: () => 'Dist_RR_00002',
    match: (req) =>
      /(?:^|&)dispatch=find/.test(getQueryParams(req)) &&
      /^\/ishopWebFront\/search\/luceneSearch\.do.*$/.test(req.path),
    getStatus: () => 301,
    getURL: () => '/?',
  },
  // curl -H "User-agent: googlebot" http://distrelec-ch.local:4200/compare/metaCompareProducts
  {
    getCode: () => 'Dist_RR_00003',
    match: (req) =>
      /^.*ooglebot.*$/.test(req.get(USER_AGENT_HEADER)) && /^\/compare\/metaCompareProducts.*$/.test(req.path),
    getStatus: () => 301,
    getURL: () => '/?',
  },
  // curl -H "User-agent: googlebot" "http://distrelec-ch.local:4200/availability?productCodes=123456,654321"
  {
    getCode: () => 'Dist_RR_00004',
    match: (req) =>
      /^.*ooglebot.*$/.test(req.get(USER_AGENT_HEADER)) &&
      /(?:^|&)productCodes=.*/.test(getQueryParams(req)) &&
      /^\/availability.*$/.test(req.path),
    getStatus: () => 301,
    getURL: () => '/?',
  },
  // curl "http://distrelec-ch.local:4200/ishopWebFront/search/luceneSearch.do?dispatch=show&filterHierarchyNodeId=123456789"
  {
    getCode: () => 'Dist_RR_00005',
    match: (req) =>
      /(?:^|&)dispatch=show/.test(getQueryParams(req)) &&
      /(?:^|&)filterHierarchyNodeId=([0-9]*)/.test(getQueryParams(req)) &&
      /^\/ishopWebFront\/search\/luceneSearch\.do.*$/.test(req.path),
    getStatus: () => 301,
    getURL: (req) => {
      const result = getQueryParams(req).match('(?:^|&)filterHierarchyNodeId=([0-9]*)');
      return '/c/cat-' + result[1];
    },
  },
  // curl "http://distrelec-ch.local:4200/ishopWebFront/product/direct.do?nr=123456"
  {
    getCode: () => 'Dist_RR_00006',
    match: (req) =>
      /(?:^|&)nr=([0-9]{6})/.test(getQueryParams(req)) && /^\/ishopWebFront\/product\/direct\.do.*$/.test(req.path),
    getStatus: () => 301,
    getURL: (req) => {
      const result = getQueryParams(req).match('(?:^|&)nr=([0-9]{6})');
      return '/p/' + result[1];
    },
  },
  // curl -H "User-agent: baidu" http://distrelec-ch.local:4200/robots.txt
  // curl -H "User-agent: baidu" http://distrelec-ch.local:4200/cms/disclaimer
  {
    getCode: () => 'Dist_RR_00007',
    match: (req) => equalsIgnoringCase(req.get(USER_AGENT_HEADER), 'baidu') && !/^\/robots\.txt$/.test(req.path),
    getStatus: () => 403,
  },
  // curl -H "User-agent: semrush" http://distrelec-ch.local:4200/robots.txt
  // curl -H "User-agent: semrush" http://distrelec-ch.local:4200/cms/disclaimer
  {
    getCode: () => 'Dist_RR_00008',
    match: (req) => equalsIgnoringCase(req.get(USER_AGENT_HEADER), 'semrush') && !/^\/robots\.txt$/.test(req.path),
    getStatus: () => 403,
  },
  // curl http://distrelec-ch.local:4200/path/redeemVoucher/123456
  // curl http://distrelec-ch.local:4200/path/p/123456
  // curl http://distrelec-ch.local:4200/path/pf/123456
  // curl http://distrelec-ch.local:4200/path/subpath/123456
  {
    getCode: () => 'Dist_RR_00009',
    match: (req) =>
      !/^.*\/redeemVoucher\/.*$/.test(req.path) &&
      !/^.*\/p\/.*$/.test(req.path) &&
      !/^.*\/pf\/.*$/.test(req.path) &&
      /^\/.*\/([0-9]{6})$/.test(req.path),
    getStatus: () => 301,
    getURL: (req) => {
      const result = req.path.match('^/.*/([0-9]{6})$');
      return '/p/' + result[1];
    },
  },
  // curl "http://distrelec-ch.local:4200?lang=de&utm_campaign=NL1409_weblaunch_CH"
  // curl "http://distrelec-ch.local:4200?lang=DE&utm_campaign=NL1409_weblaunch_ch"
  {
    getCode: () => 'Dist_RR_00010',
    match: (req) => {
      const lang = req.query.lang;
      const campaign = req.query.utm_campaign;
      return (
        PATH_ROOT_PATTERN.test(req.path) &&
        equalsIgnoringCase(lang, 'de') &&
        equalsIgnoringCase(campaign, 'NL1409_weblaunch_CH')
      );
    },
    getStatus: () => 301,
    getURL: () => '/de//cms/newsletter_voucher_sales_ch_1410',
  },
  // curl "http://distrelec-ch.local:4200?lang=fr&utm_campaign=NL1409_weblaunch_CH"
  // curl "http://distrelec-ch.local:4200?lang=FR&utm_campaign=NL1409_weblaunch_ch"
  {
    getCode: () => 'Dist_RR_00011',
    match: (req) => {
      const lang = req.query.lang;
      const campaign = req.query.utm_campaign;
      return (
        PATH_ROOT_PATTERN.test(req.path) &&
        equalsIgnoringCase(lang, 'fr') &&
        equalsIgnoringCase(campaign, 'NL1409_weblaunch_CH')
      );
    },
    getStatus: () => 301,
    getURL: () => '/fr//cms/newsletter_voucher_sales_ch_1410',
  },
  // curl -H "Host: pretest.distrelec.ch" http://distrelec-ch.local:4200/cms/widerrufsbelehrung
  // curl -H "Host: pretest.distrelec.hu" http://distrelec-ch.local:4200/cms/widerrufsbelehrung
  {
    getCode: () => 'Dist_RR_00012',
    match: (req) => !DISTRELEC_HOST_HU.test(req.hostname) && '/cms/widerrufsbelehrung' === req.path,
    getStatus: () => 301,
    getURL: () => '/cms/disclaimer',
  },
  // curl -H "Host: pretest.distrelec.biz" http://distrelec-ch.local:4200/register/b2b
  // curl -H "Host: pretest.distrelec.ch" http://distrelec-ch.local:4200/register/b2b
  {
    getCode: () => 'Dist_RR_00013',
    match: (req) => DISTRELEC_HOST_BIZ.test(req.hostname) && /^\/register\/b2[bc]$/.test(req.path),
    getStatus: () => 301,
    getURL: () => '/registration?registerFrom=header',
  },
  // curl -H "Host: pretest.distrelec.biz" http://distrelec-ch.local:4200/register/existing
  // curl -H "Host: pretest.distrelec.ch" http://distrelec-ch.local:4200/register/existing
  {
    getCode: () => 'Dist_RR_00014',
    match: (req) => !DISTRELEC_HOST_BIZ.test(req.hostname) && /^\/register\/existing$/.test(req.path),
    getStatus: () => 301,
    getURL: () => '/registration?registerFrom=header',
  },
];

const siteDynamicRulesMap = new Map<string, SiteDynamicMappingRules>();

function equalsIgnoringCase(o1, o2) {
  if (o1 === undefined || o2 === undefined) {
    return false;
  }
  return o1.localeCompare(o2, undefined, { sensitivity: 'base' }) === 0;
}

function getQueryParams(req: Request): string {
  const n = req.originalUrl.lastIndexOf('?');
  return n !== -1 ? req.originalUrl.substring(n + 1) : '';
}

function createRuleAction(req: Request, match: RequestRule): RuleAction {
  if (isRedirect(match)) {
    return {
      code: match.getCode(),
      URL: match.getURL(req),
      status: match.getStatus(),
    };
  } else {
    return {
      code: match.getCode(),
      status: match.getStatus(),
    };
  }
}

function isRedirect(match: RequestRule) {
  const status = match.getStatus();
  return status === 301 || status === 302;
}

function createDynamicRuleAction(req: Request, dynamicMapping: DynamicMappingRule): RuleAction {
  let redirectUrl = dynamicMapping.destinationURL;
  if (isInternalRedirect(redirectUrl) && !hasContextParameters(dynamicMapping.destinationURL)) {
    // Take the language from the original URL or default to user's preferred language
    const languageContext = hasContextParameters(req.path) ? req.path.split('/')[1] : getPreferredLanguage(req);
    redirectUrl = `/${languageContext}${redirectUrl}`;
  }

  const redirectStatus = dynamicMapping.permanent ? 301 : 302;
  return {
    code: 'Dynamic mapping',
    URL: redirectUrl,
    status: redirectStatus,
  };
}

export async function getRuleAction(req: Request, apiUrl: string): Promise<RuleAction> {
  const match: RequestRule = rules.find((rule) => rule.match(req));
  if (match) {
    return createRuleAction(req, match);
  }
  const dynamicMatch: DynamicMappingRule = await getDynamicMappingRule(req, apiUrl);
  if (dynamicMatch) {
    return createDynamicRuleAction(req, dynamicMatch);
  }
  const contentPagePath: string = await shouldRedirectContentPageRequest(req, apiUrl);
  if (contentPagePath) {
    return {
      code: 'Content page',
      URL: contentPagePath,
      status: 302,
    };
  }
}

async function getDynamicMappingRule(req: Request, apiUrl: string): Promise<DynamicMappingRule> {
  const siteDynamicMappingRules: SiteDynamicMappingRules = await getSiteDynamicRules(req, apiUrl);
  if (siteDynamicMappingRules) {
    if (isDynamicMappingsNotSet(siteDynamicMappingRules)) {
      await getDynamicMappings(siteDynamicMappingRules, apiUrl);
    } else if (shouldRefreshDynamicMappings(siteDynamicMappingRules)) {
      getDynamicMappings(siteDynamicMappingRules, apiUrl);
    }
    if (siteDynamicMappingRules.dynamicRules) {
      return siteDynamicMappingRules.dynamicRules.find((mapping) => matchDynamicRule(req, mapping));
    }
  }
}

async function getSiteDynamicRules(req: Request, apiUrl: string): Promise<SiteDynamicMappingRules> {
  const siteDynamicMappingRules = siteDynamicRulesMap.get(req.hostname);
  if (!siteDynamicMappingRules) {
    return initializeSiteDynamicRules(req, apiUrl);
  }
  return siteDynamicMappingRules;
}

async function initializeSiteDynamicRules(req: Request, apiUrl: string): Promise<SiteDynamicMappingRules> {
  const siteId = await siteIdResolver.getSiteId(req, apiUrl);
  const newDynamicMappingRules: SiteDynamicMappingRules = {
    site: siteId,
    dynamicRules: [],
  };
  siteDynamicRulesMap.set(req.hostname, newDynamicMappingRules);
  return newDynamicMappingRules;
}

function isDynamicMappingsNotSet(siteDynamicMappingRules: SiteDynamicMappingRules): boolean {
  return !siteDynamicMappingRules.validTimeStamp;
}

function shouldRefreshDynamicMappings(siteDynamicMappingRules: SiteDynamicMappingRules): boolean {
  const duration: number = new Date().valueOf() - siteDynamicMappingRules.validTimeStamp.valueOf();
  return duration > REFRESH_DYNAMIC_MAPPING_INTERVAL;
}

async function getDynamicMappings(siteDynamicMappingRules: SiteDynamicMappingRules, apiUrl: string) {
  const requestURL = getMappingAPIUrl(siteDynamicMappingRules, apiUrl);

  const response = await fetch(requestURL);

  if (response.ok) {
    siteDynamicMappingRules.dynamicRules = (await response.json()) as DynamicMappingRule[];
    siteDynamicMappingRules.validTimeStamp = new Date();
  } else {
    console.log(
      `Error while fetching dynamic mappings for site ${siteDynamicMappingRules.site}: ${response.statusText}`,
    );
  }
}

function getMappingAPIUrl(siteDynamicMappingRules: SiteDynamicMappingRules, apiUrl: string) {
  return `${apiUrl}/rest/v2/${siteDynamicMappingRules.site}/mapping-rules`;
}

function matchDynamicRule(req: Request, mapping: DynamicMappingRule): boolean {
  if (URL_MATCH_EXPRESSION_ENDSWITH === mapping.urlMatchExpression) {
    return req.path.endsWith(mapping.shortURL);
  }
  return req.path === mapping.shortURL || stripContextFromPath(req.path) === mapping.shortURL;
}

function stripContextFromPath(path: string): string {
  return path.replace(CONTEXT_URL_PATTERN, '/');
}

async function shouldRedirectContentPageRequest(req: Request, apiUrl: string): Promise<string> {
  if (req.path.includes('/cms/')) {
    const contentPageUrl: string = await checkContentPagePath(req, apiUrl);
    if (contentPageUrl) {
      return contentPageUrl;
    }
  }
}

async function checkContentPagePath(req: Request, apiUrl: string): Promise<string> {
  const siteId = await siteIdResolver.getSiteId(req, apiUrl);
  const lang = language.getPreferredLanguage(req, siteId);
  const requestUrl = `${apiUrl}/rest/v2/${siteId}/content-page-url?cmsPagePath=${req.path}&lang=${lang}`;
  const response = await fetch(requestUrl);

  return response.ok ? response.text() : null;
}

function hasContextParameters(path: string) {
  return path.match(CONTEXT_URL_PATTERN);
}

function isInternalRedirect(path: string) {
  return path.startsWith('/');
}
