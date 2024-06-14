import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  CmsService,
  PageBreadcrumbResolver,
  RoutingService,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { BreadcrumbService } from '@services/breadcrumb.service';
import { decode } from '@helpers/encoding';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';

@Injectable({
  providedIn: 'root',
})
export class DistEmptySearchPageMetaResolver extends DistContentPageMetaResolver implements PageBreadcrumbResolver {
  pageTemplate = 'SearchResultsEmptyPageTemplate';

  protected searchBreadcrumb$: Observable<BreadcrumbMeta> = combineLatest(
    this.routingService.getRouterState().pipe(
      filter((routerState) => !routerState.nextState),
      map((routerState) => routerState.state),
    ),
    this.cmsService.getCurrentPage().pipe(map((page) => page?.title)),
  ).pipe(
    map(([state, label]) => ({
      label: `${label}: ${decode(state.queryParams?.q)}`,
      link: '/search',
      robotTag: 'NOINDEX_FOLLOW',
      queryParams: { q: state.queryParams?.q },
    })),
  );

  constructor(
    basePageMetaResolver: DistBasePageMetaResolver,
    cmsService: CmsService,
    protected translation: TranslationService,
    protected breadCrumbService: BreadcrumbService,
    protected routingService: RoutingService,
  ) {
    super(basePageMetaResolver, cmsService, translation, breadCrumbService);
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([this.homeBreadcrumb$, this.searchBreadcrumb$]).pipe(
      map((breadcrumb) => breadcrumb?.filter((crumb) => crumb?.label !== undefined)),
    );
  }

  resolveImage(): Observable<string> {
    return this.cmsService.getCurrentPage().pipe(switchMap((page) => of(page?.properties.seo.metaImage as string)));
  }
}
