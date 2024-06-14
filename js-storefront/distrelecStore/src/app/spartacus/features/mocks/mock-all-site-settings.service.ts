import { Observable, of } from 'rxjs';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { CountryCodesEnum } from '@context-services/country.service';

export const MOCK_CHANNEL_DATA_PRETEST_CH: CurrentSiteSettings = {
  channel: 'B2B',
  language: 'en',
  country: CountryCodesEnum.SWITZERLAND,
  currency: 'CHF',
  domain: 'https://pretest.distrelec.ch',
  mediaDomain: 'https://pretest.media.distrelec.com',
  storefrontDomain: 'https://pretest.storefront.distrelec.ch',
};

export default class MockAllsitesettingsService {
  getMediaDomain(): Observable<string> {
    return of('http://pretest.mock.api.distrelec.com/');
  }

  getCurrentChannelData(): Observable<any> {
    return of(MOCK_CHANNEL_DATA_PRETEST_CH);
  }
}
