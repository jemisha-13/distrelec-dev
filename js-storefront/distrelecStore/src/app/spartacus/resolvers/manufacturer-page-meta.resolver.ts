import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  CmsService,
  ContentPageMetaResolver,
  Page,
  PageBreadcrumbResolver,
  PageRobotsMeta,
  PageType,
  Priority,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { ManufactureService } from '@services/manufacture.service';
import { ManufacturerData } from '@model/manufacturer.model';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { BreadcrumbService } from '@services/breadcrumb.service';

@Injectable({
  providedIn: 'root',
})
export class DistManufacturerStoreDetailPageMetaResolver
  extends DistContentPageMetaResolver
  implements PageBreadcrumbResolver
{
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  protected manufacturerRootBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.manufacturer')
    .pipe(map((label) => ({ label, link: '/cms/manufacturer' })));

  protected categoryBreadcrumbs$: Observable<BreadcrumbMeta[]> = this.productListService.searchResults$.pipe(
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
    basePageMetaResolver: DistBasePageMetaResolver,
    cmsService: CmsService,
    translation: TranslationService,
    breadcrumbService: BreadcrumbService,
    private manufactureService: ManufactureService,
    private productListService: DistProductListComponentService,
  ) {
    super(basePageMetaResolver, cmsService, translation, breadcrumbService);
    this.pageType = 'DistManufacturerPage' as PageType;
    this.pageTemplate = 'ManufacturerStoreDetailPageTemplate';
  }

  getPriority(_page: Page): number {
    return Priority.HIGH;
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([
      this.homeBreadcrumb$,
      this.manufacturerRootBreadcrumb$,
      this.manufactureService.getCurrentManufacturerData().pipe(
        filter<ManufacturerData>(Boolean),
        map(
          (manufacture): BreadcrumbMeta => ({
            label: manufacture.name,
            link: manufacture.urlId,
          }),
        ),
      ),
      this.categoryBreadcrumbs$,
    ]).pipe(
      map((values) => values.flat()),
      map((breadcrumb) => breadcrumb?.filter((crumb) => crumb?.label !== undefined)),
    );
  }
}
