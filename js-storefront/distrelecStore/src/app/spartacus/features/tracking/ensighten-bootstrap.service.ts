import { Injectable } from '@angular/core';
import { BaseSiteService, LanguageService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { combineLatest } from 'rxjs';
import { first } from 'rxjs/operators';
import { ScriptService } from '@services/script.service';
import { CountryService } from '../../site-context/services/country.service';
import { SmartEditLauncherService } from '@spartacus/smartedit/root';

const ENSIGHTEN_BOOTSTRAP_CONFIG_ID = 'ensighten-bootstrap-config';

export enum DistOCCBackendApi {
  LOCAL = 'https://localhost:9002/',
  DEV_DIST = 'https://dev.api.distrelec.com/',
  DEV_RAD = 'https://dev.api.rad.distrelec.com/',
  DEV2_DIST = 'https://dev2.api.distrelec.com/',
  DEV2_RAD = 'https://dev2.api.rad.distrelec.com/',
  PRETEST_DIST = 'https://pretest.api.distrelec.com/',
  PRETEST_RAD = 'https://pretest.api.rad.distrelec.com/',
  TEST_DIST = 'https://test.api.distrelec.com/',
  TEST_RAD = 'https://test.api.rad.distrelec.com/',
  PROD_DIST = 'https://api.distrelec.com/',
  PROD_RAD = 'https://api.rad.distrelec.com/',
}

export enum EnsightenBootstrapUrl {
  DEFAULT = '//ensighten.distrelec.com/distrelec/headless/Bootstrap.js',
  STAGING = '//ensighten.distrelec.com/distrelec/headless_staging/Bootstrap.js',
  PROD = '//ensighten.distrelec.com/distrelec/headlessprod/Bootstrap.js',
}
@Injectable({
  providedIn: 'root',
})
export class EnsightenBootstrapService {
  readonly ensightenEnvMappings = new Map<DistOCCBackendApi, EnsightenBootstrapUrl>([
    [DistOCCBackendApi.LOCAL, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.DEV_DIST, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.DEV2_DIST, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.PRETEST_DIST, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.TEST_DIST, EnsightenBootstrapUrl.STAGING],
    [DistOCCBackendApi.PROD_DIST, EnsightenBootstrapUrl.PROD],
    [DistOCCBackendApi.DEV_RAD, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.DEV2_RAD, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.PRETEST_RAD, EnsightenBootstrapUrl.DEFAULT],
    [DistOCCBackendApi.TEST_RAD, EnsightenBootstrapUrl.STAGING],
    [DistOCCBackendApi.PROD_RAD, EnsightenBootstrapUrl.PROD],
  ]);

  constructor(
    private countryService: CountryService,
    private languageService: LanguageService,
    private baseSiteService: BaseSiteService,
    private winRef: WindowRef,
    private scriptService: ScriptService,
    private occEndPointsService: OccEndpointsService,
    private smartEditLauncherService: SmartEditLauncherService,
  ) {}

  init(): Promise<void> {
    if (this.smartEditLauncherService.isLaunchedInSmartEdit()) {
      return Promise.resolve();
    }

    if (this.isConfigInjected()) {
      this.injectBootstrapScript();
      return Promise.resolve();
    }

    return this.initServer().then(() => {
      this.injectBootstrapScript();
    });
  }

  initServer(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        combineLatest(this.countryService.getActive(), this.languageService.getActive(), this.baseSiteService.get())
          .pipe(first())
          .subscribe(([country, lang, site]) => {
            const baseStoreLanguageEN = (site.baseStore.languages.find((language) => language.isocode === lang) as any)
              ?.nameEN;
            const defaultLanguageEN = site.defaultLanguage.name;
            const languageEN = baseStoreLanguageEN ?? defaultLanguageEN;

            this.injectPageDetails(country, languageEN.toLowerCase());
            resolve();
          });
      } catch (e) {
        console.error(e);
        reject();
      }
    });
  }

  private isConfigInjected(): boolean {
    return this.winRef.document.getElementById(ENSIGHTEN_BOOTSTRAP_CONFIG_ID) !== null;
  }

  private injectPageDetails(countryCode: string, languageEN: string) {
    // Required fields in Ensighten Privacy custom JS implementation
    this.scriptService.appendScript({
      id: ENSIGHTEN_BOOTSTRAP_CONFIG_ID,
      innerHTML: `
        window.digitalData = {
          page: {
            pageInfo: {
              language: '${languageEN}',
              countryCode: '${countryCode}'
            }
          }
        }
      `,
    });
  }

  get ensightenEnvBootstrapUrl() {
    return this.ensightenEnvMappings.get(
      this.occEndPointsService.getBaseUrl().split('rest/v2')[0] as DistOCCBackendApi,
    );
  }

  private injectBootstrapScript() {
    this.scriptService.appendScript({
      src: this.ensightenEnvBootstrapUrl ?? EnsightenBootstrapUrl.PROD,
      async: true,
    });
  }
}
