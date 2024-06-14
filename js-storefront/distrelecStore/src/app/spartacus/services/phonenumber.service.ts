import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { CountryCode, PhoneNumber } from 'libphonenumber-js';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { map } from 'rxjs/operators';
import { BIZCountryCodesEnum, CountryCodesEnum } from '../site-context/services/country.service';

export type DistCountryCode = import('libphonenumber-js').CountryCode | 'XI';
export type DistNationalNumber = import('libphonenumber-js').NationalNumber;

@Injectable({
  providedIn: 'root',
})
export class PhoneNumberService {
  isValidPhoneNumber(phoneNumber: string, distCountryCode?: DistCountryCode): Observable<boolean> {
    return from(
      import('libphonenumber-js/max')
        .then((libPhoneNumber) => libPhoneNumber.isValidPhoneNumber(phoneNumber, this.mapCountryCode(distCountryCode)))
        .catch(() => true),
    );
  }

  isValidNumberForRegion(phoneNumber: string, distCountryCode: DistCountryCode): Observable<boolean> {
    const countryCode = this.mapCountryCode(distCountryCode);
    return from(
      import('libphonenumber-js/max')
        .then((libPhoneNumber) => {
          const parsedPhoneNumber = libPhoneNumber.parsePhoneNumber(phoneNumber, countryCode);

          if (countryCode === CountryCodesEnum.SWITZERLAND) {
            const parsedPhoneNumberForLiechtenstein = libPhoneNumber.parsePhoneNumber(
              phoneNumber,
              CountryCodesEnum.LIECHTENSTEIN,
            );
            return (
              this.isPhoneNumberValid(parsedPhoneNumber, countryCode) ||
              this.isPhoneNumberValid(parsedPhoneNumberForLiechtenstein, CountryCodesEnum.LIECHTENSTEIN)
            );
          }

          if (countryCode === CountryCodesEnum.LIECHTENSTEIN) {
            const parsedPhoneNumberForSwitzerland = libPhoneNumber.parsePhoneNumber(
              phoneNumber,
              CountryCodesEnum.SWITZERLAND,
            );
            return (
              this.isPhoneNumberValid(parsedPhoneNumber, countryCode) ||
              this.isPhoneNumberValid(parsedPhoneNumberForSwitzerland, CountryCodesEnum.SWITZERLAND)
            );
          }

          if (this.isSanMarinoOrVatican(countryCode)) {
            const parsedPhoneNumberForForItaly = libPhoneNumber.parsePhoneNumber(phoneNumber, CountryCodesEnum.ITALY);
            return (
              this.isPhoneNumberValid(parsedPhoneNumber, countryCode) ||
              this.isPhoneNumberValid(parsedPhoneNumberForForItaly, CountryCodesEnum.ITALY)
            );
          }
          return this.isPhoneNumberValid(parsedPhoneNumber, countryCode);
        })
        .catch(() => false),
    );
  }

  createPhoneNumberValidator(countryCodeControl: AbstractControl): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> =>
      this.isValidNumberForRegion(control.value, countryCodeControl.value).pipe(
        map((isValid) => (isValid ? null : { erroring: true })),
      );
  }

  private isPhoneNumberValid(parsedPhoneNumber: PhoneNumber, countryCode: CountryCode) {
    return parsedPhoneNumber.isValid() && this.isPhoneNumberCountryMatching(parsedPhoneNumber, countryCode);
  }

  private mapCountryCode(distCountryCode: DistCountryCode): CountryCode {
    if (distCountryCode === BIZCountryCodesEnum.NORTHERN_IRELAND) {
      return BIZCountryCodesEnum.GREAT_BRITAIN;
    }
    return distCountryCode;
  }

  private isSanMarinoOrVatican(countryCode: CountryCode): boolean {
    return countryCode === CountryCodesEnum.SAN_MARINO || countryCode === CountryCodesEnum.VATICAN;
  }

  private isPhoneNumberCountryMatching(parsedPhoneNumber: PhoneNumber, countryCode: CountryCode): boolean {
    return parsedPhoneNumber.country === countryCode;
  }
}
