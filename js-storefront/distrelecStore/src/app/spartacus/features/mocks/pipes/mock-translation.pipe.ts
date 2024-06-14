import { Pipe, PipeTransform } from '@angular/core';
import { Translatable, TranslatableParams } from '@spartacus/core';

@Pipe({
  name: 'cxTranslate',
})
export class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return input;
  }
}
