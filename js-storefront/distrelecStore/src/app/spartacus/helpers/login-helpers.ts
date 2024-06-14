import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginErrorMessage } from '@model/auth.model';
import { AccountActiveService } from '@services/account-active.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { AppendComponentService } from '@services/append-component.service';
import { DistrelecUserService } from '@services/user.service';
import {
  AuthRedirectStorageService,
  AuthStorageService,
  CmsService,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  LoginEvent,
  RoutingService,
  SiteContextUrlSerializer,
  User,
  WindowRef,
} from '@spartacus/core';
import { BehaviorSubject, EMPTY, Observable } from 'rxjs';
import { first, switchMap, tap } from 'rxjs/operators';
import { CountryService } from '@context-services/country.service';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';
import { LocalStorageService } from '@services/local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class LoginServiceHelper {
  private isLoginInProgress_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private accountActiveService: AccountActiveService,
    private allSiteSettings: AllsitesettingsService,
    private appendComponentService: AppendComponentService,
    private authStorageService: AuthStorageService,
    private authRedirectStorageService: AuthRedirectStorageService,
    private winRef: WindowRef,
    private routingService: RoutingService,
    private distrelecUserService: DistrelecUserService,
    private globalMessageService: GlobalMessageService,
    private countryService: CountryService,
    private route: ActivatedRoute,
    private siteContextUrlSerializer: SiteContextUrlSerializer,
    private router: Router,
    private cmsService: CmsService,
    private localStorageService: LocalStorageService,
    private eventService: EventService,
  ) {}

  handleRedirectOnLogout(redirectUrl?: string): Promise<boolean> {
    //need some refinement later on
    this.clearRedirectUrl();
    return this.routingService.goByUrl(redirectUrl ?? '/');
  }

  displayMOVpopup(movLimit: number, currentTotalValue: number, currency: string): void {
    this.appendComponentService.appendMOVpopup(movLimit, currentTotalValue, currency);
    this.appendComponentService.appendBackdropModal({ lightTheme: true });
  }

  startLoading(): void {
    this.isLoginInProgress_.next(true);
    this.appendComponentService.startScreenLoading();
  }

  cancelLoading(): void {
    this.isLoginInProgress_.next(false);
    this.appendComponentService.stopScreenLoading();
  }

  handleUserError(pageTemplate: string, isDeactivatedUser?: boolean): Observable<any> {
    this.distrelecUserService.userDetails_.next(null);
    this.cancelLoading();

    // If user is already on login error page or if error is for deactivated user (shows error msg in banner), do nothing
    if (
      this.winRef.location.href.indexOf('error=true') !== -1 ||
      pageTemplate === 'CheckoutPageTemplate' ||
      isDeactivatedUser
    ) {
      return EMPTY;
    }

    // If user is not on login error page, redirect them to it
    return fromPromise(
      this.routingService.go(['/login'], { queryParams: { error: 'true' } }).then(() => {
        this.cmsService
          .getCurrentPage()
          .pipe(first())
          .subscribe((data) => {
            if (!data) {
              this.cmsService.refreshLatestPage();
            }
          });
      }),
    );
  }

  // This method is being executed for all places where login is used apart from checkout login which has it's own logic of redirection
  handleRedirectOnLogin(pageTemplate: string, userInfo?: User): Observable<any> {
    return this.authRedirectStorageService.getRedirectUrl().pipe(
      first(),
      switchMap((urlWithContext = '') => {
        const { url } = this.siteContextUrlSerializer.urlExtractContextParameters(urlWithContext);
        this.clearRedirectUrl();

        this.dispatchLoginEvent();

        if (pageTemplate === 'CompareListPageTemplate') {
          return this.refreshAndDisplayLoginSuccessMessage(url);
        }

        if (pageTemplate === 'ProductDetailsPageTemplate') {
          this.forceGuardsToRunForRoute('product');
          return this.refreshAndDisplayLoginSuccessMessage(url);
        }

        if (this.isLoggedInOnPageWithNoRedirect(pageTemplate)) {
          return this.routingService.goByUrl(url && url !== 'login/pw/request' ? url : '/').then(() => {
            // this might be true for when user registers and loading screen must be removed
            // set the cookie to display the marketing popup after user is redirected
            this.cancelLoading();
            this.displayLoginSuccessMessage();
          });
        }

        // in cases when user stays on the same page after logging in, check if communication pref popup needs to be displayed
        if (userInfo?.consentConditionRequired) {
          this.appendComponentService.appendCommunicationsPopUp();
        }

        return Promise.resolve().then(() => {
          this.cancelLoading();
          this.displayLoginSuccessMessage();
        });
      }),
    );
  }

  isLoggedInOnPageWithNoRedirect(pageTemplate: string): boolean {
    return (
      pageTemplate !== 'CartPageTemplate' &&
      pageTemplate !== 'ProductDetailsPageTemplate' &&
      pageTemplate !== 'CategoryPageTemplate' &&
      pageTemplate !== 'HomePage2018Template'
    );
  }

  redirectGuestAfterRegistering() {
    this.routingService.go(['/welcome']).then(() => {
      this.displayLoginSuccessMessage();
      this.cancelLoading();
    });
  }

  displayLoginSuccessMessage(): void {
    this.globalMessageService.add('account.confirmation.signin.title', GlobalMessageType.MSG_TYPE_CONFIRMATION, 4000);
  }

  displayVoucherAppliedSuccessMessage(): void {
    this.globalMessageService.add({ key: 'cart.voucher_success' }, GlobalMessageType.MSG_TYPE_CONFIRMATION, 4000);
  }

  setUpErrorMessage(
    invalidShop: boolean,
    customersBaseSite: string,
    isErrorMessage_: BehaviorSubject<string | LoginErrorMessage>,
  ): void {
    if (invalidShop) {
      const isocode = customersBaseSite.split('_')[1];
      this.initialiseInvalidShopCountry(isocode, isErrorMessage_);
    }
  }

  initialiseInvalidShopCountry(isocode: string, isErrorMessage_: BehaviorSubject<string | LoginErrorMessage>): void {
    const userCountryData = this.allSiteSettings.getWebshopSettings(isocode);
    isErrorMessage_.next({ country: 'country_distrelec_' + isocode, href: userCountryData?.domain });
  }

  checkIfCaptchaDisabled(isCaptchaDisabled_): void {
    if (this.authStorageService.getItem('skipCaptcha')) {
      isCaptchaDisabled_.next(true);
    }
    if (this.winRef.location.href.indexOf('key=skipCaptcha') !== -1) {
      this.authStorageService.setItem('skipCaptcha', true);
      isCaptchaDisabled_.next(true);
    }
  }

  setUserSpecificSettings(userInfo: User): void {
    this.setActiveCountry(userInfo);
    this.distrelecUserService.userDetails_.next(userInfo);
    this.accountActiveService.updateAccountState(userInfo?.orgUnit.active);
  }

  setActiveCountry(userInfo: User) {
    if (userInfo.contactAddress?.country) {
      this.countryService
        .getActive()
        .pipe(
          first(),
          tap((activeCountry) => {
            this.localStorageService.setItem('previous_country', activeCountry);
          }),
        )
        .subscribe();
      this.countryService.setActive(userInfo.contactAddress?.country.isocode);
    }
  }

  assignRedirectUrlAfterLogin(url: string): void {
    this.authRedirectStorageService.setRedirectUrl(url);
  }

  clearRedirectUrl(): void {
    this.authRedirectStorageService.setRedirectUrl(undefined);
  }

  isLoginProgress(): Observable<boolean> {
    return this.isLoginInProgress_.asObservable();
  }

  dispatchLoginEvent(): void {
    this.eventService.dispatch(new LoginEvent());
  }

  private forceGuardsToRunForRoute(cxRoute: string): void {
    this.router.resetConfig(
      this.router.config.map((route) => {
        if (route.data?.cxRoute === cxRoute) {
          return {
            ...route,
            runGuardsAndResolvers: 'always',
          };
        }
        return route;
      }),
    );
  }

  private async refreshAndDisplayLoginSuccessMessage(url: string): Promise<void> {
    await this.router.navigateByUrl(url, {
      skipLocationChange: true,
      onSameUrlNavigation: 'reload',
    });

    this.cancelLoading();
    this.displayLoginSuccessMessage();
  }
}
