import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'decimalPlaces',
})
export class DecimalPlacesPipe implements PipeTransform {
  transform(value: number | string, decimalPlaces: number): number {
    if (value === undefined || value === null) {
      return 0;
    }

    if (typeof value === 'string') {
      value = parseFloat(value);
    }
    return parseFloat(value.toFixed(decimalPlaces));
  }
}
