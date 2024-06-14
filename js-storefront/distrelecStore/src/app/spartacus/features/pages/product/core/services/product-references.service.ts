import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService, Product } from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ProductAvailability } from '@model/product-availability.model';
import { CarouselProducts, ProductReference, ProductReferences } from '@model/product-reference.model';
import { ProductAvailabilityService } from './product-availability.service';

import { ICustomProduct } from '@model/product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductReferencesService {
  alternativeData$: Observable<ProductReference[]>;

  protected alternative$: BehaviorSubject<CarouselProducts> = new BehaviorSubject<CarouselProducts>(null);

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private productAvailabilityService: ProductAvailabilityService,
  ) {}

  getAlternatives(productCode: string): Observable<ProductReference[]> {
    const url = this.occEndpoints.buildUrl(`products/${productCode}/alternatives`);
    this.alternativeData$ = this.http.get<{ references: ProductReference[] }>(url).pipe(
      map((response) => response.references),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
    return this.alternativeData$;
  }

  getReferencesByType(productCode: string, types?: string[], componentType?: string): Observable<ProductReference[]> {
    const url = this.occEndpoints.buildUrl(`products/${productCode}/references`);

    const params: Record<string, string> = {};
    if (types) {
      params.referenceType = types.join(',');
    }

    return this.http.get<{ references: ProductReference[] }>(url, { params }).pipe(
      map((response) => response.references),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getProductReferencesData(productCode?: string): Observable<ProductReferences> {
    return this.http
      .get<ProductReferences>(this.buildProductReferencesRequest() + '/' + productCode + '/product-references')
      .pipe(shareReplay({ bufferSize: 1, refCount: true }))
      .pipe(
        tap((data: any) => {
          //TODO we can refactor this only used for getStockData
          this.alternative$.next(data.alternatives);
        }),
      );
  }

  //TODO we can refactor this and use getCarouselStock
  getAlternativeStock(): Observable<ProductAvailability[]> {
    return this.getStockData(this.alternative$);
  }

  //TODO we can refactor this and use getCarouselStock
  getStockData(observable: Observable<any>) {
    return observable.pipe(
      filter(Boolean),
      map((data: any) => data.carouselProducts.map((productData: Product) => productData.code)),
      switchMap((productCodes) => this.productAvailabilityService.getProductAvailabilities(productCodes)),
    );
  }

  getCarouselStock(carouselProducts): Observable<ProductAvailability[]> {
    const productCodes = carouselProducts?.map((carouselProduct) => carouselProduct?.code);

    if (productCodes?.length) {
      return of(productCodes).pipe(
        take(1),
        switchMap((productCode: string[]) => this.productAvailabilityService.getProductAvailabilities(productCode)),
      );
    }

    return of(null);
  }

  getCarouselItemStock(product: ICustomProduct): Observable<ProductAvailability> {
    const productCode = product?.code;

    if (productCode) {
      return this.productAvailabilityService.getAvailability(productCode);
    }

    return of(null);
  }

  protected buildProductReferencesRequest(): string {
    return this.occEndpoints.buildUrl('products');
  }
}
