import { Pipe, PipeTransform } from '@angular/core';
import { SafeUrl, DomSanitizer } from '@angular/platform-browser';

@Pipe({
  name: 'sanitizeUrl',
})
export class SanitizeUrl implements PipeTransform {
  constructor(private dom: DomSanitizer) {}

  transform(url: string): SafeUrl {
    return this.dom.bypassSecurityTrustResourceUrl(url);
  }
}
