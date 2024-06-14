import { Injectable, OnDestroy } from '@angular/core';
import { Meta } from '@angular/platform-browser';
import { BaseSiteService, CmsService, LanguageService, PageMetaService } from '@spartacus/core';
import { combineLatest, Observable, ReplaySubject } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';
import { LinkService } from '@services/link.service';

interface SiteProps {
  siteName: string;
  language: string;
  metaTitle: string;
  metaDescription: string;
  metaImage: string;
  canonicalUrl: string;
  alternateHreflangUrls: string;
}

interface AlternateLink {
  hreflang: string;
  href: string;
}

@Injectable({
  providedIn: 'root',
})
export class SiteMetaPropertiesService implements OnDestroy {
  private destroyed$ = new ReplaySubject<void>(1);

  constructor(
    private languageService: LanguageService,
    private baseSiteService: BaseSiteService,
    private metaService: Meta,
    private cmsService: CmsService,
    private linkService: LinkService,
    private pageMetaService: PageMetaService,
  ) {}

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  getProperties(): Observable<SiteProps> {
    return combineLatest([
      this.baseSiteService.get(),
      this.languageService.getActive(),
      this.pageMetaService.getMeta(),
    ]).pipe(
      map(([site, language, meta]) => ({
        siteName: site.name,
        language,
        metaTitle: meta.title ?? '',
        metaDescription: meta.description ?? '',
        metaImage: meta.image ?? '',
        canonicalUrl: meta.canonicalUrl ?? '',
        alternateHreflangUrls: meta.alternateLinks ?? '',
      })),
    );
  }

  addMetaTags() {
    this.getProperties()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((props) => {
        this.setMetaTag('og:locale', props.language);
        this.setMetaTag('og:site_name', props.siteName);
        this.setMetaTag('og:title', props.metaTitle);
        this.setMetaTag('og:description', props.metaDescription);
        this.setMetaTag('description', props.metaDescription);
        this.setMetaTag('og:image', props.metaImage);
        this.setMetaTag('og:url', props.canonicalUrl);
        this.setAlternateHreflangUrls(props.alternateHreflangUrls);
      });
    this.metaService.addTag({ name: 'og:type', content: 'website' });
  }

  private setMetaTag(name: string, content: string) {
    if (!content) {
      this.metaService.removeTag(this.metaNameSelector(name));
      return;
    }

    if (this.metaService.getTag(this.metaNameSelector(name))) {
      this.metaService.updateTag({ name, content }, this.metaNameSelector(name));
    } else {
      this.metaService.addTag({ name, content });
    }
  }

  private metaNameSelector(name: string) {
    return `name='${name}'`;
  }

  private setAlternateHreflangUrls(alternateHreflangUrls: string) {
    const alternateHreflangLinks = this.parseAlternateHreflangUrls(alternateHreflangUrls);
    this.linkService.removeLink("rel='alternate'");
    alternateHreflangLinks.forEach((link) =>
      this.linkService.addLink({
        rel: 'alternate',
        hreflang: link.hreflang,
        href: link.href,
      }),
    );
  }

  private parseAlternateHreflangUrls(alternateHreflangUrls: string): AlternateLink[] {
    return (
      alternateHreflangUrls?.split('|').map((alternateHreflangUrl) => {
        const [hreflang, href] = alternateHreflangUrl.split('~');
        return {
          hreflang,
          href,
        };
      }) ?? []
    );
  }
}
