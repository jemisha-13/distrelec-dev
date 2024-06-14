import { Pipe, PipeTransform } from '@angular/core';
import { FacetValue } from '@spartacus/core';

@Pipe({
  name: 'optionFilter',
})
export class OptionFilterPipe implements PipeTransform {
  transform(values: FacetValue[], filterQuery: string, filterKey: string = 'name'): FacetValue[] {
    if (filterQuery === '' || values.length === 0) {
      return values;
    }
    return values?.filter((el) => el[filterKey]?.toLowerCase().includes(filterQuery.toLowerCase()));
  }
}
