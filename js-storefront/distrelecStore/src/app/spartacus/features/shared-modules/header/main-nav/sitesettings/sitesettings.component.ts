import { Component, OnDestroy, OnInit } from '@angular/core';
import { EventService, LanguageService, User, UserIdService, WindowRef, createFrom } from '@spartacus/core';
import { faCaretDown, faTimes } from '@fortawesome/free-solid-svg-icons';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Channel, SiteSettings } from '@model/site-settings.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { DistrelecUserService } from '@services/user.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { HeaderInteractionEvent } from '@features/tracking/events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';

@Component({
  selector: 'app-site-settings',
  templateUrl: './sitesettings.component.html',
  styleUrls: ['./sitesettings.component.scss', './sitesettings-flags.scss'],
})
export class SiteSettingsComponent implements OnInit, OnDestroy {
  faTimes = faTimes;
  faCaretDown = faCaretDown;
  activeIndex: number;
  initialIndex: number; // this doesn't get changed only set on page load
  siteData?: SiteSettings[];
  activeChannel$: Observable<Channel> = this.siteSettingsService.activeChannel$;

  activeCountry = '';
  activeLanguage = 'en';
  defaultLanguageSelection = 'en';
  radioLanguageSelected = 'en';
  lastActiveChannel: Channel;
  activeChannelSet: Channel;
  public activeLanguageFull: Observable<string> = this.languageService.getActive().pipe(
    map((code: string) => {
      for (const data of this.siteData[this.activeIndex]?.languages?.entry) {
        if (data.key === code) {
          return data.value;
        }
      }
    }),
  );
  public activeCountryKey = `countries.country_distrelec_${this.activeCountry}`;

  userId$: Observable<string> = this.userIdService.getUserId();
  activeCurrency$: Observable<string> = combineLatest([this.userId$, this.distrelecUserService.userDetails_]).pipe(
    map(([userId, userData]) => this.getActiveCurrency(userId, userData)),
  );

  private readonly subscriptions = new Subscription();

  constructor(
    private siteSettingsService: AllsitesettingsService,
    private winRef: WindowRef,
    private languageService: LanguageService,
    private channelService: ChannelService,
    private countryService: CountryService,
    private userIdService: UserIdService,
    private distrelecUserService: DistrelecUserService,
    private siteConfigService: SiteConfigService,
    private slideDrawerService: SlideDrawerService,
    private eventService: EventService,
  ) {}

  ngOnInit(): void {
    this.siteSettingsService
      .getSiteSettings() // Data won't change over the lifetime of the app, so we only need to get it once.
      .subscribe((allSettings) => {
        this.siteData = allSettings;

        // Language and country can change after login use subscriptions
        this.subscriptions.add(
          this.languageService.getActive().subscribe((activeLanguage) => {
            this.activeLanguage = activeLanguage;
            this.radioLanguageSelected = activeLanguage;
          }),
        );

        this.subscriptions.add(
          this.countryService.getActive().subscribe((countryIsoCode) => {
            this.activeIndex = allSettings.findIndex((settings) =>
              settings.country.entry.some((entry) => entry.key === countryIsoCode),
            );
            if (!this.initialIndex) {
              this.initialIndex = this.activeIndex;
            }

            this.setDefaultLanguageSelection(false);
            this.setActiveCountryName(countryIsoCode);
          }),
        );
      });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  submit(e, country): void {
    e.preventDefault();
    if (this.activeChannelSet) {
      this.channelService.setActive(this.activeChannelSet);
    }
    this.switchSite(country);
    this.dispatchHeaderInteractionEvent(Ga4HeaderInteractionEventType.HEADER_SETTINGS_UPDATE_CLICK);
  }

  channelSelected(channel: Channel): void {
    this.lastActiveChannel = this.activeChannelSet;
    this.activeChannelSet = channel.toUpperCase() as Channel;
  }

  languageSelected(event: Event) {
    this.radioLanguageSelected = (event.target as HTMLInputElement).value;
  }

  switchSite(country): void {
    let updatedCountry;
    const pageTemplate = this.siteConfigService.getCurrentPageTemplate();
    const selectedCountry = country.country.entry[0].key;
    if (this.activeCountry !== selectedCountry) {
      updatedCountry = selectedCountry;
      this.countryService.setActive(updatedCountry);
    }

    this.languageService.setActive(this.radioLanguageSelected);

    // cxTranslate keys will change immediately, but we must refresh for CMS content
    let url: URL;

    if (this.initialIndex !== this.activeIndex) {
      url = new URL(country.domain);
      url.pathname = this.radioLanguageSelected;
    } else {
      url = new URL(this.winRef.location.href);
    }

    if (updatedCountry && updatedCountry !== 'otherCountries') {
      url.searchParams.set('country', updatedCountry);
    }

    this.winRef.location.href = url.toString();
  }

  isFrance(selectedCountry: string) {
    return selectedCountry === 'FR';
  }

  setDefaultLanguageSelection(isDropdownSelection: boolean): void {
    const selectedCountryData = this.siteData[this.activeIndex];

    if (selectedCountryData.languages && selectedCountryData.languages.entry) {
      this.defaultLanguageSelection = selectedCountryData.languages.entry[0].key;

      for (const entry of selectedCountryData.languages.entry) {
        if (this.activeLanguage === entry.key) {
          this.defaultLanguageSelection = this.activeLanguage;
          break;
        }
      }
      if (isDropdownSelection) {
        for (const entry of selectedCountryData.country.entry) {
          if ('LI' === entry.key) {
            this.defaultLanguageSelection = 'en';
            break;
          }
        }
      }

      this.radioLanguageSelected = this.defaultLanguageSelection;
    }
  }

  checkSelectedLanguage(language: string): boolean {
    return language === this.defaultLanguageSelection;
  }

  openPanelAndDispatchEvent(event, id): void {
    this.slideDrawerService.openPanel(event, id);
    this.dispatchHeaderInteractionEvent(Ga4HeaderInteractionEventType.HEADER_CLICK_SETTINGS);
  }

  closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  getActiveCurrency(userId: string, userData: User): string {
    if (userId === 'current' && userData?.currency) {
      return userData.currency.isocode;
    }
    return this.siteData[this.initialIndex]?.currencies?.entry[0]?.key;
  }

  private dispatchHeaderInteractionEvent(type: Ga4HeaderInteractionEventType): void {
    this.eventService.dispatch(createFrom(HeaderInteractionEvent, { type }));
  }

  private setActiveCountryName(isoCode: string) {
    this.activeCountry = isoCode;
    this.activeCountryKey = `countries.country_distrelec_${isoCode}`;
  }
}
