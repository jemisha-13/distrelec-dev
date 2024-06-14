import { Injectable } from '@angular/core';
import { RegistrationForm, VatValidationData } from '@model/registration.model';
import { RegistrationTypeEnum } from '@services/checkout.service';
import { WindowRef } from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { Observable, of } from 'rxjs';
import { CountryCodesEnum, CountryService } from '../site-context/services/country.service';

export enum VatIdPrefixEnum {
  AT = 'AT',
  BE = 'BE',
  CZ = 'CZ',
  DE = 'DE',
  DK = 'DK',
  EE = 'EE',
  FR = 'FR',
  FI = 'FI',
  HU = 'HU',
  LV = 'LV',
  LT = 'LT',
  NL = 'NL',
  PL = 'PL',
  RO = 'RO',
  SK = 'SK',
  CH = 'CHE',
  LI = '',
  IT = 'IT',
  SM = 'SM',
  VA = '',
  BG = 'BG',
  HR = 'HR',
  CY = 'CY',
  GR = 'EL',
  IE = 'IE',
  LU = 'LU',
  MT = 'MT',
  PT = 'PT',
  SI = 'SI',
  ES = 'ES',
  GB = 'GB',
  XI = 'XI',
}

export enum CountriesWithOrgNumberEnum {
  DK = 'DK',
  NO = 'NO',
  SE = 'SE',
  EE = 'EE',
  LV = 'LV',
  NL = 'NL',
}

export enum CountriesWithoutVat {
  NORWAY = 'NO',
  SWEDEN = 'SE',
}

const orgNumberPatterns = {
  DEFAULT: /.*/,
  DK: /^[0-9]{8}$/,
  NL: /^[0-9]{8}$/,
  NO: /^[0-9]{9}$/,
  SE: /^[0-9]{10}$/,
  LV: /^[0-9]{11}$/,
};

@Injectable({
  providedIn: 'root',
})
export class RegisterServiceHelper {
  protected vatIdValidation: VatValidationData;

  constructor(
    private cookieService: DistCookieService,
    private winRef: WindowRef,
    private countryService: CountryService,
  ) {}

  createVatValidationObject(
    vatText: string,
    placeholder: string,
    minChars: number,
    optional: boolean,
    orgNumberHint?: string,
  ) {
    this.vatIdValidation = {
      vatText,
      placeholder,
      minChars,
      optional,
      orgNumberHint,
      isVatValidated: false,
    };
  }

  createVatIDValidators(countryCode: string): Observable<VatValidationData> {
    if (this.countryService.isExportShopCountry(countryCode)) {
      this.setVATIdValidatorForForEUCountry(countryCode);
    } else {
      this.setVATforItOrOther(countryCode);
    }

    return of(this.vatIdValidation);
  }

  setVATIdValidatorForForEUCountry(countryCodeOther: string) {
    this.setVAT(countryCodeOther, 'BG', 'validations.vat.hint_BG', '1 2 3 4 5 6 7 8 9', 9, false);
    this.setVAT(countryCodeOther, 'HR', 'validations.vat.hint_HR', '1 2 3 4 5 6 7 8 9 0 1', 11, false);
    this.setVAT(countryCodeOther, 'CY', 'validations.vat.hint_CY', '1 2 3 4 5 6 7 8 X', 8, false);
    this.setVAT(countryCodeOther, 'GR', 'validations.vat.hint_GR', '1 2 3 4 5 6 7 8 9', 9, false);
    this.setVAT(countryCodeOther, 'IE', 'validations.vat.hint_IE', '1 X 2 3 4 5 6 X', 8, false);
    this.setVAT(countryCodeOther, 'LU', 'validations.vat.hint_LU', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCodeOther, 'MT', 'validations.vat.hint_MT', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCodeOther, 'PT', 'validations.vat.hint_PT', '1 2 3 4 5 6 7 8 9', 9, false);
    this.setVAT(countryCodeOther, 'SI', 'validations.vat.hint_SI', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCodeOther, 'ES', 'validations.vat.hint_ES', 'X 1 2 3 4 5 6 7 X', 9, false);
    this.setVAT(countryCodeOther, 'GB', 'validations.vat.hint_GB', '1 2 3 4 5 6 7 8 9', 9, false);
    this.setVAT(countryCodeOther, 'XI', 'validations.vat.hint_XI', '1 2 3 4 5 6 7 8 9', 9, false);
  }

  setVAT(
    countryCodeOther,
    locale,
    vatText: string,
    placeholder: string,
    minChars: number,
    optional: boolean,
    orgNumberHint?: string,
  ) {
    if (countryCodeOther === locale) {
      this.createVatValidationObject(vatText, placeholder, minChars, optional, orgNumberHint);
    }
  }

  setVATforItOrOther(countryCode) {
    this.setVAT(countryCode, 'BE', 'validations.vat.hint_BE', '1 2 3 4 5 6 7 8 9 0', 10, false);
    this.setVAT(countryCode, 'IT', 'validations.vat.hint_IT', '1 2 3 4 5 6 7 8 9 0 1', 5, false);
    this.setVAT(countryCode, 'SM', 'validations.vat.hint_IT', '1 2 3 4 5', 5, true);
    this.setVAT(countryCode, 'VA', 'validations.vat.hint_IT', '', 0, true);
    this.setVAT(countryCode, 'HU', 'validations.vat.hint_HU', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCode, 'RO', 'validations.vat.hint_RO', '1 2 3 4 5 6 7 8 9 0', 2, false);
    this.setVAT(countryCode, 'CZ', 'validations.vat.hint_CZ', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCode, 'SE', 'validations.vat.hint_SE', '1 2 3 4 5 6 7 8 9', 10, true, 'validations.vat.hint_SE');
    this.setVAT(countryCode, 'DK', 'validations.vat.hint_DK', '1 2 3 4 5 6 7 8', 8, true, 'validations.vat.hint_DK');
    this.setVAT(countryCode, 'AT', 'validations.vat.hint_AT', 'U 1 2 3 4 5 6 7 8', 8, true, 'validations.vat.hint_AT');
    this.setVAT(countryCode, 'FI', 'validations.vat.hint_FI', '1 2 3 4 5 6 7 8', 8, false);
    this.setVAT(countryCode, 'FR', 'validations.vat.hint_FR', 'X X 1 2 3 4 5 6 7 8 9', 11, false);
    this.setVAT(countryCode, 'LT', 'validations.vat.hint_LT', '1 2 3 4 5 6 7 8 9', 9, false);
    this.setVAT(countryCode, 'LV', 'validations.vat.hint_LV', '1 2 3 4 5 6 7 8 9 0 1', 11, false);
    // NL has a commercial number instead of organisational number
    this.setVAT(
      countryCode,
      'NL',
      'validations.vat.hint_NL',
      '1 2 3 4 5 6 7 8 9 B 1 2',
      11,
      false,
      'validations.vat.hint_NL_org_no',
    );
    this.setVAT(countryCode, 'NO', 'validations.vat.hint_NO', '1 2 3 4 5 6 7 8 9', 9, false, 'validations.vat.hint_NO');
    this.setVAT(countryCode, 'PL', 'validations.vat.hint_PL', '1 2 3 4 5 6 7 8 9 0', 10, false);
    this.setVAT(countryCode, 'SK', 'validations.vat.hint_SK', '1 2 3 4 5 6 7 8 9 0', 10, false);
    this.setVAT(countryCode, 'DE', 'validations.vat.hint_DE', '1 2 3 4 5 6 7 8 9', 9, true);
    this.setVAT(countryCode, 'EE', 'validations.vat.hint_EE', '1 2 3 4 5 6 7 8 9', 9, true);
    this.setVAT(countryCode, 'CH', 'validations.vat.hint_CH', '1 2 3 4 5 6 7 8 9', 9, true);
    if (!this.vatIdValidation) {
      this.createVatValidationObject('validations.vat.hint_9', '1 2 3 4 5 6 7 8 9', 9, true);
    }
  }

  returnCorrectValueString(condition: string): string {
    return condition ? condition.trim() : '';
  }

  getVatId(vatId: string, countryCode: string): string {
    if (vatId) {
      if (vatId.startsWith(this.getVatIdPrefix(countryCode))) {
        return vatId;
      } else {
        return this.returnCorrectValueString(this.getVatIdPrefix(countryCode) + vatId);
      }
    }
    return '';
  }

  getVatIdPrefix(country: string): string {
    return VatIdPrefixEnum[country];
  }

  returnCountryCode(countryCode, countryCodeOther): string {
    return countryCode === CountryCodesEnum.SWITZERLAND || countryCode === CountryCodesEnum.ITALY
      ? countryCodeOther
      : countryCode;
  }

  returnCorrectEmail(legalEmail: string, uid: string): string {
    return legalEmail ? legalEmail : uid;
  }

  returnIsMarketingCookieEnabled(): boolean {
    return this.cookieService.get('DISTRELEC_ENSIGHTEN_PRIVACY_Marketing') === '1';
  }

  returnCorrectBoolean(condition): boolean {
    return condition ? true : false;
  }

  getVat4ValueForPayload(form): string | null {
    if (form.countryCode === CountryCodesEnum.ITALY) {
      return this.returnCorrectValueString(form.vat4);
    }
    return null;
  }

  createRequestBody(form: RegistrationForm) {
    let registBody = {};
    registBody = {
      titleCode: form.titleCode,
      firstName: form.firstName,
      lastName: form.lastName,
      phoneNumber: form.phoneNumber,
      uid: form.uid,
      legalEmail: form.legalEmail,
      password: form.password,
      checkPwd: form.checkPwd,
      codiceFiscale: form.countryCode === CountryCodesEnum.ITALY ? form.codiceFiscale : '',
      company: this.returnCorrectValueString(form.companyname),
      countryCode: this.returnCountryCode(form.countryCode, form.countryCodeOther),
      currencyCode: '',
      customerId: this.returnCorrectValueString(form.customerId),

      customerSurveysConsent: false,
      knowHowConsent: false,
      isMarketingCookieEnabled: this.returnIsMarketingCookieEnabled(),
      newsLetterConsent: false,
      npsConsent: false,
      obsolescenceConsent: false,
      personalisationConsent: form.personalisationConsent,
      personalisedRecommendationConsent: false,
      phoneConsent: form.phoneConsent,
      postConsent: form.postConsent,
      profilingConsent: form.profilingConsent,
      saleAndClearanceConsent: false,
      selectAllemailConsents: false,
      marketingConsent: form.marketingConsent,
      smsConsent: form.smsConsent,
      termsAndConditionsConsent: false,
      termsCheck: form.termsOfUseOption,
      termsOfUseOption: form.termsOfUseOption,
      customerType: form.type,
      registrationType:
        this.winRef.location.href.indexOf('checkout') > -1
          ? RegistrationTypeEnum.CHECKOUT
          : RegistrationTypeEnum.STANDALONE,
      existingCustomer: this.returnCorrectBoolean(form.customerId),
      faxNumber: '',
      organizationalNumber: this.returnCorrectValueString(form.orgNumber),
      invoiceEmail: this.returnCorrectEmail(form.invoiceEmail, form.uid),
      vat4: this.getVat4ValueForPayload(form),
      vatId: this.getVatId(form.vatId, this.returnCountryCode(form.countryCode, form.countryCodeOther)),
    };

    if (form.functionCode) {
      registBody = { functionCode: form.functionCode, ...registBody };
    }

    return registBody;
  }

  getVatIdValidation(): VatValidationData {
    return this.vatIdValidation;
  }

  scrollViewToTop() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
  }

  getOrgNumberPattern(countryCode: string): RegExp {
    const regex = orgNumberPatterns[countryCode];
    return regex ?? orgNumberPatterns.DEFAULT;
  }
}
