import { Injectable } from '@angular/core';
import {
  BreadcrumbMeta,
  CategoryPageMetaResolver,
  CmsService,
  Page,
  PageRobotsMeta,
  PageType,
  ProductSearchService,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { CategoriesService } from '@services/categories.service';
import { Breadcrumb } from '@model/breadcrumb.model';
import { ActivatedRoute, UrlSerializer } from '@angular/router';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { DistBasePageMetaResolver } from './dist-base-page-meta.resolver';

@Injectable({
  providedIn: 'root',
})
export class DistCategoryPageMetaResolver extends CategoryPageMetaResolver {
  cmsPage$ = this.cms.getCurrentPage().pipe(filter<Page>(Boolean));

  categoryLevel$ = this.categoryService
    .getCurrentCategoryData()
    .pipe(map((category) => category?.sourceCategory?.level));

  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  protected categoryBreadcrumbs$: Observable<BreadcrumbMeta[]> = this.productSearchService.getResults().pipe(
    map((model) => model?.categoryBreadcrumbs ?? []),
    map((breadcrumbs): BreadcrumbMeta[] =>
      breadcrumbs?.map((breadcrumb) => ({
        label: breadcrumb?.name,
        link: this.adjustCategoryBreadcrumbUrl(breadcrumb.url),
      })),
    ),
  );

  constructor(
    productSearchService: ProductSearchService,
    cms: CmsService,
    translation: TranslationService,
    basePageMetaResolver: DistBasePageMetaResolver,
    private categoryService: CategoriesService,
    private productListService: DistProductListComponentService,
    private route: ActivatedRoute,
    private urlSerializer: UrlSerializer,
  ) {
    super(productSearchService, cms, translation, basePageMetaResolver);
    this.pageType = PageType.CATEGORY_PAGE;
  }

  resolveTitle(): Observable<string> {
    return this.cmsPage$.pipe(
      switchMap((cmsPage) =>
        this.categoryLevel$.pipe(
          switchMap((level) => {
            const title = cmsPage?.properties?.seo?.metaTitle ?? '';
            try {
              if (level === 1) {
                return of(title);
              } else {
                const titleFormatted = title.replace('- ', '');
                return of(titleFormatted);
              }
            } catch (e) {
              return of(title);
            }
          }),
        ),
      ),
    );
  }

  resolveDescription(): Observable<string> {
    return this.cmsPage$.pipe(map((page) => page?.properties?.seo?.metaDescription as string));
  }

  resolveImage(): Observable<string> {
    return this.cmsPage$.pipe(map((page) => page?.properties?.seo?.metaImage as string));
  }

  resolveRobots(): Observable<PageRobotsMeta[]> {
    return this.basePageMetaResolver.resolveRobots();
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return this.productListService.isPlpActive$.pipe(
      switchMap((isPlpActive) =>
        isPlpActive ? this.resolvePLPActiveBreadcrumbs() : this.resolveCategoryBreadcrumbs(),
      ),
    );
  }

  resolveAlternateLinks(): Observable<string> {
    return (this.basePageMetaResolver as DistBasePageMetaResolver).resolveAlternateLinks();
  }

  private resolvePLPActiveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([this.homeBreadcrumb$, this.categoryBreadcrumbs$]).pipe(
      map((values) => values.flat()),
      map((breadcrumb) => breadcrumb?.filter((crumb) => crumb?.label !== undefined)),
    );
  }

  private resolveCategoryBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([
      this.homeBreadcrumb$,
      this.categoryService.getCurrentCategoryData().pipe(
        map((categoryData) => categoryData?.breadcrumbs),
        filter<Breadcrumb[]>(Boolean),
        map((breadcrumbs): BreadcrumbMeta[] =>
          breadcrumbs?.map((breadcrumb) => ({
            label: breadcrumb?.name,
            link: this.adjustCategoryBreadcrumbUrl(breadcrumb?.url),
          })),
        ),
      ),
    ]).pipe(map((values) => values.flat()));
  }

  private adjustCategoryBreadcrumbUrl(categoryUrl: string): string {
    const params = this.route.snapshot.queryParams;
    const urlTree = this.urlSerializer.parse(categoryUrl);
    if (params.useTechnicalView === 'true') {
      urlTree.queryParams.useTechnicalView = 'true';
      return urlTree.toString();
    }
    return urlTree.toString();
  }
}
