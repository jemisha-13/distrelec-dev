import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterLabels',
  pure: false,
})
export class FilterLabelsPipe implements PipeTransform {
  transform(items: any[]): any {
    return items.filter((item) => item.code !== 'calibrationService');
  }
}
