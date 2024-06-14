import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  CanonicalUrlOptions,
  CmsService,
  PageLinkService,
  PageRobotsMeta,
  RoutingPageMetaResolver,
  TranslationService,
  WindowRef,
} from '@spartacus/core';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class DistBasePageMetaResolver extends BasePageMetaResolver {
  protected robots$: Observable<PageRobotsMeta[]> = this.page$.pipe(
    map((page) => page.robots || this.getDefaultRobotsMetaForCurrentUrl()),
  );

  protected canonicalUrl$: Observable<string> = this.page$.pipe(
    map((page) => page?.properties.seo?.canonicalUrl ?? ''),
  );

  constructor(
    protected cmsService: CmsService,
    protected translation: TranslationService,
    protected routingPageMetaResolver: RoutingPageMetaResolver,
    protected router: Router,
    protected pageLinkService: PageLinkService,
    protected winRef: WindowRef,
  ) {
    super(cmsService, translation, routingPageMetaResolver, router, pageLinkService);
  }

  resolveCanonicalUrl(options?: CanonicalUrlOptions): Observable<string> {
    return this.canonicalUrl$;
  }

  resolveAlternateLinks(): Observable<string> {
    return this.page$.pipe(map((page) => page?.properties.seo?.alternateHreflangUrls ?? ''));
  }

  private isIndexFollowUrl(url: string): boolean {
    const indexFollowRegex = /(.+\/p\/.+)|(.+\/c\/.+)|(.+\/manufacturer\/.+)|(.+\/cms\/.+)/;
    return indexFollowRegex.test(url);
  }

  private getDefaultRobotsMetaForCurrentUrl(): PageRobotsMeta[] {
    if (this.isIndexFollowUrl(this.winRef.location.pathname)) {
      return [PageRobotsMeta.INDEX, PageRobotsMeta.FOLLOW];
    } else {
      return [PageRobotsMeta.NOINDEX, PageRobotsMeta.FOLLOW];
    }
  }
}
