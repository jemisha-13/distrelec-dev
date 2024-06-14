import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap, take } from 'rxjs/operators';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductDataService } from '../../core/services/product-data.service';
import { GlobalMessageService, GlobalMessageType, WindowRef } from '@spartacus/core';
import { Translatable } from '@spartacus/core/src/i18n/translatable';

@Injectable({
  providedIn: 'root',
})
export class ProductAvailabilityGuard  {
  constructor(
    private router: Router,
    private productAvailabilityService: ProductAvailabilityService,
    private window: WindowRef,
    private productDataService: ProductDataService,
    private globalMessageService: GlobalMessageService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const productCode = route.params?.productCode;

    return this.getProductStatus(productCode).pipe(
      switchMap((status): Observable<boolean | UrlTree> => {
        if (status === '99') {
          this.showErrorAfterNavigation({ key: 'product.message.status.99', params: { 0: productCode } });
          return of(this.router.parseUrl('/'));
        }

        if (this.isSmarteditPreview(route) || !this.window.isBrowser()) {
          return of(true);
        }

        return this.productAvailabilityService.getAvailability(productCode, true).pipe(
          take(1),
          map((availability) => Boolean(availability)),
          catchError((err) => {
            console.error(err);
            return of(this.router.parseUrl('/not-found'));
          }),
        );
      }),
    );
  }

  private isSmarteditPreview(route: ActivatedRouteSnapshot) {
    return route.url[0]?.path === 'cx-preview' && route.queryParams.cmsTicketId;
  }

  private getProductStatus(productCode: string): Observable<string> {
    return this.productDataService.getProductData(productCode).pipe(
      take(1),
      map((productData) => productData.salesStatus),
    );
  }

  private showErrorAfterNavigation(errorMessage: Translatable): void {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        take(1),
      )
      .subscribe(() => {
        this.globalMessageService.add(errorMessage, GlobalMessageType.MSG_TYPE_ERROR, 10000);
      });
  }
}
