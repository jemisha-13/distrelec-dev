import { Directive, ElementRef, HostListener, Input, OnChanges } from '@angular/core';
import { convertToParamMap, Params, Router } from '@angular/router';
import { SiteContextUrlSerializer, WindowRef } from '@spartacus/core';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[absoluteRouterLink]',
})
export class AbsoluteRouterLinkDirective implements OnChanges {
  @Input() absoluteRouterLink = '/';
  @Input() queryParams?: Params | null;

  constructor(
    private el: ElementRef,
    private router: Router,
    private winRef: WindowRef,
    private siteContextUrlSerializer: SiteContextUrlSerializer,
  ) {
    this.setHref();
  }

  @HostListener('click', ['$event'])
  onClick(e) {
    e.preventDefault();
    if (this.absoluteRouterLink.startsWith('/')) {
      this.router.navigateByUrl(this.getUrlWithParams());
    } else {
      // handles url fragments
      const element = this.winRef.document.querySelector(this.absoluteRouterLink);
      element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
    }
  }

  ngOnChanges(): void {
    this.setHref();
  }

  private getUrlWithParams(): string {
    try {
      let link: string;
      if (this.absoluteRouterLink === '/') {
        // Put current language context into the URL so crawlers don't detect temporary redirects
        link = `${this.winRef.location.origin}/${this.siteContextUrlSerializer.serialize(this.router.parseUrl('/'))}`;
      } else {
        link = this.absoluteRouterLink.startsWith('/')
          ? `${this.winRef.location.origin}${this.absoluteRouterLink}`
          : `${this.winRef.location.origin}/${this.absoluteRouterLink}`;
      }

      const url = new URL(link);
      const paramMap = convertToParamMap(this.queryParams);
      paramMap.keys.forEach((key: string) => {
        url.searchParams.set(key, paramMap.get(key));
      });
      return `${url.pathname}${url.search.replace(/%2B/g, '+')}`;
    } catch (e) {
      console.error(`Invalid absoluteRouterLink: ${this.absoluteRouterLink}`);
      return this.absoluteRouterLink;
    }
  }

  private setHref() {
    this.el.nativeElement.setAttribute('href', this.getUrlWithParams());
  }
}
