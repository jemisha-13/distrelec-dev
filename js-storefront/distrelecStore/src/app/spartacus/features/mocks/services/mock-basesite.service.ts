import { SearchExperience } from '@model/misc.model';
import { BaseSite, BaseStore } from '@spartacus/core';
import { Observable, of } from 'rxjs';

export const MOCK_ACTIVE_BASE_STORE = {
  backorderAllowed: true,
  currencies: [
    {
      active: true,
      isocode: 'CHF',
      name: 'Swiss Franc',
      symbol: 'CHF',
    },
  ],
  defaultCurrency: {
    active: true,
    isocode: 'CHF',
    name: 'Swiss Franc',
    symbol: 'CHF',
  },
  defaultLanguage: {
    active: true,
    isocode: 'en',
    name: 'English',
    nativeName: 'English',
    rank: 0,
  },
  deliveryCountries: [
    {
      european: false,
      isocode: 'CH',
      name: 'Switzerland',
      nameEN: 'Switzerland',
    },
    {
      european: true,
      isocode: 'LI',
      name: 'Liechtenstein',
      nameEN: 'Liechtenstein',
    },
  ],
  deliveryModes: {
    deliveryModes: [],
  },
  externalTaxEnabled: false,
  languages: [
    {
      active: true,
      isocode: 'en',
      name: 'English',
      nativeName: 'English',
      rank: 0,
    },
    {
      active: true,
      isocode: 'de',
      name: 'German',
      nativeName: 'Deutsch',
      rank: 0,
    },
    {
      active: true,
      isocode: 'fr',
      name: 'French',
      nativeName: 'Français',
      rank: 0,
    },
  ],
  name: 'Business',
  orderApprovalEnabled: true,
  pointsOfService: [],
  quotationsEnabled: true,
};

export const MOCK_BASE_SITE_DATA = {
  bloomreachScriptToken: 'ed7433e4-14f9-11ee-80e1-ee1b690c1c7b',
  checkoutDeliveryMethodPricesShown: true,
  defaultLanguage: {
    active: true,
    isocode: 'de',
    name: 'German',
    nativeName: 'Deutsch',
    rank: 0,
  },
  defaultPreviewCategoryCode: 'cat-DNAV_PL_110302',
  defaultPreviewProductCode: '30373708',
  doNotCalcuteTaxForB2B: false,
  enableAddToCart: true,
  enableMyAccountRedirect: false,
  enableNewsletter: true,
  enableProductReturn: true,
  enableRegistration: true,
  guestCheckoutEnabled: true,
  hidden: false,
  httpsOnly: false,
  name: 'Distrelec Switzerland',
  paymentOptionsEditable: false,
  reevooActivated: true,
  reevooBrandVisible: false,
  requestedDeliveryDateEnabled: false,
  shippingOptionsEditable: false,
  theme: 'green',
  tracking: {
    gtmAuth: '5SmGNjY6cmByPZk5m3Z3Pw',
    gtmCookiesWin: 'x',
    gtmPreview: 'env-1',
    gtmTagId: 'GTM-K7Z2SKD',
  },
  uid: 'distrelec_CH',
  urlEncodingAttributes: [],
  urlPatterns: [
    '(?i)^https?://[^/]+(/[^?]*)?\\?(.*\\&)?(site=distrelec-ch)(|\\&.*)$',
    '(?i)^https?://distrelec-ch\\.[^/]+(|/.*|\\?.*)$',
    'https?://.*\\.distrelec\\.ch.*',
    'https?://prod\\.distrelec-ch\\.distrelec\\.[^/]+(|/.*|\\?.*)$',
  ],
  useProductFeatures: false,
  baseStore: {
    backorderAllowed: true,
    currencies: [
      {
        isocode: 'CHF',
      },
    ],
    defaultCurrency: {
      isocode: 'CHF',
    },
    defaultLanguage: {
      isocode: 'en',
      rank: 0,
    },
    deliveryCountries: [
      {
        isocode: 'CH',
      },
      {
        isocode: 'LI',
      },
    ],
    orderApprovalEnabled: false,
    quotationsEnabled: true,
    searchExperienceMap: [
      {
        key: 'de',
        value: {
          code: SearchExperience.FactFinder,
          searchUrl: 'https://aws-ccv2-q2-ff00.distrelec.com/FACT-Finder',
        },
      },
      {
        key: 'en',
        value: {
          code: SearchExperience.FactFinder,
          searchUrl: 'https://aws-ccv2-q2-ff00.distrelec.com/FACT-Finder',
        },
      },
      {
        key: 'fr',
        value: {
          code: SearchExperience.FactFinder,
          searchUrl: 'https://aws-ccv2-q2-ff00.distrelec.com/FACT-Finder',
        },
      },
    ],
  },
};

export class MockBaseSiteService {
  get() {
    return of(MOCK_BASE_SITE_DATA);
  }

  getActive(): Observable<string> {
    return of('distrelec_CH');
  }
}

export class MockDistBaseSiteService extends MockBaseSiteService {
  activeBaseStore$ = of(MOCK_ACTIVE_BASE_STORE);
  baseSiteData$ = of(MOCK_BASE_SITE_DATA);

  isAddToCartDisabledForActiveSite(): Observable<boolean> {
    return of(false);
  }

  getBaseStoreData(): Observable<any> {
    return this.activeBaseStore$;
  }
}
