import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { DistCartService } from '@services/cart.service';
import { AuthRedirectService, AuthService, BaseSiteService, SemanticPathService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { Observable, of } from 'rxjs';
import { catchError, first, map, switchMap, take } from 'rxjs/operators';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users.
 * Url: "/login/checkout"
 */
@Injectable({
  providedIn: 'root',
})
export class BackorderAuthGuard  {
  isValidOrUrl: boolean | UrlTree;

  constructor(
    protected authService: AuthService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    protected authRedirectService: AuthRedirectService,
    protected baseSiteService: BaseSiteService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
  ) {}

  redirectToPage(page): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    return this.router.parseUrl(this.semanticPathService.get(page));
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
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
            map((cartData) => {
              // @ts-ignore
              this.cartStoreService.setCartState(cartData);
              return this.validateUserAndCart(cartData, isLoggedIn);
            }),
            catchError(() => of(this.redirectToPage('cart'))),
          );
        } else if (cartToken && this.cartStoreService.getCartUser()) {
          // If cartToken exists and cart state is set, validate cart accordingly
          return of(this.validateUserAndCart(this.cartStoreService.getCartState().value, isLoggedIn));
        } else {
          return of((this.isValidOrUrl = this.redirectToPage('cart')));
        }
      }),
    );
  }

  validateUserAndCart(cartData, isLoggedIn): boolean | UrlTree {
    // If there are 1 on more items in the cart, if user is logged in redirect to checkout/delivery
    // // If user is not logged in, he remains on same page (checkout/login)
    // Otherwise when there are no items in cart, redirect user to cart page
    if (cartData.entries?.length > 0) {
      if (isLoggedIn || this.cartStoreService.isCartUserGuest()) {
        // Stay on the same page
        this.authRedirectService.saveCurrentNavigationUrl();
        this.isValidOrUrl = true;
      } else {
        this.isValidOrUrl = this.redirectToPage('checkoutLogin');
      }
    } else {
      this.isValidOrUrl = this.redirectToPage('cart');
    }
    return this.isValidOrUrl;
  }
}
