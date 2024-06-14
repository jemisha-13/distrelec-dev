import { CountryCodesEnum } from '@context-services/country.service';
import { FusionConfig } from '@features/pages/product/core/fusion/model/fusion-config.model';
import { FactFinderEnv } from '@model/factfinder.model';
import { EntryList } from '@model/misc.model';

export type Channel = 'B2B' | 'B2C';

export interface SiteSettingsResponseData {
  settings: SiteSettings[];
  salesOrg: SalesOrg;
  decimalCommaCountries: string[];
  factFinderSearchExpose: FactFinderEnv;
  fusionSearchExpose: FusionConfig;
  ymktTrackingEnabled: boolean;
}

export enum CustomerType {
  B2B = 'B2B',
  B2B_KEY_ACCOUNT = 'B2B_KEY_ACCOUNT',
  B2C = 'B2C',
  B2E = 'B2E',
  GUEST = 'GUEST',
}

export interface SiteSettings {
  domain: string;
  mediaDomain: string;
  storefrontDomain?: string;
  channels: EntryList<Channel, string>;
  country: EntryList<string, string>;
  currencies: EntryList<string, string>;
  languages: EntryList<string, string>;
}

export interface CurrentSiteSettings {
  channel: Channel;
  country: CountryCodesEnum;
  currency: string;
  language: string;
  domain: string;
  mediaDomain: string;
  storefrontDomain?: string;
}

export interface SalesOrg {
  code: string;
  brand: string;
  nativeName: string;
  countryIsocode: string;
  erpSystem: string;
}

export enum CountryDateFormatEnum {
  AT = 'dd/MM/yy',
  BE = 'dd/MM/yy',
  CZ = 'MM/dd/yy',
  DE = 'dd.MM.yyyy',
  DK = 'dd/MM/yy',
  CH_en = 'dd/MM/yyyy',
  CH_de = 'dd.MM.yyyy',
  CH_fr = 'dd/MM/yy',
  EE = 'dd.MM.yyyy',
  FI = 'dd.MM.yyyy',
  FR = 'dd/MM/yy',
  HU = 'yyyy.MM.dd',
  IT = 'dd/MM/yy',
  SM = 'dd/MM/yy',
  VA = 'dd/MM/yy',
  LT = 'yy-MM-dd',
  LV = 'dd.MM.yyyy',
  NL = 'dd/MM/yy',
  NO = 'dd.MM.yyyy',
  PL = 'dd.MM.yy',
  RO = 'dd.MM.yy',
  SK = 'dd/MM/yy',
  SE = 'yyyy-MM-dd',
  EX = 'dd/MM/yy',
}

export enum LongDateFormatEnum {
  AT = 'dd.MM.yyyy',
  BE = 'dd/MM/yyyy',
  CZ = 'MM/dd/yyyy',
  DE = 'dd.MM.yyyy',
  DK = 'dd/MM/yyyy',
  CH_en = 'dd/MM/yyyy',
  CH_de = 'dd.MM.yyyy',
  CH_fr = 'dd/MM/yyyy',
  EE = 'dd.MM.yyyy',
  FI_fi = 'dd.MM.yyyy',
  FI_sv = 'yyyy-MM-dd',
  FR = 'dd/MM/yyyy',
  HU = 'yyyy.MM.dd',
  IT = 'dd/MM/yyyy',
  SM = 'dd/MM/yyyy',
  VA = 'dd/MM/yyyy',
  LT = 'yyyy-MM-dd',
  LV = 'dd.MM.yyyy',
  NL = 'dd/MM/yyyy',
  NO = 'dd.MM.yyyy',
  PL_pl = 'dd.MM.yyyy',
  PL_en = 'dd/MM/yyyy',
  RO = 'dd.MM.yyyy',
  SK = 'dd/MM/yyyy',
  SE = 'yyyy-MM-dd',
  EX = 'dd/MM/yyyy',
}

export enum SiteIdEnum {
  CH = 'distrelec_CH',
  DE = 'distrelec_DE',
  ET = 'distrelec_EE',
  EX = 'distrelec_EX',
  FR = 'distrelec_FR',
  IT = 'distrelec_IT',
  LV = 'distrelec_LV',
  BELGIUM = 'distrelec_BE',
  NETHERLANDS = 'distrelec_NL',
}
