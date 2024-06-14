import { Injectable } from '@angular/core';
import { BIZCountryCodesEnum, CountryCodesEnum } from '../site-context/services/country.service';

export const webshopCountryList = [
  { value: 'AT', name: 'Austria', pattern: '^[0-9]{4}$' },
  { value: 'BE', name: 'Belgium', pattern: '^[0-9]{4}$' },
  { value: 'CZ', name: 'Czech Republic', pattern: '^[0-9]{3}\\s[0-9]{2}$' },
  { value: 'DK', name: 'Denmark', pattern: '^[0-9]{4}$' },
  { value: 'EE', name: 'Estonia', pattern: '^[0-9]{5}$' },
  { value: 'FI', name: 'Finland', pattern: '^[0-9]{5}$' },
  { value: 'FR', name: 'France', pattern: '^[0-9]{5}$' },
  { value: 'DE', name: 'Germany', pattern: '^[0-9]{5}$' },
  { value: 'HU', name: 'Hungary', pattern: '^[0-9]{4}$' },
  { value: 'IT', name: 'Italy', pattern: '^[0-9]{5}$' },
  { value: 'LV', name: 'Latvia', pattern: '^[0-9]{4}$' },
  { value: 'LI', name: 'Liechtenstein', pattern: '^[0-9]{4}$' },
  { value: 'LT', name: 'Lithuania', pattern: '^[0-9]{5}$' },
  { value: 'NL', name: 'Netherlands', pattern: '^[0-9]{4}\\s[A-Z]{2}$' },
  { value: 'NO', name: 'Norway', pattern: '^[0-9]{4}$' },
  { value: 'PL', name: 'Poland', pattern: '^[0-9]{2}-[0-9]{3}$' },
  { value: 'RO', name: 'Romania', pattern: '^[0-9]{6}$' },
  { value: 'SM', name: 'San Marino', pattern: '^[0-9]{5}$' },
  { value: 'SK', name: 'Slovakia', pattern: '^[0-9]{3}\\s[0-9]{2}$' },
  { value: 'SE', name: 'Sweden', pattern: '^[0-9]{3}\\s[0-9]{2}$' },
  { value: 'CH', name: 'Switzerland', pattern: '^[0-9]{4}$' },
  { value: 'VA', name: 'Vatican City State', pattern: '^[0-9]{5}$' },
  { value: 'BG', name: 'Bulgaria', pattern: '^\\d{4}$' },
  { value: 'HR', name: 'Croatia', pattern: '^\\d{5}$' },
  { value: 'CY', name: 'Cyprus', pattern: '^\\d{4}$' },
  { value: 'GR', name: 'Greece', pattern: '^(\\d{5}|\\d{3} \\d{2})$' },
  { value: 'IE', name: 'Ireland', pattern: '^[A-Za-z]{1}[0-9A-Za-z\\s]{3,8}$' },
  { value: 'LU', name: 'Luxemburg', pattern: '^\\d{4}$' },
  { value: 'MT', name: 'Malta', pattern: '^[A-Z]{3}\\s[0-9]{4}$' },
  { value: 'PT', name: 'Portugal', pattern: '^\\d{4}[-]\\d{3}' },
  { value: 'SI', name: 'Slovenia', pattern: '^\\d{4}$' },
  { value: 'ES', name: 'Spain', pattern: '\\d{5}$' },
  { value: 'GB', name: 'United Kingdom - Mainland', pattern: '^[A-Za-z]{1}[0-9A-Za-z\\s]{3,8}$' },
  { value: 'XI', name: 'United Kingdom - Northern Ireland', pattern: '^[A-Za-z]{1}[0-9A-Za-z\\s]{3,8}$' },
];

@Injectable({
  providedIn: 'root',
})
export class PostalValidation {
  constructor() {}

  validatePostcode(value, countryCode?): boolean {
    const companyExist = webshopCountryList.some((country) => country.value === countryCode);
    if (!companyExist) {
      // Return true if country code isn't on the list so user can enter any postcode (e.g. Singapore, United States, etc.)
      return true;
    } else {
      // iterate over each country code from the list and if found, validate with the provided regex
      return webshopCountryList.some((country) => {
        if (country.value === countryCode) {
          const regexp = new RegExp(country.pattern);
          return regexp.test(value);
        }
      });
    }
  }

  formatPostalCode(postalCode: string, countryCode: string): string {
    if (!postalCode) {
      return '';
    }

    if (!countryCode) {
      return postalCode;
    }

    if (this.checkIfPostalCodeNeedsFiveDigitsFormatting(countryCode)) {
      const formattedPostalCode = postalCode?.replace(/\s/g, '');
      if (formattedPostalCode?.length > 3) {
        return formattedPostalCode.substr(0, 3) + ' ' + formattedPostalCode.substr(3);
      }
    }

    return postalCode;
  }

  checkIfPostalCodeNeedsFiveDigitsFormatting(countryCode: string): boolean {
    return (
      countryCode === CountryCodesEnum.SWEDEN ||
      countryCode === CountryCodesEnum.CZECH_REPUBLIC ||
      countryCode === CountryCodesEnum.SLOVAKIA ||
      countryCode === BIZCountryCodesEnum.GR
    );
  }
}
