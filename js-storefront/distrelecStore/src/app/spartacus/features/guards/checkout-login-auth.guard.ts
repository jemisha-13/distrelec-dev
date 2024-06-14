import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { DistCartService } from '@services/cart.service';
import { CheckoutService } from '@services/checkout.service';
import { PasswordService } from '@services/password.service';
import { AuthRedirectService, AuthService, SemanticPathService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { Observable, of } from 'rxjs';
import { catchError, first, map, switchMap, take } from 'rxjs/operators';
import { AppendComponentService } from '@services/append-component.service';
import { Cart } from '@spartacus/cart/base/root';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users.
 * Url: "/login/checkout"
 */
@Injectable({
  providedIn: 'root',
})
export class CheckoutLoginAuthGuard  {
  constructor(
    protected authService: AuthService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    protected authRedirectService: AuthRedirectService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    private checkoutService: CheckoutService,
    private passwordService: PasswordService,
    private appendComponentService: AppendComponentService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    if (state.root?.queryParams?.token && state.url.includes('checkout/pw/change')) {
      this.authRedirectService.saveCurrentNavigationUrl();
      // Stay on the same page
      return this.passwordService.isChangePasswordTokenValid(state.root.queryParams.token).pipe(
        map((isTokenValid) => {
          if (isTokenValid) {
            this.checkoutService.resetPwdToken = state.root.queryParams.token;
          } else {
            this.checkoutService.isTokenExpired = true;
          }

          return true;
        }),
      );
    } else {
      return this.authService.isUserLoggedIn().pipe(
        take(1),
        // Get the cart token from local storage and return if user is logged in or not
        switchMap((isLoggedIn) =>
          this.cartService.getCartTokenFromLocalStorage().pipe(
            first(),
            map((cartToken) => ({ isLoggedIn, cartToken: cartToken || '' })),
          ),
        ),
        switchMap(({ isLoggedIn, cartToken }) => {
          // If cartToken exists but cart state is not set, call cart endpoint and validate
          if (cartToken && !this.cartStoreService.getCartUser()) {
            return this.cartService.returnCartDataFromDB(cartToken).pipe(
              map((cartData: Cart) => {
                this.cartStoreService.setCartState(cartData);
                return this.validateUserAndCart(cartData, isLoggedIn);
              }),
              catchError(() => of(this.redirectToPage('cart'))),
            );
          } else if (cartToken && this.cartStoreService.getCartUser()) {
            // If cartToken exists and cart state is set, validate cart accordingly
            return of(this.validateUserAndCart(this.cartStoreService.getCartState().value, isLoggedIn));
          } else {
            return of(this.redirectToPage('cart'));
          }
        }),
      );
    }
  }

  validateUserAndCart(cartData, isLoggedIn): boolean | UrlTree {
    if (!cartData.entries?.length) {
      return this.redirectToPage('cart');
    }

    if (isLoggedIn || this.cartStoreService.isCartUserGuest()) {
      if (cartData.hasUnallowedBackorder) {
        return this.redirectToPage('backorder');
      }
      if (this.cartService.isBelowMinimumOrderValue(cartData)) {
        this.router.navigate(['cart']).then(() => {
          this.appendComponentService.appendMOVpopup(
            cartData.movLimit,
            cartData.subTotal.value,
            cartData.subTotal.currencyIso,
          );
        });
        return false;
      }
      return this.redirectToPage('checkoutAddress');
    }
    return this.stayOnSamePage();
  }

  redirectToPage(page): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    return this.router.parseUrl(this.semanticPathService.get(page));
  }

  stayOnSamePage(): boolean {
    this.authRedirectService.saveCurrentNavigationUrl();
    return true;
  }
}
