import { CountryCodesEnum } from '@context-services/country.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { Observable, of } from 'rxjs';

/* eslint-disable @typescript-eslint/naming-convention */
export class MockSiteSettingsService implements Partial<AllsitesettingsService> {
  getCurrentChannelData(): Observable<CurrentSiteSettings> {
    return of({
      channel: 'B2C',
      language: 'en',
      country: CountryCodesEnum.SWITZERLAND,
      currency: 'CHF',
      domain: 'https://test.distrelec.ch',
      mediaDomain: 'https://test.media.distrelec.com',
      storefrontDomain: 'https://test.storefront.distrelec.ch',
    });
  }
}
