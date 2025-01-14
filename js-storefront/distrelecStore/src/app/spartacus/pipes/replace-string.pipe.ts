import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'replaceString',
})
export class ReplaceStringPipe implements PipeTransform {
  transform(value: string, strToReplace: string, replacementStr: string): string {
    if (!value || !strToReplace || !replacementStr) {
      return value;
    }
    return value.replace(strToReplace, replacementStr);
  }
}
