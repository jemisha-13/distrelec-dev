import { Injectable, OnDestroy } from '@angular/core';
import {
  BaseSite,
  BaseSiteService,
  ConfigInitializerService,
  getContextParameterValues,
  SiteContextConfig,
  WindowRef,
} from '@spartacus/core';
import { of, Subscription, zip } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { DistCookieService } from '@services/dist-cookie.service';

import { CountryService } from '../services/country.service';
import { COUNTRY_CONTEXT_ID } from '../providers/custom-context-ids';
import { getCountryContextParameterDefault } from '../utils';

@Injectable({ providedIn: 'root' })
export class CountryInitializer implements OnDestroy {
  protected subscription: Subscription;

  constructor(
    private countryService: CountryService,
    private configInit: ConfigInitializerService,
    private cookieService: DistCookieService,
    private baseSiteService: BaseSiteService,
    private winRef: WindowRef,
  ) {}

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  initialize(): void {
    this.subscription = this.configInit
      .getStable('context.baseSite')
      .pipe(
        switchMap((config: SiteContextConfig) => zip(of(config), this.baseSiteService.get())),
        tap(([config, baseSite]) => {
          this.setDefault(config, baseSite);
        }),
      )
      .subscribe();
  }

  protected setDefault(config: SiteContextConfig, baseSite: BaseSite): void {
    const contextParam = this.getValidCountry(config, baseSite);
    this.countryService.setActive(contextParam);
  }

  private getCookieContext(): string | undefined {
    try {
      return JSON.parse(this.cookieService.get('siteContext')).country;
    } catch {
      return undefined;
    }
  }

  private getUrlContext(): string | undefined {
    const queryParams = new URL(this.winRef.location.href).searchParams;

    if (queryParams.has('country')) {
      return queryParams.get('country').toUpperCase();
    }

    return undefined;
  }

  /**
   * Priority for setting initial context values is
   * 1. URL query param (override and set cookie) -- only way to set values across domains
   * 2. Cookie (previously set value)
   * 3. BaseStore default value
   *
   * If we have a value for 1 or 2, but it is not a valid value for the current BaseStore then the default value is used
   */
  private getValidCountry(config: SiteContextConfig, baseSite: BaseSite): string {
    const validCountries = getContextParameterValues(config, COUNTRY_CONTEXT_ID);

    const selectedCountry = this.getUrlContext() ?? this.getCookieContext();

    if (selectedCountry && validCountries.includes(selectedCountry)) {
      return selectedCountry;
    }
    return getCountryContextParameterDefault(config, baseSite.baseStore);
  }
}
