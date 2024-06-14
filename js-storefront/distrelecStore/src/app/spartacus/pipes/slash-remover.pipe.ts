import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'slashRemover',
})
export class SlashRemoverPipe implements PipeTransform {
  transform(value: string, ...args: string[]): string {
    if (value !== undefined) {
      return value.replace(/\//g, '');
    }
  }
}
