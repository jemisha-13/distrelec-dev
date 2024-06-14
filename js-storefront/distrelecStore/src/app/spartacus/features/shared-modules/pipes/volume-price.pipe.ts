import { CurrencyPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { CurrentSiteSettings } from '@model/site-settings.model';

/**
 *
 * It is written to solve some scenarios using the Angular's built-in currency pipe.
 *
 * Please pass unformatted value to don't get any formatting issue. (e.g. 1234.56 or '1234.56')
 * Don't pass formatted value like "1,234.56", "1'234.56", "1 234.56" etc.
 *
 * @param value
 * @param currencyCode
 * @returns
 */
@Pipe({
  name: 'volumePrice',
  pure: false,
})
export class VolumePricePipe implements PipeTransform {
  private currencyPipe: CurrencyPipe = new CurrencyPipe('');

  constructor() {}

  transform(
    value: string | number,
    currencyCode: string,
    currentChannelData: CurrentSiteSettings,
    basePrice?: number,
  ): string {
    if (value === undefined || value === null) {
      return '';
    }

    const valueNumeric = typeof value === 'string' ? Number.parseFloat(value) : value;

    try {
      const { country, language } = currentChannelData;

      let maximumFractionDigits = 2;

      if (basePrice && basePrice < 1 && valueNumeric < 1) {
        maximumFractionDigits = this.getScale(valueNumeric) > 2 ? 6 : 2;
      }
      const digitsInfo = '1.2-' + maximumFractionDigits;

      let formattedByCurrencyPipe: string;
      let locale = `${language}-${country}`;

      if (currencyCode === 'CHF') {
        locale = 'en-' + country;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '')
          .replace(/,/g, "'");
      } else if (currencyCode === 'SEK') {
        locale = 'sv-' + country;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '')
          .trim()
          .replace(/\s/g, '.');
      } else if (currencyCode === 'NOK' && (locale === 'no-NO' || locale === 'en-NO')) {
        locale = 'nb';
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '')
          .trim()
          .replace(/\s/g, '.');
        if (language === 'no') {
          formattedByCurrencyPipe = formattedByCurrencyPipe.replace('.', ' ');
        }
      } else if (country === 'DE' && language === 'en') {
        locale = `${language}-${country}`;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '');
      } else if (country === 'AT' && language === 'de') {
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '')
          .replace('.', ' ');
      } else if (country === 'NL' && language === 'en') {
        locale = 'nl-' + country;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '');
      } else if (country === 'PL' && language === 'en') {
        locale = 'pl-' + country;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '')
          .trim()
          .replace(/\s/g, '.');
      } else if (country === 'BE' && (language === 'en' || language === 'fr' || language === 'nl')) {
        locale = language + '-' + country;
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '');
        if (language === 'fr') {
          formattedByCurrencyPipe = formattedByCurrencyPipe.replace('.', ' ');
        }
      } else {
        formattedByCurrencyPipe = this.currencyPipe
          .transform(valueNumeric, currencyCode, 'code', digitsInfo, locale)
          .replace(currencyCode, '');
      }
      return formattedByCurrencyPipe.trim();
    } catch (e) {
      return valueNumeric.toString();
    }
  }

  getScale(num: number) {
    if (!isNaN(+num)) {
      const decimals = (num + '').split('.')[1];
      if (decimals) {
        return decimals.length;
      }
    }
    return 0;
  }
}
