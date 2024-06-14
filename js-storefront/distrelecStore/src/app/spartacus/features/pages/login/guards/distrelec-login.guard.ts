import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { DistCartService } from '@services/cart.service';
import {
  AuthRedirectService,
  AuthService,
  BaseSiteService,
  SemanticPathService,
  StatePersistenceService,
} from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { first, map, switchMap, take } from 'rxjs/operators';
import { CustomerType } from '@model/site-settings.model';
import { CmsPageGuard } from '@spartacus/storefront';
import { Cart } from '@spartacus/cart/base/root';

/**
 * Checks if there is currently logged in or user is GUEST
 * Use to protect pages dedicated only for logged in and GUEST users
 * This guard overrides "LoginGuard" Spartacus guard in "src/app/spartacus/features/pages/login/login.module.ts"
 */
@Injectable({
  providedIn: 'root',
})
export class DistrelecLoginGuard  {
  constructor(
    protected authService: AuthService,
    private cartService: DistCartService,
    protected authRedirectService: AuthRedirectService,
    protected baseSiteService: BaseSiteService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    protected statePersistenceService: StatePersistenceService,
    protected cmsPageGuard: CmsPageGuard,
  ) {}

  redirectToPage(page): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    return this.router.parseUrl(this.semanticPathService.get(page));
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().pipe(
      take(1),
      switchMap((isLoggedIn) =>
        this.baseSiteService.getActive().pipe(
          first(),
          map((siteId) => ({ isLoggedIn, cartToken: this.getStoredCartToken(siteId) })),
        ),
      ),
      switchMap(({ isLoggedIn, cartToken }) => {
        // If user is logged in, redirect him to homepage (logic from core LoginGuard)
        if (isLoggedIn) {
          return of(this.redirectToPage('home'));
        }

        // If there is cart token it means user can be GUEST
        // Else user is not logged in neither GUEST, so it can stay on page
        if (!cartToken) {
          return this.cmsPageGuard.canActivate(route, state);
        }

        // If user is guest, redirect to homepage, otherwise stay on same page
        return this.cartService
          .returnCartDataFromDB(cartToken)
          .pipe(
            switchMap((cartData: Cart) =>
              this.isGuestCart(cartData)
                ? of(this.redirectToPage('home'))
                : this.cmsPageGuard.canActivate(route, state),
            ),
          );
      }),
    );
  }

  private getStoredCartToken(siteId: string): string {
    return (
      this.statePersistenceService.readStateFromStorage<{ active: string }>({ key: 'cart', context: siteId })?.active ??
      ''
    );
  }

  private isGuestCart(cartData: Cart): boolean {
    return cartData.user.type === CustomerType.GUEST;
  }
}
