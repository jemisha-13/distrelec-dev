import { Injectable, makeStateKey, TransferState } from '@angular/core';
import { Observable, of, ReplaySubject } from 'rxjs';
import { getContextParameterValues, SiteContext, SiteContextConfig, WindowRef } from '@spartacus/core';
import { environment } from '@environment';
import { COUNTRY_CONTEXT_ID } from '../providers/custom-context-ids';

import { filter } from 'rxjs/operators';

export enum CountryCodesEnum {
  AUSTRIA = 'AT',
  BELGIUM = 'BE',
  SWITZERLAND = 'CH',
  LIECHTENSTEIN = 'LI',
  CZECH_REPUBLIC = 'CZ',
  GERMANY = 'DE',
  DENMARK = 'DK',
  ESTONIA = 'EE',
  EXPORT = 'EX',
  FINLAND = 'FI',
  FRANCE = 'FR',
  HUNGARY = 'HU',
  ITALY = 'IT',
  VATICAN = 'VA',
  SAN_MARINO = 'SM',
  LITHUANIA = 'LT',
  LATVIA = 'LV',
  NETHERLANDS = 'NL',
  NORWAY = 'NO',
  POLAND = 'PL',
  ROMANIA = 'RO',
  SWEDEN = 'SE',
  SLOVAKIA = 'SK',
}

export enum CountriesWithFunctionCode {
  FRANCE = 'FR',
  NETHERLANDS = 'NL',
  BELGIUM = 'BE',
}
export enum BIZCountryCodesEnum {
  GR = 'GR',
  EX = 'EX',
  BG = 'BG',
  HR = 'HR',
  CY = 'CY',
  EL = 'EL',
  IE = 'IE',
  LU = 'LU',
  MT = 'MT',
  PT = 'PT',
  SI = 'SI',
  ES = 'ES',
  GREAT_BRITAIN = 'GB',
  NORTHERN_IRELAND = 'XI',
}

export const validCountries = [
  // Delivery Countries connected to base stores
  'AT',
  'BE',
  'CH',
  'LI',
  'CZ',
  'DE',
  'DK',
  'EE',
  'FI',
  'FR',
  'HU',
  'IT',
  'VA',
  'SM',
  'LT',
  'LV',
  'NL',
  'NO',
  'PL',
  'RO',
  'SE',
  'SK',
  'EX',
];

export const validCountriesForInternationalShop = [
  'GR',
  'EX',
  'BG',
  'CY',
  'EL',
  'ES',
  'GB',
  'HR',
  'IE',
  'LU',
  'MT',
  'PT',
  'SI',
  'XI',
];

@Injectable({
  providedIn: 'root',
})
export class CountryService implements SiteContext<string> {
  private COUNTRY_KEY = makeStateKey<string>('country-key');
  private readonly activeCountry$ = new ReplaySubject<string>(1);

  constructor(
    protected config: SiteContextConfig,
    protected winRef: WindowRef,
    private transferState: TransferState,
  ) {
    if (this.transferState.hasKey(this.COUNTRY_KEY)) {
      const stateValue = this.transferState.get<string>(this.COUNTRY_KEY, null);
      this.activeCountry$.next(stateValue);
    }
  }

  setActive(countryCode: string) {
    if (this.isValid(countryCode)) {
      this.activeCountry$.next(countryCode);
      if (!this.winRef.isBrowser()) {
        this.transferState.set<string>(this.COUNTRY_KEY, countryCode);
      }
    }
  }

  getActive(): Observable<string> {
    return this.activeCountry$.asObservable().pipe(filter<string>(Boolean));
  }

  getAll(): Observable<string[]> {
    return of(getContextParameterValues(this.config, COUNTRY_CONTEXT_ID));
  }

  isExportShopCountry(countryCode: string): boolean {
    return Object.values(BIZCountryCodesEnum).some((bizCountryCode) => bizCountryCode === countryCode);
  }

  private isValid(countryCode: string): boolean {
    if (!getContextParameterValues(this.config, COUNTRY_CONTEXT_ID).includes(countryCode)) {
      if (!environment.production) {
        console.warn(`Country ${countryCode} is not valid for the current base store`);
      }
      return false;
    }

    return true;
  }
}
