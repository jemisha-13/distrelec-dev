import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'parseHtml',
})
export class ParseHtmlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string, ...args: string[]): SafeHtml {
    if (value !== undefined) {
      return this.sanitizer.bypassSecurityTrustHtml(
        value
          ?.replace(/(\r\n|\n|\r)/g, '')
          ?.replace(/\\\\/g, '\\')
          ?.replace(/\\"/g, '"'),
      );
    }

    return value;
  }
}
