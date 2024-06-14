import { Injectable } from '@angular/core';
import { Product, ProductService } from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export enum ProductScope {
  LIST = 'list',
  DETAILS = 'details',
  ATTRIBUTES = 'attributes',
  VARIANTS = 'variants',
}
@Injectable({
  providedIn: 'root',
})
export class FeaturedProductService {
  featuredProducts$: Observable<Product | [unknown]>;

  constructor(private productService: ProductService) {}

  getProduct(productCode: string): Observable<Product | undefined[]> {
    return this.productService.get(productCode, ProductScope.DETAILS);
  }

  getProducts(productCodes: string[]): Observable<Product[]> {
    return combineLatest(
      productCodes.map((productCode) => this.productService.get(productCode, ProductScope.DETAILS)),
    ).pipe(map((responses) => responses));
  }
}
