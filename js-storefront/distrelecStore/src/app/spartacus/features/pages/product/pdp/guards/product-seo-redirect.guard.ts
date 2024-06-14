import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ProductDataService } from '../../core/services/product-data.service';
import { RedirectCountService } from '@services/redirect-count.service';

@Injectable({
  providedIn: 'root',
})
export class ProductSeoRedirectGuard {
  constructor(
    private router: Router,
    private productDataService: ProductDataService,
    private redirectCount: RedirectCountService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> | boolean {
    if (this.redirectCount.exceeds(1)) {
      this.redirectCount.reset();
      return true;
    }

    const productCode = route.params?.productCode;
    return this.productDataService.getProductData(productCode).pipe(
      filter((productData) => !!productData),
      map((productData) => {
        const currentPath = '/' + route.url.map((u) => u.path).join('/');
        if (currentPath === productData.url) {
          this.redirectCount.reset();
          return true;
        }

        this.redirectCount.increment();
        return this.router.createUrlTree([productData.url], {
          queryParams: route.queryParams,
        });
      }),
    );
  }
}
