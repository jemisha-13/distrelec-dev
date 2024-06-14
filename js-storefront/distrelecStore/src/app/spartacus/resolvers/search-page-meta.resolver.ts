import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  CmsService,
  Page,
  PageBreadcrumbResolver,
  ProductSearchService,
  RoutingService,
  SearchPageMetaResolver,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';

@Injectable({
  providedIn: 'root',
})
export class DistSearchPageMetaResolver extends SearchPageMetaResolver implements PageBreadcrumbResolver {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  protected searchBreadcrumb$: Observable<BreadcrumbMeta> = this.productListComponentService.title$.pipe(
    withLatestFrom(
      this.routingService.getRouterState().pipe(
        filter((routerState) => !routerState.nextState),
        map((routerState) => routerState.state),
      ),
      this.translation.translate('metahd.search.label'),
    ),
    map(([title, state, label]) => ({
      label: `${label}: ${title}`,
      link: '/search',
      queryParams: { q: state.queryParams?.q, sid: state.queryParams?.sid },
    })),
  );

  protected categoryBreadcrumbs$: Observable<BreadcrumbMeta[]> = this.productSearchService.getResults().pipe(
    map((model) => model?.categoryBreadcrumbs ?? []),
    map(
      (breadcrumbs): BreadcrumbMeta[] =>
        breadcrumbs?.map((breadcrumb) => ({
          label: breadcrumb?.name,
          link: breadcrumb?.url,
        })),
    ),
  );

  constructor(
    protected routingService: RoutingService,
    protected productSearchService: ProductSearchService,
    protected productListComponentService: DistProductListComponentService,
    protected translation: TranslationService,
    protected basePageMetaResolver: DistBasePageMetaResolver,
    protected cmsService: CmsService,
    protected route: ActivatedRoute,
  ) {
    super(routingService, productSearchService, translation, basePageMetaResolver);
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([this.homeBreadcrumb$, this.searchBreadcrumb$, this.categoryBreadcrumbs$]).pipe(
      map((values) => values.flat()),
      map((breadcrumb) => breadcrumb?.filter((crumb) => crumb?.label !== undefined)),
    );
  }

  resolveImage(): Observable<string> {
    return this.cmsService.getCurrentPage().pipe(switchMap((page) => of(page?.properties.seo.metaImage as string)));
  }

  resolveTitle(): Observable<string> {
    return this.cmsService.getCurrentPage().pipe(
      filter<Page>(Boolean),
      switchMap((page) =>
        combineLatest([of(page), this.productListComponentService.searchQuery$]).pipe(
          switchMap(([pageData, searchQuery]) =>
            this.translation
              .translate('search.meta.title', { 0: searchQuery })
              .pipe(map((firstSegment) => `${firstSegment} - ${pageData.properties.seo.metaTitle}`)),
          ),
        ),
      ),
    );
  }

  resolveAlternateLinks(): Observable<string> {
    return this.basePageMetaResolver.resolveAlternateLinks();
  }
}
