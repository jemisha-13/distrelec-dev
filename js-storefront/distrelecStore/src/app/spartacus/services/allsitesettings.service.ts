import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy, makeStateKey, TransferState } from '@angular/core';
import { BaseSiteService, LanguageService, OCC_USER_ID_CURRENT, OccEndpointsService, UserIdService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, ReplaySubject, Subscription } from 'rxjs';
import { distinctUntilChanged, filter, first, map, pluck, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { isEqual } from 'lodash-es';

import {
  Channel,
  CurrentSiteSettings,
  SalesOrg,
  SiteSettings,
  SiteSettingsResponseData,
} from '@model/site-settings.model';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';

import { mapBaseLanguageToSiteLanguage } from 'src/app/spartacus/site-context/utils';
import { FactFinderEnv } from '@model/factfinder.model';

@Injectable({
  providedIn: 'root',
})
export class AllsitesettingsService implements OnDestroy {
  siteData_: BehaviorSubject<SiteSettings[]> = new BehaviorSubject<SiteSettings[]>(null);
  currentChannelData$: BehaviorSubject<CurrentSiteSettings> = new BehaviorSubject<CurrentSiteSettings>(null);
  activeChannel$: Observable<Channel>;

  private subscriptions = new Subscription();
  private activeLanguage = 'en';
  private activeCountry = '';
  private activeChannel = this.activeChannelService.getDefaultValue();
  private ALLSITE_SETTINGS_KEY = makeStateKey<SiteSettingsResponseData>('all-site-settings');

  private sharedAllSiteSettingsRequest = new ReplaySubject<SiteSettingsResponseData>(1);
  private isInitialised = false;

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private baseSiteService: BaseSiteService,
    private activeChannelService: ChannelService,
    private languageService: LanguageService,
    private countryService: CountryService,
    private transferState: TransferState,
    private winRef: WindowRef,
    private userIdService: UserIdService,
  ) {}

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  init() {
    if (this.isInitialised) {
      return;
    }

    this.activeChannel$ = this.activeChannelService.getActive();
    this.activeChannel$.subscribe((activeChannel) => {
      this.activeChannel = activeChannel;
      // Update on channel change
      const currentSettings = this.currentChannelData$.getValue();
      if (currentSettings) {
        this.setCurrentChannelObject(
          activeChannel,
          currentSettings.language,
          currentSettings.country,
          currentSettings.currency,
          currentSettings.domain,
          currentSettings.mediaDomain,
          currentSettings.storefrontDomain,
        );
      }
    });

    if (this.transferState.hasKey(this.ALLSITE_SETTINGS_KEY)) {
      this.sharedAllSiteSettingsRequest.next(
        this.transferState.get<SiteSettingsResponseData>(this.ALLSITE_SETTINGS_KEY, null),
      );
    } else {
      this.resetSiteSettingsRequest();
    }

    this.subscriptions.add(
      this.languageService.getActive().subscribe((activeLanguage) => (this.activeLanguage = activeLanguage)),
    );

    this.subscriptions.add(
      this.countryService.getActive().subscribe((countryCode) => {
        this.activeCountry = countryCode;
      }),
    );

    this.subscriptions.add(
      this.getSiteSettings().subscribe((allSettings: SiteSettings[]) => {
        this.siteData_.next(allSettings);
        const activeSiteSettings = allSettings.find((settings) =>
          settings.country.entry.some((entry) => entry.key === this.activeCountry),
        );
        if (activeSiteSettings) {
          this.setCurrentChannelObject(
            this.activeChannel,
            this.activeLanguage,
            activeSiteSettings.country.entry[0].key,
            activeSiteSettings.currencies.entry[0].key,
            activeSiteSettings.domain,
            activeSiteSettings.mediaDomain,
            activeSiteSettings.storefrontDomain,
          );
        }
      }),
    );

    this.isInitialised = true;
  }

  resetSiteSettingsRequest() {
    this.fetchSiteSettings()
      .pipe(first())
      .subscribe((data) => {
        this.sharedAllSiteSettingsRequest.next(data);
      });
  }

  getAllSiteSettings$(): Observable<SiteSettingsResponseData> {
    return this.sharedAllSiteSettingsRequest.asObservable();
  }

  factFinderEnvironment(): Observable<FactFinderEnv> {
    return this.sharedAllSiteSettingsRequest.pipe(
      filter((item) => item.hasOwnProperty('factFinderSearchExpose')),
      pluck('factFinderSearchExpose'),
      distinctUntilChanged(isEqual),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getDecimalCommaCountries(): Observable<string[]> {
    return this.sharedAllSiteSettingsRequest.pipe(
      filter((item) => item.hasOwnProperty('decimalCommaCountries')),
      pluck('decimalCommaCountries'),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getCurrentSalesOrg(): Observable<SalesOrg> {
    return this.sharedAllSiteSettingsRequest.pipe(
      filter((item) => item.hasOwnProperty('salesOrg')),
      pluck('salesOrg'),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getSiteSettings(): Observable<SiteSettings[]> {
    return this.sharedAllSiteSettingsRequest.pipe(
      filter((item) => item.hasOwnProperty('settings')),
      pluck('settings'),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getTrackingEnabled(): Observable<boolean> {
    return this.sharedAllSiteSettingsRequest.pipe(
      filter((item) => item.hasOwnProperty('ymktTrackingEnabled')),
      pluck('ymktTrackingEnabled'),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getWebshopSettings(isocode): SiteSettings {
    let countryData;
    this.siteData_.getValue()?.forEach((country) => {
      if (country?.country?.entry[0].key === isocode) {
        countryData = country;
      }
    });
    return countryData;
  }

  setCurrentChannelObject(channel, language, country, currency, domain, mediaDomain, storefrontDomain) {
    this.currentChannelData$.next({
      channel,
      language,
      country,
      currency: currency.toUpperCase(),
      domain,
      mediaDomain,
      storefrontDomain,
    });
  }

  getLocale(): string {
    return mapBaseLanguageToSiteLanguage(this.activeLanguage, this.activeCountry);
  }

  getCurrentChannelData(): Observable<CurrentSiteSettings> {
    return this.currentChannelData$
      .asObservable()
      .pipe(filter<CurrentSiteSettings>(Boolean), distinctUntilChanged(isEqual));
  }

  getDomain(): Observable<string> {
    return this.getSetting('domain');
  }

  getMediaDomain(): Observable<string> {
    return this.getSetting('mediaDomain');
  }

  private fetchSiteSettings(): Observable<SiteSettingsResponseData> {
    return combineLatest([this.baseSiteService.getActive(), this.userIdService.takeUserId()]).pipe(
      take(1),
      switchMap(([baseSite, userId]) => {
        const url = userId !== OCC_USER_ID_CURRENT ? `/allsitesettings` : `/allsitesettings/mutable`;
        return this.http.get<SiteSettingsResponseData>(this.occEndpoints.buildUrl(url), {
          headers: { 'Cache-Control': 'no-cache' },
        });
      }),
      tap((data) => {
        if (!this.winRef.isBrowser()) {
          this.transferState.set<any>(this.ALLSITE_SETTINGS_KEY, data);
        }
      }),
    );
  }

  private getSetting(setting: keyof CurrentSiteSettings): Observable<string> {
    return this.currentChannelData$.pipe(
      filter<CurrentSiteSettings>(Boolean),
      map((channelData) => channelData[setting]),
    );
  }
}
