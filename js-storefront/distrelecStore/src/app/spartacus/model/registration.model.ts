import { Channel } from '@model/site-settings.model';
import { CountryOfOrigin } from '@model/product.model';

export interface RegistrationForm {
  type: Channel;
  titleCode: string;
  firstName: string;
  lastName: string;
  mobileNumber: string;
  phoneNumber: string;
  telephoneNumber: string;
  uid: string;
  legalEmail: string;
  companyname: string;
  postalCode: string;
  streetName: string;
  town: string;
  password: string;
  checkPwd: string;
  codiceFiscale: string;
  company: string;
  countryCode: string;
  countryCodeOther: string;
  currencyCode: string;
  customerId: string;
  customerSurveysConsent: boolean;
  knowHowConsent: boolean;
  newsLetterConsent: boolean;
  npsConsent: boolean;
  obsolescenceConsent: boolean;
  personalisationConsent: boolean;
  personalisedRecommendationConsent: boolean;
  phoneConsent: boolean;
  postConsent: boolean;
  profilingConsent: boolean;
  saleAndClearanceConsent: boolean;
  selectAllemailConsents: boolean;
  marketingConsent: boolean;
  smsConsent: boolean;
  termsAndConditionsConsent: boolean;
  isMarketingCookieEnabled: boolean;
  termsCheck: boolean;
  termsOfUseOption: boolean;
  customerType: string;
  registrationType: string;
  // Must be set to true when on B2C and entered customerId
  existingCustomer: boolean;
  faxNumber: string;
  orgNumber: string;
  invoiceEmail: string;
  vat4: string;
  vatId: string;
  functionCode: string;
}

export interface VatValidationData {
  vatValue?: string;
  vatText?: string;
  placeholder?: string;
  minChars?: number;
  optional?: boolean;

  // We need this value to update form on the VAT validation call
  isVatValidated: boolean;
  isVatValid?: boolean;
  orgNumberHint?: string;
}

export interface VatValidationResult {
  success: boolean;
}

export interface RegistrationCountryResponse {
  countries: CountryOfOrigin[];
}
