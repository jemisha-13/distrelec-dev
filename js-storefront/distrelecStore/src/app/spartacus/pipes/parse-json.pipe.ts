import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'parseJson',
})
export class ParseJsonPipe implements PipeTransform {
  transform(value: string, ...args: string[]): object | string {
    if (value !== undefined) {
      let destructuredArgs = { ...args };

      if (destructuredArgs[1] === 'factFinder') {
        return JSON.parse(value).landscape_small;
      }

      return JSON.parse(value);
    }

    return value;
  }
}
