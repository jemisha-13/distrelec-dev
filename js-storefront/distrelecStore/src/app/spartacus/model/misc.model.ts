import { Channel, CustomerType } from '@model/site-settings.model';
import { Address, B2BUnit, B2BUser, Country, Currency, Image, Language, User } from '@spartacus/core';

declare module '@spartacus/core' {
  export interface User {
    approvers?: User[];
    functionCode?: string;
    budgetWithoutLimit?: boolean;
    companyName?: string;
    contactAddress?: Address;
    contactId?: string;
    customerId?: string;
    customerType?: CustomerType;
    customersBaseSite?: string;
    derivedChannel?: Channel;
    displayUid?: string;
    doubleOptinActivated?: boolean;
    encryptedUserID?: string;
    legalEmail?: string;
    loginDisabled?: boolean;
    newsletterPopup?: boolean;
    orgUnit?: B2BUnit;
    registeredAsGuest?: boolean;
    requestQuotationPermission?: boolean;
    type?: string;
    vat4?: string;
    budget?: Budget;
    fax?: string;
    timeout?: string;
    rsCustomer?: boolean;
    erpSelectedCustomer?: boolean;
    consentConditionRequired?: boolean;
  }

  export interface Language {
    rank?: number;
  }

  interface PaginationModel {
    sort?: string;
  }

  export interface BaseSite {
    guestCheckoutEnabled: boolean;
    reevooActivated: boolean;
    reevooBrandVisible: boolean;
    requestedDeliveryDateEnabled: boolean;
    tracking: {
      gtmAuth: string;
      gtmCookiesWin: string;
      gtmPreview: string;
      gtmTagId: string;
    };
    shippingOptionsEditable: boolean;
    checkoutDeliveryMethodPricesShown?: boolean;
    enableRegistration?: boolean;
    enableAddToCart?: boolean;
    enableNewsletter?: boolean;
    enableMyAccountRedirect?: boolean;
    enableProductReturn?: boolean;
    enableSubUserManagement?: boolean;
  }

  export interface BaseStore {
    searchExperienceMap: SearchExperienceMap[];
    backorderAllowed: boolean;
    deliveryCountries: Country[];
    deliveryModes: {
      deliveryModes: [];
    };
    externalTaxEnabled: boolean;
    name: string;
    orderApprovalEnabled: boolean;
    pointsOfService: [];
    quotationsEnabled: boolean;
  }
}

export enum SearchExperience {
  FactFinder = 'FACTFINDER',
  Fusion = 'FUSION',
}

export interface SearchExperienceConfig {
  code: SearchExperience;
  searchUrl: string;
}

export interface SearchExperienceMap {
  key: string;
  value: SearchExperienceConfig;
}

export interface B2BCustomerData extends B2BUser {
  name: string;
  uid: string;
  approvers: User[]; // Incorrectly declared as [] in @spartacus/core so we use @ts-ignore here
  billingAddress?: Address;
  companyName?: string;
  contactAddress?: Address;
  contactId?: string;
  currency?: Currency;
  customerId?: string;
  customerType?: CustomerType;
  defaultAddress?: Address;
  displayUid?: string;
  doubleOptinActivated?: boolean;
  encryptedUserID?: string;
  firstName?: string;
  functionCode?: string;
  functionName?: string;
  functionNameEN?: string;
  language?: Language;
  lastName?: string;
  loginDisabled?: boolean;
  newsletterPopup?: boolean;
  orgUnit?: B2BUnit;
  registeredAsGuest?: boolean;
  requestQuotationPermission?: boolean;
  roles?: string[];
  title?: string;
  titleCode?: string;
}

export interface Budget {
  active: boolean;
  orderBudget: number;
  yearlyBudget: number;
}

export interface Entry<K, V> {
  key: K;
  value: V;
}

export interface EntryList<K, V> {
  entry: Entry<K, V>[];
}

export type ImageFormat =
  // Portrait
  | 'portrait_small'
  | 'portrait_small-elfa'
  | 'portrait_medium'
  | 'portrait_small_webp'
  | 'portrait_medium_webp'
  // Landscape
  | 'landscape_small'
  | 'landscape_medium'
  | 'landscape_large'
  | 'landscape_small_webp'
  | 'landscape_medium_webp'
  | 'landscape_large_webp'
  | 'landscape_small-elfa'
  | 'landscape_medium-elfa'
  | 'landscape_large-elfa'
  | 'landscape_medium-fr'
  // Misc
  | 'brand_logo';

export type ImageList = Entry<ImageFormat, Image>[];

export interface MonthData {
  date: Date;
  days: DateData[];
}

export interface DateData {
  date: Date;
  attribute: string;
}

export interface PossibleDeliveryDates {
  maxRequestedDeliveryDateForCurrentCart: string;
  minRequestedDeliveryDateForCurrentCart: string;
}
