import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'articleNumber',
})
export class ArticleNumberPipe implements PipeTransform {
  transform(value: string, origin?: string): string {
    if (value.length !== 8 || this.isManufactererAndSetNumber(value, origin) || origin === 'mpnAlias') {
      return value;
    }

    if (origin === 'manufacturer') {
      return `${value.substring(0, 3)}-${value.substring(3, 5)}-${value.substring(7, 8)}`;
    }
    return `${value.substring(0, 3)}-${value.substring(3, 5)}-${value.substring(5, 8)}`;
  }

  isManufactererAndSetNumber(value: string, origin: string): boolean {
    return value.length === 8 && origin === 'manufacturer';
  }
}
