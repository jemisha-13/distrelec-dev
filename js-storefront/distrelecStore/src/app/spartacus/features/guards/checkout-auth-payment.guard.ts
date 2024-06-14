import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { CheckoutService } from '@services/checkout.service';
import { AuthRedirectService, AuthService, AuthStorageService, SemanticPathService } from '@spartacus/core';
import { combineLatest, from, Observable, of } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { DistCartService } from '@services/cart.service';
import { AppendComponentService } from '@services/append-component.service';
import { AbstractCheckoutGuard } from '@features/guards/abstract-checkout-guard';
import { HttpErrorResponse } from '@angular/common/http';
import { Cart } from '@spartacus/cart/base/root';
import { DistrelecBasesitesService } from '@services/basesites.service';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users.
 */
@Injectable({
  providedIn: 'root',
})
export class CheckoutPaymentAuthGuard extends AbstractCheckoutGuard  {
  constructor(
    protected authRedirectService: AuthRedirectService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    protected authStorageService: AuthStorageService,
    protected cartService: DistCartService,
    private authService: AuthService,
    private checkoutService: CheckoutService,
    private appendComponentService: AppendComponentService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {
    super(authRedirectService, router, semanticPathService, authStorageService, cartService);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.cartService.getCartTokenFromLocalStorage().pipe(
      switchMap((token) => {
        if (!token) {
          return this.redirectToPage('cart');
        } else {
          return this.subscribeLatestCart(state.url, token);
        }
      }),
    );
  }

  subscribeLatestCart(url: string, cartId: string): Observable<UrlTree | boolean> {
    return combineLatest([
      this.authService.isUserLoggedIn(),
      this.getCartData(),
      this.distBaseSiteService.isAddToCartDisabledForActiveSite(),
    ]).pipe(
      filter(([isLoggedIn, activeCart, isAddToCartDisabled]) =>
        isLoggedIn ? activeCart.code === cartId : activeCart.guid === cartId,
      ),
      switchMap(([isLoggedIn, activeCart, isAddToCartDisabled]) => {
        if (this.isNotLoggedIn(isLoggedIn)) {
          return this.redirectToPage('checkoutLogin');
        }
        if (this.cartService.isNotAllowedToEnterCheckout(activeCart, isAddToCartDisabled)) {
          return this.redirectToPage('cart');
        }
        if (this.cartService.hasUnallowedBackorder(activeCart)) {
          return this.redirectToPage('backorder');
        }
        if (this.cartService.isBelowMinimumOrderValue(activeCart)) {
          this.router.navigate(['cart']).then(() => {
            this.appendComponentService.appendMOVpopup(
              activeCart.movLimit,
              activeCart.subTotal.value,
              activeCart.subTotal.currencyIso,
            );
          });
          return of(false);
        }
        return this.handleValidatePaymentPage(activeCart);
      }),
      catchError(() => this.redirectToPage('cart')),
    );
  }

  private handleValidatePaymentPage(activeCart: Cart): Observable<boolean | UrlTree> {
    return this.checkoutService.validateDeliveryPage().pipe(
      switchMap(() => this.stayOnSamePage()),
      catchError((err) => {
        if (this.isMOVError(err)) {
          this.router.navigate(['cart']).then(() => {
            this.appendComponentService.appendMOVpopup(
              activeCart.movLimit,
              activeCart.subTotal.value,
              activeCart.subTotal.currencyIso,
            );
          });
        }
        if (this.isUnallowedBackorder(err)) {
          return from(this.redirectToPage('backorder'));
        }
        return from(this.redirectToPage('checkoutAddress'));
      }),
    );
  }

  private isMOVError(response: HttpErrorResponse): boolean {
    return response.error?.errors.some((e) => e.message === 'checkout.invalid.mov');
  }

  private isUnallowedBackorder(response: HttpErrorResponse): boolean {
    return response.error?.errors.some((e) => e.message === 'checkout.invalid.backorder');
  }
}
