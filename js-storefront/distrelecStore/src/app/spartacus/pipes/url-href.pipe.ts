import { Pipe, PipeTransform, Renderer2 } from '@angular/core';
import { Router, UrlSerializer } from '@angular/router';
import { WindowRef } from '@spartacus/core';

/**
 * This pipe contains all href transformation logic.
 * It replaces cxSupplementHashAnchors and adds new functionality for CMS pages
 * - adds domain and path to hash anchors
 * - adds context parameters (language) to links
 * - enforces a leading `/`
 */
@Pipe({ name: 'urlHrefPipe' })
export class UrlHrefPipe implements PipeTransform {
  constructor(
    protected renderer: Renderer2,
    protected winRef: WindowRef,
    private urlSerializer: UrlSerializer,
    private router: Router,
  ) {}

  public transform(input: string): string {
    try {
      const template = this.renderer.createElement('template');
      template.innerHTML = input?.trim();
      const linkNodes: NodeList = template.content.querySelectorAll('a');

      Array.from(linkNodes).forEach((link: HTMLAnchorElement) => {
        const href = link.getAttribute('href');

        if (this.excludeLinkTransformation(href)) {
          return;
        }

        if (href.startsWith('#')) {
          // #someId -> distrelec.ch/current/path#someId
          this.renderer.setProperty(link, 'href', this.getPath(href));
        } else if (!href.startsWith('http')) {
          // cms/help -> /de/cms/help
          this.renderer.setProperty(link, 'href', this.getLinkWithContextParams(href));
        }
      });

      return template.innerHTML;
    } catch (e) {
      console.error('UrlHrefPipe transform failed for value: ', input);
      return input;
    }
  }

  protected getPath(anchorId: string): string {
    const currentUrlWithoutFragment = this.winRef.location.href.replace(/#.*$/, '');
    return `${currentUrlWithoutFragment}${anchorId}`;
  }

  /**
   * Language tags should not be added to compliance documents as they are served by express js which uses a language in
   * a customer context.
   */
  private excludeLinkTransformation(href?: string): boolean {
    return (
      !href ||
      href.startsWith('/compliance-document/') ||
      href.startsWith('/medias/') ||
      href.startsWith('tel:') ||
      href.startsWith('media:')
    );
  }

  private getLinkWithContextParams(link: string): string {
    return `/${this.urlSerializer.serialize(this.router.parseUrl(link))}`;
  }
}
