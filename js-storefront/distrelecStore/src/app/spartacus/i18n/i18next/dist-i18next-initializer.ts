import { Inject, Injectable } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import {
  I18nConfig,
  I18NEXT_INSTANCE,
  I18nextBackendService,
  I18nextInitializer,
  LanguageService,
} from '@spartacus/core';
import { i18n } from 'i18next';
import { combineLatest } from 'rxjs';
import { CountryService } from '@context-services/country.service';
import { mapBaseLanguageToSiteLanguage } from '../../site-context/utils';
import { getLocaleImport } from '../locale-importer';

@Injectable({ providedIn: 'root' })
export class DistI18nextInitializer extends I18nextInitializer {
  constructor(
    @Inject(I18NEXT_INSTANCE) protected i18next: i18n,
    protected config: I18nConfig,
    protected languageService: LanguageService,
    protected i18nextBackendService: I18nextBackendService,
    protected countryService: CountryService,
  ) {
    super(i18next, config, languageService, i18nextBackendService);
  }

  protected override synchronizeLanguage() {
    this.subscription =
      this.subscription ??
      combineLatest([this.languageService.getActive(), this.countryService.getActive()]).subscribe(
        ([lang, country]) => {
          this.i18next.changeLanguage(mapBaseLanguageToSiteLanguage(lang, country));
          this.loadLocale(lang, country);
        },
      );
  }

  private loadLocale(lang: string, country: string) {
    const localeModule = getLocaleImport(lang, country);
    if (localeModule) {
      localeModule().then((localeData) => {
        registerLocaleData(localeData.default);
      });
    }
  }
}
