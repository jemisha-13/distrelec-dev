import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'firstWord',
})
export class FirstWordPipe implements PipeTransform {
  transform(value: string): string {
    if (!value) {
      return '';
    }

    let words = value.split(' ');

    if (words.length > 1) {
      return words[0] + ' ...';
    } else {
      return value;
    }
  }
}
