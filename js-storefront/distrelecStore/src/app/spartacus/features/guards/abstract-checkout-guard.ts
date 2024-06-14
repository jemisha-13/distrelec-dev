import { Injectable } from '@angular/core';
import { AuthRedirectService, AuthStorageService, SemanticPathService } from '@spartacus/core';
import { Router, UrlTree } from '@angular/router';
import { combineLatest, EMPTY, Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { DistCartService } from '@services/cart.service';
import { Cart } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export abstract class AbstractCheckoutGuard {
  protected constructor(
    protected authRedirectService: AuthRedirectService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    protected authStorageService: AuthStorageService,
    protected cartService: DistCartService,
  ) {}

  protected redirectToPage(page: string): Observable<UrlTree> {
    this.authRedirectService.saveCurrentNavigationUrl();
    return of(this.router.parseUrl(this.semanticPathService.get(page)));
  }

  protected stayOnSamePage(): Observable<boolean> {
    return of(true);
  }

  protected isNotLoggedIn(isLoggedIn: boolean): boolean {
    return !isLoggedIn && !this.authStorageService.getItem('guest');
  }

  protected getCartData(): Observable<Cart> {
    return combineLatest([
      this.cartService.getCartTokenFromLocalStorage(),
      this.cartService.getCartDataFromStore(),
    ]).pipe(
      switchMap(([cartToken, cartData]) => {
        if (cartToken) {
          // if cart state already has latest data, e.g. user gets to checkout from the cart page
          if (cartData.code === cartToken || cartData.guid === cartToken) {
            return of(cartData);
          } else {
            // if user gets to cart page directly by entering /checkout/... in their browser
            return this.cartService.returnCartDataFromDB(cartToken);
          }
        } else {
          // if no cart token exist, user must be redirected back to cart
          this.redirectToPage('cart');
          return EMPTY;
        }
      }),
    );
  }
}
