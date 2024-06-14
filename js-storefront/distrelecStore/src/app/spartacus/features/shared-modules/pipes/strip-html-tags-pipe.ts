import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'stripHTMLTags',
})
export class StripHTMLTagsPipe implements PipeTransform {
  transform(value: string): string {
    if (typeof value !== 'string') {
      return '';
    }

    return value.replace(/<[^>]*>/g, '');
  }
}
