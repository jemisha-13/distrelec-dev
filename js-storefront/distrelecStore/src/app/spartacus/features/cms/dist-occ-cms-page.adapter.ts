import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  CMS_PAGE_NORMALIZER,
  CmsStructureModel,
  ConverterService,
  DEFAULT_SCOPE,
  OccCmsPageAdapter,
  OccCmsPageRequest,
  OccEndpointsService,
  PageContext,
  PageType,
  Product,
  ProductService,
  SMART_EDIT_CONTEXT,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';

declare module '@spartacus/core' {
  interface OccCmsPageRequest {
    categoryCode?: string;
    manufacturerCode?: string;
    productCode?: string;
    smartedit?: boolean;
  }
}

@Injectable({
  providedIn: 'root',
})
export class DistOccCmsPageAdapter extends OccCmsPageAdapter {
  constructor(
    http: HttpClient,
    occEndpoints: OccEndpointsService,
    converter: ConverterService,
    protected productService: ProductService,
  ) {
    super(http, occEndpoints, converter);
  }

  /**
   * @override returns the OCC CMS page data for the given context and converts
   * the data by any configured `CMS_PAGE_NORMALIZER`.
   *
   * Injects additional manufacturerCode and categoryCode fields in the case of a product page.
   */
  override load(pageContext: PageContext): Observable<CmsStructureModel> {
    if (pageContext.type === PageType.PRODUCT_PAGE) {
      return combineLatest([
        this.productService.get(pageContext.id),
        this.productService.hasError(pageContext.id, DEFAULT_SCOPE),
      ]).pipe(
        filter(([product, hasError]) => Boolean(product || (!product && hasError))),
        switchMap(([product]) => {
          if (!product) {
            // Redirect to 404 page
            throw new Error('Product not found');
          }

          const params = this.getPagesRequestParams(pageContext, product);
          const endpoint = this.occEndpoints.buildUrl('pages', { queryParams: params });
          return this.http.get(endpoint, { headers: this.headers });
        }),
        this.converter.pipeable(CMS_PAGE_NORMALIZER),
      );
    }

    return super.load(pageContext);
  }

  protected override getPagesRequestParams(pageContext: PageContext, product?: Product): OccCmsPageRequest {
    const httpParams = super.getPagesRequestParams(pageContext);

    if (pageContext.type === PageType.PRODUCT_PAGE) {
      httpParams.manufacturerCode = product?.distManufacturer?.code;
      httpParams.categoryCode = product?.categories[0]?.code;
      httpParams.productCode = httpParams.code;
      delete httpParams.code;
    }

    if (pageContext.id === SMART_EDIT_CONTEXT) {
      httpParams.smartedit = true;
    }

    return httpParams;
  }
}
