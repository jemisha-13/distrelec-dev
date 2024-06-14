import { Injectable } from '@angular/core';
import { CustomerType } from '@model/site-settings.model';
import {
  AuthService,
  AuthStorageService,
  BaseSiteService,
  GlobalMessageService,
  GlobalMessageType,
} from '@spartacus/core';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { CartStoreService } from '@state/cartState.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { first } from 'rxjs/operators';
import { CountryService } from '@context-services/country.service';
import { isInternationalShop } from '../site-context/utils';
import { AllsitesettingsService } from './allsitesettings.service';
import { DistCartService } from './cart.service';
import { CheckoutService } from './checkout.service';
import { LocalStorageService } from './local-storage.service';
import { DistrelecUserService } from './user.service';
import { PriceService } from '@services/price.service';

@Injectable({
  providedIn: 'root',
})
export class DistLogoutService {
  constructor(
    private authStorageService: AuthStorageService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
    private distrelecUserService: DistrelecUserService,
    protected allSiteSettings: AllsitesettingsService,
    private authService: AuthService,
    private loginServiceHelper: LoginServiceHelper,
    private localStorage: LocalStorageService,
    private baseSiteService: BaseSiteService,
    private countryService: CountryService,
    private globalMessageService: GlobalMessageService,
    private cookieService: DistCookieService,
    private priceService: PriceService,
  ) {}

  postLogoutRequest(redirectUrlOnLogout?: string): Promise<boolean> {
    this.loginServiceHelper.startLoading();
    const isB2EUser = this.distrelecUserService.userDetails_.getValue()?.customerType === CustomerType.B2E;

    // HDLS-1711: Once B2E triggers logout, we are removing cart
    if (isB2EUser) {
      this.cartService
        .deleteCart()
        .pipe(first())
        .subscribe(() => this.loginServiceHelper.cancelLoading())
        .unsubscribe();
    }
    this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);

    return this.authService
      .coreLogout()
      .then(() => {
        // Update cart product entries
        if (!isB2EUser) {
          this.cartService.revokeCartEntries();
        }
        this.distrelecUserService.userDetails_.next(null);
        this.checkoutService.clearCheckoutData();
        this.priceService.cleanPricesLocalStorage();
        this.localStorage.removeItem('addressId');
        this.localStorage.removeItem('IS_NEW_REGISTRATION');
        this.localStorage.removeItem('voucherCode');
        this.allSiteSettings.resetSiteSettingsRequest();
        this.resetCountryIfInternationalShop();
        this.cookieService.delete('subscribePopupDelay');
        return this.loginServiceHelper.handleRedirectOnLogout(redirectUrlOnLogout);
      })
      .finally(() => {
        this.loginServiceHelper.cancelLoading();
      });
  }

  logoutGuestUser() {
    this.authStorageService.removeItem('guest');
    this.authStorageService.removeItem('guestExpiry');
    this.cartService.removeCartToken();
    this.cartStoreService.resetCart();
  }

  resetCountryIfInternationalShop() {
    this.baseSiteService
      .getActive()
      .pipe(first())
      .subscribe((site) => {
        if (isInternationalShop(site)) {
          const previousCountry = this.localStorage.getItem('previous_country');
          this.localStorage.removeItem('previous_country');
          this.countryService.setActive(previousCountry ?? 'EX');
        }
      });
  }
}
