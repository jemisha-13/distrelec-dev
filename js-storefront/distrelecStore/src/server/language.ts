import { Request } from 'express';

import { LANGUAGE_MAPPINGS, VALID_LANGUAGES } from './constants';

function getCookieLanguage(req: Request) {
  const contextCookie = req.cookies.siteContext;
  if (!contextCookie) {
    return undefined;
  }
  return JSON.parse(contextCookie).lang;
}

function isValidLanguage(preferredLanguage: string, siteId: string): boolean {
  /*
   For countries with single language that cant be resolved by cookie or site context
   we can provide mapping for these countries seeing as they wont change
  */

  const site: string = siteId === 'distrelec' ? 'int' : siteId.split('_')[1].toLowerCase();
  return LANGUAGE_MAPPINGS[site].includes(preferredLanguage);
}

function getDefaultLanguage(siteId: string): string {
  const language = siteId.split('_')[1].toLowerCase();
  return LANGUAGE_MAPPINGS[language][0];
}

function getHttpHeaderLanguage(req: Request) {
  return req.headers['accept-language']?.split(',')?.[0]?.substring(0, 2);
}

export function getRouteLanguage(req: Request): string | undefined {
  const routeLang: string = req.url.split('/')[1].toLowerCase();
  return VALID_LANGUAGES.includes(routeLang) ? routeLang : undefined;
}

export function getPreferredLanguage(req: Request, siteId?: string) {
  const preferredLanguage = getRouteLanguage(req) ?? getCookieLanguage(req) ?? getHttpHeaderLanguage(req) ?? '';

  if (siteId && !isValidLanguage(preferredLanguage, siteId)) {
    return getDefaultLanguage(siteId);
  }

  return preferredLanguage;
}
