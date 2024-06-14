import { ChangeDetectionStrategy, Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { CmsParagraphComponent } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { Router } from '@angular/router';
import { filter, first } from 'rxjs/operators';
import { containsParams } from '@features/shared-modules/paragraph/html-links-helper';

@Component({
  selector: 'cx-paragraph',
  templateUrl: './paragraph.component.html',
  styleUrls: ['./paragraph.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ParagraphComponent implements OnInit {

  constructor(
    public component: CmsComponentData<CmsParagraphComponent>,
    protected router: Router,
    private elementRef: ElementRef,
  ) {}

  ngOnInit(): void {
    this.component.data$
      .pipe(
        filter((item) => !item.content.startsWith('<style')),
        filter((item) => containsParams(item.content)),
        first(),
      )
      .subscribe();
  }

  @HostListener('click', ['$event'])
  public handleClick(event: Event): void {
    const element = this.findClosestParentHTMLAnchorElement(event.target);
    if (element) {
      const href = element.getAttribute('href');

      const documentHost = element.ownerDocument.URL.split('://')[1].split('/')[0];

      const hrefWithFragmentPattern = /^.+#.+$/;

      // Use router for internal link navigation
      if (
        href &&
        documentHost === element.host &&
        !hrefWithFragmentPattern.test(href) &&
        !this.excludeRouterRedirect(href)
      ) {
        event.preventDefault();
        const withoutTrailingSlash = href.endsWith('/') ? href.slice(0, -1) : href;
        this.router.navigateByUrl(withoutTrailingSlash);
      }
    }
  }

  /**
   * Router should not be redirect to compliance documents and medias.
   */
  private excludeRouterRedirect(href?: string): boolean {
    return href.startsWith('/compliance-document/') || href.startsWith('/medias/');
  }

  private findClosestParentHTMLAnchorElement(eventTarget: EventTarget): HTMLAnchorElement {
    if (eventTarget instanceof HTMLAnchorElement) {
      return eventTarget;
    } else if (eventTarget instanceof HTMLElement) {
      const htmlElement = eventTarget as HTMLElement;
      const parentElement = htmlElement.parentElement;
      if (parentElement.localName !== 'cx-paragraph') {
        return this.findClosestParentHTMLAnchorElement(parentElement);
      }
    }
    return null;
  }
}
