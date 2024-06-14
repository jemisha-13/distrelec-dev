import { Injectable } from '@angular/core';
import {
  BreadcrumbMeta,
  CmsService,
  ContentPageMetaResolver,
  Page,
  PageRobotsMeta,
  PageType,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, defer, EMPTY, Observable, of } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';
import { BreadcrumbService } from '@services/breadcrumb.service';

@Injectable({
  providedIn: 'root',
})
export class DistContentPageMetaResolver extends ContentPageMetaResolver {
  protected page$: Observable<Page> = defer(() => this.cmsService.getCurrentPage()).pipe(filter((p) => Boolean(p)));

  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  constructor(
    basePageMetaResolver: DistBasePageMetaResolver,
    protected cmsService: CmsService,
    protected translation: TranslationService,
    protected breadCrumbService: BreadcrumbService,
  ) {
    super(basePageMetaResolver);
    this.pageType = PageType.CONTENT_PAGE;
  }

  resolveTitle(): Observable<string> {
    return this.page$.pipe(map((page) => page?.properties?.seo.metaTitle as string));
  }

  resolveImage(): Observable<string> {
    return this.page$.pipe(map((page) => page?.properties.seo.metaImage as string));
  }

  resolveDescription(): Observable<string> {
    return this.page$.pipe(map((page) => page?.properties?.seo?.metaDescription as string));
  }

  resolveRobots(): Observable<PageRobotsMeta[]> {
    return this.basePageMetaResolver.resolveRobots();
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[] | undefined> {
    return this.page$.pipe(
      switchMap((page) => {
        if (this.breadCrumbService.isBreadcrumbHiddenOnPage(page)) {
          return EMPTY;
        }
        return combineLatest(
          this.homeBreadcrumb$,
          of(page).pipe(
            map((filteredPage): BreadcrumbMeta => ({ label: filteredPage.title, link: '/' + filteredPage.label })),
          ),
        );
      }),
    );
  }

  resolveAlternateLinks(): Observable<string> {
    return (this.basePageMetaResolver as DistBasePageMetaResolver).resolveAlternateLinks();
  }
}
