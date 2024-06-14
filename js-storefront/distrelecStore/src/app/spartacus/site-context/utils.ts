import { BASE_SITE_CONTEXT_ID, BaseStore, getContextParameterDefault, SiteContextConfig } from '@spartacus/core';
import { SiteIdEnum } from '@model/site-settings.model';

export function mapSiteLanguageToBaseLanguage(language: string): string {
  // de_CH => de
  return language?.split('_')[0];
}

export function mapBaseLanguageToSiteLanguage(languageIso: string, countryIso?: string): string {
  // de => de_CH
  if (!countryIso) {
    return languageIso;
  }
  return `${languageIso}_${countryIso.toUpperCase()}`;
}

export function getCountryContextParameterDefault(config: SiteContextConfig, baseStore: BaseStore): string {
  const baseSiteId = getContextParameterDefault(config, BASE_SITE_CONTEXT_ID);
  if (baseSiteId === SiteIdEnum.EX) {
    return 'EX';
  }

  const deliveryCountries: { isocode: string }[] = (baseStore as any).deliveryCountries;
  return deliveryCountries[0].isocode;
}

export function isInternationalShop(baseSiteId: string): boolean {
  return baseSiteId === SiteIdEnum.EX || baseSiteId === 'distrelec';
}

export function getActiveBaseSite(config: SiteContextConfig): string {
  return getContextParameterDefault(config, BASE_SITE_CONTEXT_ID);
}

export function isActiveSiteInternational(config: SiteContextConfig): boolean {
  const baseSite = getActiveBaseSite(config);
  return isInternationalShop(baseSite);
}

export function isSwitzerlandShop(baseSiteId: string): boolean {
  return baseSiteId === SiteIdEnum.CH;
}

export function isActiveSiteSwitzerland(config: SiteContextConfig): boolean {
  const baseSite = getActiveBaseSite(config);
  return isSwitzerlandShop(baseSite);
}

export function isItalyShop(baseSiteId: string): boolean {
  return baseSiteId === SiteIdEnum.IT;
}

export function isActiveSiteItaly(config: SiteContextConfig): boolean {
  const baseSite = getActiveBaseSite(config);
  return isItalyShop(baseSite);
}
