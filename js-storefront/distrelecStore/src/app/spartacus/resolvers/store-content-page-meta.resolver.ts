import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  CmsService,
  Page,
  PageType,
  ProductSearchService,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, EMPTY, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { BreadcrumbService } from '@services/breadcrumb.service';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';

@Injectable({
  providedIn: 'root',
})
export class DistStoreContentPageMetaResolver extends DistContentPageMetaResolver {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  protected categoryBreadcrumbs$: Observable<BreadcrumbMeta[]> = this.productSearchService.getResults().pipe(
    map((model) => model?.categoryBreadcrumbs ?? []),
    map(
      (breadcrumbs): BreadcrumbMeta[] =>
        breadcrumbs?.map((breadcrumb) => ({
          label: breadcrumb?.name,
          link: this.adjustCategoryUrl(breadcrumb?.url),
        })),
    ),
  );

  constructor(
    basePageMetaResolver: DistBasePageMetaResolver,
    protected cmsService: CmsService,
    protected translation: TranslationService,
    protected breadCrumbService: BreadcrumbService,
    protected productSearchService: ProductSearchService,
  ) {
    super(basePageMetaResolver, cmsService, translation, breadCrumbService);
    this.pageType = PageType.CONTENT_PAGE;
    this.pageTemplate = 'StorePageTemplate';
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[] | undefined> {
    return this.cmsService.getCurrentPage().pipe(
      filter<Page>(Boolean),
      switchMap((page) => {
        if (this.breadCrumbService.isBreadcrumbHiddenOnPage(page)) {
          return EMPTY;
        }
        return combineLatest(
          this.homeBreadcrumb$,
          of(page).pipe(
            map(
              (filteredPage): BreadcrumbMeta => ({
                label: filteredPage.title,
                link: this.getPageUrlPrefix(filteredPage.label) + filteredPage.label,
              }),
            ),
          ),
          this.categoryBreadcrumbs$,
        ).pipe(
          map((values) => values.flat()),
          map((breadcrumb) => breadcrumb?.filter((crumb) => crumb?.label !== undefined)),
        );
      }),
    );
  }

  private getPageUrlPrefix(label) {
    if (label === 'new') {
      return '/cms/';
    }
    return '/';
  }

  private adjustCategoryUrl(categoryUrl) {
    if (categoryUrl.startsWith('/new')) {
      return '/cms' + categoryUrl;
    }
    return categoryUrl;
  }
}
