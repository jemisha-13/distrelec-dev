import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'countSelected',
})
export class CountSelectedPipe implements PipeTransform {
  transform(value: any, ...args: unknown[]): string {
    if (value.length === value.filter((v: any) => !v.selected).length) return 'All';
    return `(${value.filter((v: any) => v.selected).length})`;
  }
}
