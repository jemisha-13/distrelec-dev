import { Directive, ElementRef, HostListener, Input, OnChanges } from '@angular/core';
import { Router, UrlSerializer } from '@angular/router';
import { WindowRef } from '@spartacus/core';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[externalRouterLink]',
})
export class ExternalRouterLinkDirective implements OnChanges {
  @Input() externalRouterLink: string;

  private href: string;
  private isBlankTarget = false;
  private isExternal = false;

  private httpRegex = new RegExp('(http(s?))://');
  private targetBlankRegex = new RegExp('(.*)" target="_blank');

  constructor(
    private el: ElementRef,
    private router: Router,
    private winRef: WindowRef,
    private urlSerializer: UrlSerializer,
  ) {}

  @HostListener('click', ['$event'])
  onClick(e) {
    e.preventDefault();

    if (this.isBlankTarget) {
      this.winRef.nativeWindow.open(this.href, '_blank');
    } else if (this.isExternal) {
      this.winRef.location.href = this.href;
    } else {
      // also used in places where query params are used for links (stops encoding special characters / serialisation with routerLink)
      this.router.navigateByUrl(this.href);
    }
  }

  ngOnChanges(): void {
    this.processURL();
    this.setAttributes();
  }

  private processURL() {
    let link = this.externalRouterLink;

    const regExpExecArray = this.targetBlankRegex.exec(link);
    if (regExpExecArray !== null) {
      this.isBlankTarget = true;
      link = regExpExecArray[1];
    }

    if (this.isExternalLink(link)) {
      this.isExternal = true;
      this.href = this.isCurrentSiteHomepage(link) ? `${link}/${this.getLinkWithContextParams('/')}` : link;
    } else {
      this.href = `/${this.getLinkWithContextParams(link)}`;
    }
  }

  private setAttributes() {
    this.el.nativeElement.setAttribute('href', this.href);
    if (this.isBlankTarget) {
      this.el.nativeElement.setAttribute('target', '_blank');
    }
  }

  private getLinkWithContextParams(link: string): string {
    return this.urlSerializer.serialize(this.router.parseUrl(link));
  }

  private isExternalLink(link: string): boolean {
    return this.httpRegex.test(link);
  }

  private isCurrentSiteHomepage(link): boolean {
    return link === this.winRef.location.origin;
  }
}
