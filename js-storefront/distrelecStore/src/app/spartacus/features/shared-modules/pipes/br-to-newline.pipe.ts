import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'brToNewline',
})
export class BrToNewlinePipe implements PipeTransform {
  transform(value: string): unknown {
    // Replace <br>, <br/>, <br /> with newline
    return value.replace(/(<br ?\/?>)/g, '\n');
  }
}
