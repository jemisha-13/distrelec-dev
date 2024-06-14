import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import {
  AuthRedirectService,
  AuthService,
  AuthStorageService,
  BaseSiteService,
  SemanticPathService,
} from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap, take, tap } from 'rxjs/operators';
import { LocalStorageService } from '@services/local-storage.service';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';

/**
 * Checks if loggedIn or Guest user have valid orderCode, if so then they can get on page
 * Urls: "checkout/orderConfirmation" and "checkout/orderConfirmation/:id"
 */
@Injectable({
  providedIn: 'root',
})
export class CheckoutOrderConfirmationAuthGuard  {
  orderCode: string;
  isCustomerGuest: boolean;

  constructor(
    protected authService: AuthService,
    protected authRedirectService: AuthRedirectService,
    protected baseSiteService: BaseSiteService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    private localStorage: LocalStorageService,
    protected authStorageService: AuthStorageService,
    private orderService: DistOrderService,
  ) {}

  redirectToPage(page): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    return this.router.parseUrl(this.semanticPathService.get(page));
  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    this.orderCode = route.params.orderCode ?? this.localStorage.getItem('orderCode');
    this.isCustomerGuest = this.authStorageService.getItem('guest');

    // If orderCode exists in url as path param, continue
    // Else redirect to homepage
    if (this.orderCode) {
      return this.authService.isUserLoggedIn().pipe(
        take(1),
        switchMap((isLoggedIn) => {
          // If user is not anonymous (logged in or guest) and when orderCode exists in url param or localStorage, proceed
          // Else redirect user to homepage
          if (isLoggedIn || this.isCustomerGuest) {
            return this.orderService
              .getUserOrder(this.isCustomerGuest ? 'anonymous' : 'current', this.orderCode, 'DEFAULT')
              .pipe(
                tap(() => this.resetIsCustomerGuestFlag()),
                map((order) => !!order),
                catchError(() => of(this.redirectToPage('home'))),
              );
          } else {
            return of(this.redirectToPage('home'));
          }
        }),
      );
    } else {
      return of(this.redirectToPage('home'));
    }
  }

  resetIsCustomerGuestFlag(): void {
    this.isCustomerGuest = false;
  }
}
