import { CountryDateFormatEnum, LongDateFormatEnum } from '@model/site-settings.model';

export function getDateFormat(countryCode: string, countryLanguage: string): string {
  if (countryCode === 'CH' || countryCode === 'LI') {
    countryLanguage = countryLanguage.split('_')[0];
    return CountryDateFormatEnum['CH_' + countryLanguage];
  } else {
    return CountryDateFormatEnum[countryCode] ?? CountryDateFormatEnum.EX;
  }
}

export function getLongDateFormat(countryCode: string, countryLanguage: string): string {
  if (countryCode === 'CH' || countryCode === 'LI') {
    countryLanguage = countryLanguage.split('_')[0];
    return LongDateFormatEnum['CH_' + countryLanguage];
  } else if (countryCode === 'FI') {
    return LongDateFormatEnum['FI_' + countryLanguage];
  } else if (countryCode === 'PL') {
    return LongDateFormatEnum['PL_' + countryLanguage];
  } else {
    return LongDateFormatEnum[countryCode];
  }
}
