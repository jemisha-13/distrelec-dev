import { Injectable, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { LoginErrorMessage } from '@model/auth.model';
import { DistrelecAuthService } from '@services/distrelec-auth.service';
import { SiteConfigService } from '@services/siteConfig.service';
import {
  BaseSiteService,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  OCC_USER_ID_CURRENT,
  OccEndpointsService,
  Translatable,
  User,
  UserIdService,
} from '@spartacus/core';
import { UserAccountFacade } from '@spartacus/user/account/root';
import { CartStoreService } from '@state/cartState.service';
import type { TokenResponse } from 'angular-oauth2-oidc/types';
import { BehaviorSubject, combineLatest, EMPTY, from, Observable, of, Subscription } from 'rxjs';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';
import { PriceService } from '@services/price.service';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { AllsitesettingsService } from './allsitesettings.service';
import { DistCartService } from './cart.service';
import { CheckoutService } from './checkout.service';
import { LocalStorageService } from './local-storage.service';
import { Cart, MergeCartSuccessEvent } from '@spartacus/cart/base/root';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Injectable({
  providedIn: 'root',
})
export class LoginService implements OnDestroy {
  isErrorMessage_ = new BehaviorSubject<string | LoginErrorMessage>(null);
  successMessage_: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  isCaptchaDisabled_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  // Use this flag when page skeleton must be shown while login is in progress
  isLoginProgress$: Observable<boolean> = this.loginServiceHelper.isLoginProgress();

  inactiveMessage: Translatable = {
    key: 'account.signout.active.error',
  };

  // this subscription is used for listening to login cart merges and unsubscribing after they are complete
  private userSubscription: Subscription;
  private subscriptions = new Subscription();

  constructor(
    private loginServiceHelper: LoginServiceHelper,
    private siteConfigService: SiteConfigService,
    protected router: Router,
    protected userIdService: UserIdService,
    private userService: UserAccountFacade,
    protected occEndpointsService: OccEndpointsService,
    protected authService: DistrelecAuthService,
    protected allSiteSettings: AllsitesettingsService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private priceService: PriceService,
    private baseSiteService: BaseSiteService,
    private localStorage: LocalStorageService,
    private checkoutService: CheckoutService,
    private globalMessageService: GlobalMessageService,
    private eventService: EventService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  async authorizeLogin(userEmail: string, password: string): Promise<void | TokenResponse> {
    // To prevent multiple loaders at same time on screen
    this.loginServiceHelper.cancelLoading();
    this.loginServiceHelper.startLoading();
    // OCC specific user id handling. Customize when implementing different backend

    return this.authService.loginWithCredentials(userEmail, password).then(() => {
      this.userIdService.setUserId(OCC_USER_ID_CURRENT);
      this.allSiteSettings.resetSiteSettingsRequest();
    });
  }

  /**
   * Authorize login flow when the general login is used
   *
   * @param userId
   * @param password
   * @param pageTemplate
   */
  postLoginRequest(userId: string, password: string, pageTemplate?: string): Observable<any> {
    this.localStorage.removeItem('registration-login');
    if (!pageTemplate) {
      pageTemplate = this.siteConfigService.getCurrentPageTemplate();
    }

    return from(this.authorizeLogin(userId, password)).pipe(
      switchMap(() => {
        if (this.isErrorMessage_.value !== null) {
          this.isErrorMessage_.next(null);
        }
        return this.handleUserValidation(pageTemplate);
      }),
      catchError((data) => {
        if (data.error?.error_description === 'User is disabled') {
          this.router.navigate(['/']);
          this.loginServiceHelper.cancelLoading();
          this.globalMessageService.add(this.inactiveMessage, GlobalMessageType.MSG_TYPE_WARNING);
        } else {
          return this.loginServiceHelper.handleUserError(pageTemplate);
        }
      }),
    );
  }

  /**
   * Authorize login flow for the checkout login only '/login/checkout'
   *
   * @param userId
   * @param password
   */
  postLoginRequestAfterCheckout(userId: string, password: string): Observable<any> {
    this.isErrorMessage_.next(null);
    return fromPromise(this.authorizeLogin(userId, password)).pipe(
      switchMap(() => this.handleUserValidation('CheckoutPageTemplate')),
      catchError((error) => {
        if (error?.error?.error_description === 'Bad credentials') {
          this.isErrorMessage_.next('login.error_true');
        } else {
          this.isErrorMessage_.next('checkout.login.forgotten_pwd_error');
        }
        this.loginServiceHelper.cancelLoading();
        return EMPTY;
      }),
    );
  }

  // Handle user validation if user logged into the wrong webshop, in this case user is redirected to login page
  handleUserValidation(pageTemplate: string): Observable<any> {
    return this.userService.get().pipe(
      filter((userInfo) => !!userInfo),
      first(),
      switchMap((userInfo: User) =>
        combineLatest([this.baseSiteService.getActive(), this.distBaseSiteService.isMyAccountRedirectEnabled()]).pipe(
          first(),
          switchMap(([activeBaseSite, isMyAccountRedirectEnabled]) => {
            if (userInfo.customersBaseSite && activeBaseSite !== userInfo?.customersBaseSite) {
              this.loginServiceHelper.setUpErrorMessage(true, userInfo.customersBaseSite, this.isErrorMessage_);
              return this.logoutUserIfWrongWebshop(pageTemplate);
            } else {
              return this.continueUserLogin(
                userInfo,
                isMyAccountRedirectEnabled ? 'AccountPageTemplate' : pageTemplate,
              );
            }
          }),
        ),
      ),
      catchError((error) => {
        // When user is deactivated, we are showing error msg in banner on page
        const isDeactivatedUser = error.error?.errors[0]?.message === 'httpHandlers.accountDeactivated';
        this.userSubscription.unsubscribe();
        return fromPromise(this.logoutUserIfWrongWebshop(pageTemplate, isDeactivatedUser));
      }),
    );
  }

  logoutUserIfWrongWebshop(pageTemplate: string, isDeactivatedUser?: boolean): Promise<void> {
    return this.authService.coreLogout().then(() => {
      this.loginServiceHelper.handleUserError(pageTemplate, isDeactivatedUser);
    });
  }

  continueUserLogin(userInfo: User, pageTemplate: string): Observable<any> {
    this.loginServiceHelper.setUserSpecificSettings(userInfo);
    // We pass true since user will always be redirected after validation is successful (in case of login from header, login page or checkout)
    return this.handleUserSuccess(userInfo, pageTemplate);
  }

  handleUserSuccess(userInfo: User, pageTemplate: string): Observable<any> {
    this.isErrorMessage_.next(null);
    this.priceService.cleanPricesLocalStorage();

    return this.populateAndRedirect(userInfo, pageTemplate).pipe(
      catchError((error) => {
        this.loginServiceHelper.cancelLoading();
        throw error;
      }),
    );
  }

  // After user logs in on checkout page, backorder service is called,
  // if there are no backorder products, fast checkout is called before user is redirected to checkout delivery page
  handleSuccessForCheckoutLogin(cart?: Cart): Observable<Cart> {
    const voucherCode = this.localStorage.getItem('voucherCode');
    const cartFlow$: Observable<Cart | MergeCartSuccessEvent> = cart
      ? this.cartService.returnCartDataFromDB()
      : this.eventService.get(MergeCartSuccessEvent);

    return cartFlow$.pipe(
      first(),
      switchMap((cartData) => {
        if (cart) {
          return of(cartData);
        }

        return this.cartService.returnCartDataFromDB(cartData.cartId);
      }),
      tap((cartData: Cart) => this.cartStoreService.setCartState(cartData)),
      switchMap(() =>
        this.checkoutService.prepareForCheckout().pipe(
          switchMap((cartData: Cart) => {
            this.cartStoreService.setCartState(cartData);
            this.loginServiceHelper.cancelLoading();
            if (voucherCode) {
              return this.handleValidateVoucher(voucherCode, cartData);
            } else {
              return this.handleFastCheckoutRedirection(cartData);
            }
          }),
          tap(() => this.loginServiceHelper.dispatchLoginEvent()),
        ),
      ),
    );
  }
  handleValidateVoucher(voucherCode: string | null, fastCheckoutCartData: Cart): Observable<Cart> {
    if (voucherCode) {
      return this.cartService.validateVoucher(voucherCode).pipe(
        map((response) => {
          this.loginServiceHelper.displayVoucherAppliedSuccessMessage();
          this.cartStoreService.updateCartKeys(response);
          if (fastCheckoutCartData) {
            return this.handleFastCheckoutRedirection(fastCheckoutCartData);
          }
          return of(fastCheckoutCartData);
        }),
        catchError((err) => {
          this.checkoutService.errorMessage_.next('cart.voucher_invalid');
          if (fastCheckoutCartData) {
            return this.handleFastCheckoutRedirection(fastCheckoutCartData);
          }
          return of(err);
        }),
        tap(() => this.localStorage.removeItem('voucherCode')),
      );
    } else {
      return this.handleFastCheckoutRedirection(fastCheckoutCartData);
    }
  }

  handleFastCheckoutRedirection(cartData: Cart): Observable<Cart> {
    this.loginServiceHelper.displayLoginSuccessMessage();
    let navObservable: Observable<any> = null;
    if (cartData.hasUnallowedBackorder) {
      navObservable = this.navigateToBackorder();
    } else if (
      cartData.punchedOutProducts ||
      cartData.endOfLifeProducts?.length > 0 ||
      cartData.phasedOutProducts?.length > 0
    ) {
      // if cart contains restircted products, redirect back to cart page
      navObservable = this.navigateBackToCartAndCancelLoading();
    } else if (cartData.movLimit > cartData.subTotal.value) {
      return this.navigateBackToCartAndDisplayMOVPopup(cartData).pipe((_) => of(cartData));
    } else if (cartData.eligibleForFastCheckout) {
      navObservable = this.navigateToCheckoutPaymentAndCancelLoading();
    } else {
      return this.navigateToCheckoutDeliveryAndCancelLoading().pipe((_) => of(cartData));
    }
    return navObservable.pipe(map((_) => cartData));
  }

  navigateBackToCartAndDisplayMOVPopup(cartData: Cart): Observable<boolean | void> {
    return fromPromise(
      this.router
        .navigate(['/cart'])
        .then(() =>
          this.loginServiceHelper.displayMOVpopup(
            cartData.movLimit,
            cartData.subTotal.value,
            cartData.subTotal.currencyIso,
          ),
        ),
    );
  }

  navigateBackToCartAndCancelLoading(): Observable<boolean | void> {
    return fromPromise(this.router.navigate(['/cart']).then(() => this.loginServiceHelper.cancelLoading()));
  }

  navigateToCheckoutDeliveryAndCancelLoading(): Observable<boolean | void> {
    return fromPromise(this.router.navigate(['checkout/delivery']).then(() => this.loginServiceHelper.cancelLoading()));
  }

  navigateToCheckoutPaymentAndCancelLoading(): Observable<boolean | void> {
    return fromPromise(
      this.router.navigate(['checkout/review-and-pay']).then(() => this.loginServiceHelper.cancelLoading()),
    );
  }

  navigateToBackorder(): Observable<boolean | void> {
    return fromPromise(
      this.router.navigate(['checkout/backorderDetails']).then(() => {
        this.loginServiceHelper.displayLoginSuccessMessage();
        this.loginServiceHelper.cancelLoading();
      }),
    );
  }

  populateAndRedirect(userInfo: User, pageTemplate: string): Observable<void | Cart> {
    if (pageTemplate === 'CheckoutPageTemplate') {
      return this.handleSuccessForCheckoutLogin();
    }

    if (pageTemplate === 'shoppingList') {
      this.assignRedirectUrlAfterLogin(`/shopping`);
    }

    if (pageTemplate === 'AccountPageTemplate') {
      this.assignRedirectUrlAfterLogin(
        userInfo.derivedChannel === 'B2B' ? '/my-account/company/information' : '/my-account/my-account-information',
      );
    }

    if (pageTemplate === 'rs-welcome') {
      this.assignRedirectUrlAfterLogin(`/rs-welcome`);
    }

    return this.loginServiceHelper.handleRedirectOnLogin(pageTemplate, userInfo);
  }

  assignRedirectUrlAfterLogin(url: string): void {
    this.loginServiceHelper.assignRedirectUrlAfterLogin(url);
  }

  stopLoading(): void {
    this.loginServiceHelper.cancelLoading();
  }
}
