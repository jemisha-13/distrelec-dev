import { Country, Region } from '@spartacus/core';

declare module '@spartacus/core' {
  interface Address {
    billingAddress?: boolean;
    cellphone?: string;
    codiceFiscale?: string;
    companyName2?: string;
    companyName?: string;
    contactAddress?: boolean;
    defaultAddress?: boolean;
    defaultBilling?: boolean;
    defaultShipping?: boolean;
    department?: string;
    departmentCode?: string;
    email?: string;
    functionName?: string;
    functionNameEN?: string;
    erpAddressID?: string;
    firstName?: string;
    formattedAddress?: string;
    lastName?: string;
    line1?: string;
    line2?: string;
    checkoutPhone?: string;
    phone1?: string;
    phone?: string;
    postalCode?: string;
    shippingAddress?: boolean;
    titleCode?: string;
    town?: string;
    visibleInAddressBook?: boolean;
  }

  interface Country {
    european?: boolean;
    nameEN?: string;
    regions?: Region[];
  }

  interface Region {
    nameEN?: string;
  }
}

export interface Countries {
  countries: Country[];
}

export type AddressType = 'SHIPPING' | 'BILLING';
