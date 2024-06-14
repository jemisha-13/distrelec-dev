/* eslint-disable @typescript-eslint/no-unused-vars */
import { TestBed, waitForAsync } from '@angular/core/testing';
import { PhoneNumberService } from './phonenumber.service';
import { BIZCountryCodesEnum, CountryCodesEnum } from '../site-context/services/country.service';

describe('PhoneNumberService', () => {
  const EXAMPLE_SWITZERLAND_PHONE_NUMBER = '+41782345678';
  const EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER = '+442890311600';
  const EXAMPLE_SWITZERLAND_PHONE_NUMBER_WITHOUT_PREFIX = '782345678';
  const EXAMPLE_ITALY_PHONE_NUMBER = '+39 0549 908938';
  const EXAMPLE_ITALY_PHONE_NUMBER_WITHOUT_PREFIX = '0811234567';
  const EXAMPLE_SAN_MARINO_PHONE_NUMBER = '+378 991007';
  const EXAMPLE_VATICAN_PHONE_NUMBER = '+39 06 69812345';
  const INVALID_PHONE_NUMBER = '+123';
  const EMPTY_PHONE_NUMBER = '';

  let phoneNumberService: PhoneNumberService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PhoneNumberService],
    });
    phoneNumberService = TestBed.inject(PhoneNumberService);
  });

  it('testIsValidPhoneNumberForSwitzerlandRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER, CountryCodesEnum.SWITZERLAND)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidPhoneNumberWithoutPrefixForSwitzerlandRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER_WITHOUT_PREFIX, CountryCodesEnum.SWITZERLAND)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidPhoneNumberWithoutPrefixForLiechtensteinRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER_WITHOUT_PREFIX, CountryCodesEnum.LIECHTENSTEIN)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidSwitzerlandPhoneNumberForLiechtensteinRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER, CountryCodesEnum.LIECHTENSTEIN)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsInvalidPhoneNumberForRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(INVALID_PHONE_NUMBER, CountryCodesEnum.SWITZERLAND)
      .subscribe((result) => expect(result).toBeFalse());
  }));

  it('testIsEmptyPhoneNumberForRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EMPTY_PHONE_NUMBER, CountryCodesEnum.SWITZERLAND)
      .subscribe((result) => expect(result).toBeFalse());
  }));

  it('testIsValidSwitzerlandPhoneNumberForRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(INVALID_PHONE_NUMBER, CountryCodesEnum.GERMANY)
      .subscribe((result) => expect(result).toBeFalse());
  }));

  it('testIsValidNorthernIrelandPhoneNumberForRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER, BIZCountryCodesEnum.NORTHERN_IRELAND)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidPhoneNumberForItalyRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, CountryCodesEnum.ITALY)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidPhoneNumberForSanMarinoRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_SAN_MARINO_PHONE_NUMBER, CountryCodesEnum.SAN_MARINO)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsValidPhoneNumberForVaticanRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_VATICAN_PHONE_NUMBER, CountryCodesEnum.VATICAN)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsItalyPhoneNumberValidForVaticanRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, CountryCodesEnum.VATICAN)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsItalyPhoneNumberWithoutPrefixValidForSanMarinoRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER_WITHOUT_PREFIX, CountryCodesEnum.SAN_MARINO)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsItalyPhoneNumberValidForSanMarinoRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, CountryCodesEnum.SAN_MARINO)
      .subscribe((result) => expect(result).toBeTrue());
  }));

  it('testIsVaticanPhoneNumberInvalidForItalyRegion', waitForAsync(() => {
    phoneNumberService
      .isValidNumberForRegion(EXAMPLE_VATICAN_PHONE_NUMBER, CountryCodesEnum.ITALY)
      .subscribe((result) => expect(result).toBeFalse());
  }));
});
