import { Injectable } from '@angular/core';
import { BreadcrumbMeta, CmsService, PageBreadcrumbResolver, PageType, TranslationService } from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CategoriesService } from '@services/categories.service';
import { Breadcrumb } from '@model/breadcrumb.model';
import { ProductFamilyService } from '@features/pages/product/core/services/product-family.service';
import { ProductFamilyData } from '@model/product-family.model';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';
import { BreadcrumbService } from '@services/breadcrumb.service';

@Injectable({
  providedIn: 'root',
})
export class DistFamilyPageMetaResolver extends DistContentPageMetaResolver implements PageBreadcrumbResolver {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  constructor(
    basePageMetaResolver: DistBasePageMetaResolver,
    cmsService: CmsService,
    translation: TranslationService,
    breadcrumbService: BreadcrumbService,
    private familyService: ProductFamilyService,
    private categoryService: CategoriesService,
  ) {
    super(basePageMetaResolver, cmsService, translation, breadcrumbService);
    this.pageType = 'ProductFamilyPage' as PageType;
    this.pageTemplate = 'ProductFamilyPageTemplate';
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([
      this.homeBreadcrumb$,
      this.categoryService.getCurrentCategoryData().pipe(
        map((categoryData) => categoryData?.breadcrumbs),
        filter<Breadcrumb[]>(Boolean),
        map((breadcrumbs): BreadcrumbMeta[] =>
          breadcrumbs?.map((breadcrumb) => ({
            label: breadcrumb.name,
            link: breadcrumb.url,
          })),
        ),
      ),
      this.familyService.getCurrentFamilyData().pipe(
        filter<ProductFamilyData>(Boolean),
        map((family): BreadcrumbMeta => ({ label: family.name, link: '/pf/' + family.code })),
      ),
    ]).pipe(map((values) => values.flat()));
  }
}
