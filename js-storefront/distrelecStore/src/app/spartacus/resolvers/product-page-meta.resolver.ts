import { Injectable } from '@angular/core';
import {
  BasePageMetaResolver,
  BreadcrumbMeta,
  Occ,
  PageLinkService,
  PageRobotsMeta,
  PageType,
  Product,
  ProductPageMetaResolver,
  ProductService,
  RoutingService,
  TranslationService,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, pluck, switchMap } from 'rxjs/operators';
import { Breadcrumb } from '@model/breadcrumb.model';
import { SeoMetaData } from '@model/page.model';

@Injectable({
  providedIn: 'root',
})
export class DistProductPageMetaResolver extends ProductPageMetaResolver {
  protected homeBreadcrumb$: Observable<BreadcrumbMeta> = this.translation
    .translate('breadcrumb.home')
    .pipe(map((label) => ({ label, link: '/' })));

  constructor(
    protected routingService: RoutingService,
    protected productService: ProductService,
    protected translation: TranslationService,
    protected basePageMetaResolver: BasePageMetaResolver,
    protected pageLinkService: PageLinkService,
  ) {
    super(routingService, productService, translation, basePageMetaResolver, pageLinkService);
    this.pageType = PageType.PRODUCT_PAGE;
  }

  resolveDescription(): Observable<string> {
    return this.resolveProductMeta().pipe(map((meta) => meta.metaDescription));
  }

  resolveTitle(): Observable<string> {
    return this.resolveProductMeta().pipe(map((meta) => meta.metaTitle));
  }

  resolveHeading(): Observable<string> {
    return this.product$.pipe(map((product) => product.name));
  }

  resolveRobots(): Observable<PageRobotsMeta[]> {
    return this.resolveProductMeta().pipe(map((meta) => meta.robots));
  }

  resolveImage(): Observable<string> {
    return this.resolveProductMeta().pipe(map((meta) => meta.metaImage));
  }

  resolveBreadcrumbs(): Observable<BreadcrumbMeta[]> {
    return combineLatest([
      this.homeBreadcrumb$,
      this.product$.pipe(
        map((categoryData) =>
          categoryData.breadcrumbs.map((breadcrumb) => ({
            label: breadcrumb.name,
            link: breadcrumb.url,
          })),
        ),
      ),
    ]).pipe(map((values) => values.flat()));
  }

  resolveCanonicalUrl(): Observable<string> {
    return this.basePageMetaResolver.resolveCanonicalUrl();
  }

  resolveAlternateLinks(): Observable<string> {
    return this.resolveProductMeta().pipe(map((meta) => meta.alternateHreflangUrls));
  }

  private resolveProductMeta(): Observable<SeoMetaData> {
    return this.routingService.getPageContext().pipe(
      switchMap((pageContext) => this.productService.get(pageContext.id)),
      filter((product) => Boolean(product)),
      map((product) => ({
        ...product.metaData,
        robots: this.normalizeRobots(product.metaData.robots),
      })),
    );
  }

  private normalizeRobots(source: string): PageRobotsMeta[] {
    const robots: PageRobotsMeta[] = [];
    switch (source) {
      case Occ.PageRobots.INDEX_FOLLOW:
        robots.push(PageRobotsMeta.INDEX);
        robots.push(PageRobotsMeta.FOLLOW);
        break;
      case Occ.PageRobots.NOINDEX_FOLLOW:
        robots.push(PageRobotsMeta.NOINDEX);
        robots.push(PageRobotsMeta.FOLLOW);
        break;
      case Occ.PageRobots.INDEX_NOFOLLOW:
        robots.push(PageRobotsMeta.INDEX);
        robots.push(PageRobotsMeta.NOFOLLOW);
        break;
      case Occ.PageRobots.NOINDEX_NOFOLLOW:
        robots.push(PageRobotsMeta.NOINDEX);
        robots.push(PageRobotsMeta.NOFOLLOW);
        break;
    }

    return robots;
  }
}
