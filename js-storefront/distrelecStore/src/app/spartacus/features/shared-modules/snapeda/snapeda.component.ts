import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { snap_eda_token } from '@helpers/auth-tokens';
import { AuthService, WindowRef } from '@spartacus/core';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { Subscription } from 'rxjs';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-snap-eda',
  templateUrl: './snapeda.component.html',
  styleUrls: ['./snapeda.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SnapedaComponent implements OnInit, OnDestroy {
  @Input() isAvailableInSnapEda: boolean;
  @Input() mpn: string;
  @Input() manufacturerName: string;
  @Input() isBetterWorldShown: boolean;

  userId = '';
  isoCode = '';
  currentChannelData: CurrentSiteSettings;

  private subscription: Subscription = new Subscription();

  constructor(
    private countryService: CountryService,
    private winRef: WindowRef,
    private authService: AuthService,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  ngOnInit() {
    this.subscription.add(
      this.countryService.getActive().subscribe((isoCode) => {
        this.isoCode = isoCode;
      }),
    );

    this.subscription.add(
      this.siteSettingsService.currentChannelData$.subscribe((data: CurrentSiteSettings) => {
        this.currentChannelData = data;

        if (this.isAvailableInSnapEda && this.currentChannelData) {
          this.getSnapEdaData();
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getSnapEdaData(): void {
    const script = this.winRef.document.createElement('script');
    script.type = 'text/javascript';
    script.src = `https://www.snapeda.com/distributor/files/plugins/snapeda/snapeda.min.js?token=${snap_eda_token}`;
    script.setAttribute('lang', this.getCurrentLanguage());
    script.setAttribute('token', snap_eda_token); //token needed for api its hardcoded
    script.setAttribute('container', '#snap-eda-container'); //place where button is injected
    script.setAttribute('autoload', '');
    script.setAttribute('manufacturer', this.manufacturerName); //params so it knows what manufacturer name to display
    script.setAttribute('part', this.mpn); //params so it knows what manufacturer data to load
    script.setAttribute('isCustomerLoggedIn', this.isCurrentUserLoggedIn() ? '1' : '0'); // param to tell snapeda if our customer is logged in or not (0 is logged out)
    script.setAttribute('loginUrl', this.getLoginUrlForCurrentSite()); // param to tell snapeda the login url for the current site user is on

    this.winRef.document.body.appendChild(script);
  }

  private isCurrentUserLoggedIn(): boolean {
    let isCurrentUserLoggedIn = false;
    this.authService.isUserLoggedIn().subscribe((isUserLoggedIn) => (isCurrentUserLoggedIn = isUserLoggedIn));
    return isCurrentUserLoggedIn;
  }

  private getLoginUrlForCurrentSite(): string {
    if (this.currentChannelData && this.currentChannelData.domain && this.currentChannelData.language) {
      return `${this.currentChannelData.domain}/${this.currentChannelData.language}/login`;
    } else {
      return 'https://www.distrelec.com/'; // this should never happen
    }
  }

  private getCurrentLanguage(): string {
    if (this.currentChannelData && this.currentChannelData.language) {
      return this.currentChannelData.language;
    } else {
      return 'en';
    }
  }
}
