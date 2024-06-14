import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  BaseSiteService,
  ConverterService,
  EventService,
  InterceptorUtil,
  LoginEvent,
  OccEndpointsService,
  TranslationService,
  USE_CLIENT_TOKEN,
  User,
  WindowRef,
} from '@spartacus/core';
import { Cart, MergeCartSuccessEvent } from '@spartacus/cart/base/root';
import { catchError, first, map, switchMap, take, tap } from 'rxjs/operators';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import {
  RegistrationCountryResponse,
  RegistrationForm,
  VatValidationData,
  VatValidationResult,
} from '@model/registration.model';
import { LoginService } from './login.service';
import { CheckoutService } from './checkout.service';
import { UntypedFormGroup } from '@angular/forms';
import { DistrelecUserService } from './user.service';
import { LocalStorageService } from './local-storage.service';
import { BIZCountryCodesEnum, CountryCodesEnum } from '../site-context/services/country.service';
import { AppendComponentService } from '@services/append-component.service';
import { MarketingConsentNotificationPopupComponent } from '@features/shared-modules/marketing-consent-notification-popup/marketing-consent-notification-popup.component';
import { SiteConfigService } from '@services/siteConfig.service';
import { CartStoreService } from '@state/cartState.service';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { Router } from '@angular/router';
import { DistCartService } from './cart.service';
import { CountriesWithOrgNumberEnum, RegisterServiceHelper, VatIdPrefixEnum } from '@helpers/register-helpers';
import { ChannelService } from '../site-context/services/channel.service';
import { SiteIdEnum } from '@model/site-settings.model';
import { CountryOfOrigin } from '@model/product.model';

@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  errorMessage_: BehaviorSubject<string> = new BehaviorSubject<string>('');
  isRegistrationComplete_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  customerNumberFound: boolean;
  customerAccountExists = false;
  isCustomerExist: boolean;
  countryWithoutInvoiceEmail = ['IT', 'SM', 'VA'];
  isDisplayInvoiceContainer_ = new BehaviorSubject<boolean>(false);

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private loginService: LoginService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private channelService: ChannelService,
    protected converter: ConverterService,
    private checkoutService: CheckoutService,
    private userService: DistrelecUserService,
    private i18n: TranslationService,
    private baseSiteService: BaseSiteService,
    private localStorageService: LocalStorageService,
    private localStorage: LocalStorageService,
    private appendComponentService: AppendComponentService,
    private siteConfigService: SiteConfigService,
    private loginServiceHelper: LoginServiceHelper,
    private router: Router,
    private registerServiceHelper: RegisterServiceHelper,
    private winRef: WindowRef,
    private eventService: EventService,
  ) {}

  buildRegistrationEndpoint(): string {
    return this.occEndpoints.buildUrl('users');
  }

  callRegistrationEndpoint(
    form: RegistrationForm,
    activeSiteId: string,
    isCheckout?: boolean,
  ): Observable<void | User> {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    headers = InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
    return this.http
      .post<any>(this.buildRegistrationEndpoint(), this.registerServiceHelper.createRequestBody(form), {
        headers,
      })
      .pipe(switchMap((data) => this.submitRegistrationSuccess(isCheckout, form, activeSiteId, data)));
  }

  submitRegistrationSuccess(
    isCheckout: boolean,
    form: RegistrationForm,
    activeSiteId: string,
    userData,
  ): Observable<void | User> {
    // When user is successfully registered, we call the login service
    // Which logs in the user and redirects them to '/welcome' page
    this.localStorage.setItem('registration-login', true);
    this.appendComponentService.removeLoadingLogoFromBody();
    return this.postLoginRequestAfterReg(form.uid, form.password, activeSiteId, form.marketingConsent).pipe(
      take(1),
      switchMap(() => this.setFlags(form)),
      tap(() => {
        this.loginServiceHelper.setActiveCountry(userData);
        this.userService.userDetails_.next(userData);
        this.channelService.setActive(form.type);
        if (isCheckout) {
          this.checkoutService.checkoutPageSteps_.next({ loginRegisterStep: 'passed', billingStep: 'current' });
        } else {
          this.registerServiceHelper.scrollViewToTop();
        }
      }),
    );
  }

  /**
   * Authorize login flow straight after user has been registeredr
   *
   * @param userId
   * @param password
   * @param activeSiteId
   * @param isMarketingConsent
   */
  postLoginRequestAfterReg(
    userId: string,
    password: string,
    activeSiteId: string,
    isMarketingConsent?: boolean,
  ): Observable<Cart | void> {
    const pageTemplate = this.siteConfigService.getCurrentPageTemplate();
    this.cartStoreService.updateCartState('user', { uid: userId });

    return from(this.loginService.authorizeLogin(userId, password)).pipe(
      switchMap(() => {
        // HDLS-1546: set flag to display double opt in popup on DE webshop
        if (
          (pageTemplate === 'CheckoutPageTemplate' || pageTemplate === 'RegisterPageTemplate') &&
          activeSiteId === 'DE' &&
          isMarketingConsent
        ) {
          this.localStorage.setItem('showDoubleOptInInfoMessage', true);
        }

        // If user already had something in their cart before registering, create a new cart and merge them two
        return this.completeUserLoginAfterRegistration(pageTemplate);
      }),
      catchError((err) => {
        this.loginServiceHelper.handleUserError(pageTemplate, false);
        this.loginService.isErrorMessage_.next('login.error_true');
        return of(err);
      }),
    );
  }

  completeUserLoginAfterRegistration(pageTemplate: string): Observable<Cart | void> {
    if (pageTemplate === 'CheckoutPageTemplate') {
      return this.eventService.get(MergeCartSuccessEvent).pipe(
        first(),
        switchMap((cartEvent) => this.cartService.returnCartDataFromDB(cartEvent.cartId)),
        tap((cart: Cart) => this.cartStoreService.setCartState(cart)),
        switchMap((cart) => this.loginService.handleSuccessForCheckoutLogin(cart)),
      );
    } else {
      this.eventService.dispatch(new LoginEvent());
      this.loginServiceHelper.displayLoginSuccessMessage();
      this.router.navigate(['/welcome']).then(() => {
        this.loginServiceHelper.cancelLoading();
      });
    }
    return of(null);
  }

  setFlags(form): Observable<any> {
    const pageTemplate = this.siteConfigService.getCurrentPageTemplate();
    return this.baseSiteService.getActive().pipe(
      first(),
      tap((siteId) => {
        if (siteId === SiteIdEnum.EX) {
          this.localStorageService.setItem('IS_NEW_REGISTRATION', true);
        } else if (siteId === SiteIdEnum.DE && form.marketingConsent && pageTemplate !== 'CheckoutPageTemplate') {
          this.localStorage.setItem('consentEmail', form.uid);
          const marketingNotificationComponent = this.appendComponentService.appendComponent(
            MarketingConsentNotificationPopupComponent,
          );
          marketingNotificationComponent.instance.confirmEmail = this.localStorage.getItem('consentEmail');
          marketingNotificationComponent.instance.modalClosed.subscribe({
            complete: (_) => {
              this.appendComponentService.destroyComponent(marketingNotificationComponent);
            },
          });
        }
      }),
    );
  }

  submitRegistrationError(errorMessage?: string): void {
    if (errorMessage === 'register.organizationalNumber.new.customerID') {
      this.i18n
        .translate('registration.b2b.cust_no_not_found')
        .pipe(first())
        .subscribe((data) => this.errorMessage_.next(data));
    } else if (errorMessage === 'registration.error.existing.contact.hybris') {
      this.i18n
        .translate('registration.cust_exist')
        .pipe(first())
        .subscribe((data) => this.errorMessage_.next(data));
    } else if (errorMessage === 'registration.error.account.exists.title') {
      this.i18n
        .translate('registration.error.account.exists.title')
        .pipe(first())
        .subscribe((data) => this.errorMessage_.next(data));
    } else if (errorMessage === 'registration.vatId.validationMessage') {
      this.i18n
        .translate('registration.vatId.validationMessage')
        .pipe(first())
        .subscribe((data) => this.errorMessage_.next(data));
    } else {
      this.errorMessage_.next(errorMessage);
    }

    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
  }

  validateEmail(email: string): Observable<boolean> {
    const url = this.occEndpoints.buildUrl('validateUid', {
      queryParams: {
        uid: email,
      },
    });

    if (email !== '') {
      return this.http.post<any>(url, {}).pipe(
        map(() => true),
        catchError(() => of(false)),
      );
    }
  }

  validateCustomerNumber(customerId: string, form: UntypedFormGroup): Observable<boolean> {
    const url = this.occEndpoints.buildUrl('/isCustomerPresent', {
      queryParams: {
        customerId,
        customerName: form.get('companyname').value,
      },
    });

    // Response contains a boolean if customer number is valid or not
    return this.http.post<boolean>(url, {});
  }

  validateVAT(countryCode: string, vatNumber: string): Observable<boolean> {
    if (!vatNumber) {
      if (this.registerServiceHelper.getVatIdValidation()?.optional) {
        return of(true);
      } else {
        return of(false);
      }
    }

    if (countryCode === 'CH' || countryCode === 'GB') {
      const regex = /^[0-9]{9}$/;
      return of(regex.test(vatNumber));
    }

    const url = this.occEndpoints.buildUrl('validateVat', {
      queryParams: {
        countryCode: this.getVatIdPrefix(countryCode),
        vatNumber,
      },
    });

    return this.http.post<VatValidationResult>(url, {}).pipe(map((response) => response.success));
  }

  validateFunctions(): Observable<any> {
    const url = this.occEndpoints.buildUrl('/functions', {});

    return this.http.get<any>(url, {}).pipe(
      map((listOfFunctions) => listOfFunctions),
      catchError(() => null),
    );
  }

  getCountryCodes(form): Observable<CountryOfOrigin> {
    const url = this.occEndpoints.buildUrl('/countries/registration', {
      queryParams: {
        registrationType: form.get('type').value,
      },
    });

    return this.http.get<RegistrationCountryResponse>(url).pipe(
      map((listOfCountries) => listOfCountries.countries),
      catchError(() => null),
    );
  }

  returnCountryCodeEU(countryCode): boolean {
    return countryCode === CountryCodesEnum.EXPORT || Object.values(BIZCountryCodesEnum).includes(countryCode);
  }

  getVatIdPrefix(country): string {
    return VatIdPrefixEnum[country];
  }

  isCountryWithOrgNumber(countryCode: CountriesWithOrgNumberEnum): boolean {
    return Object.values(CountriesWithOrgNumberEnum).includes(countryCode);
  }

  createVatIDValidators(countryCode: string): Observable<VatValidationData> {
    return this.registerServiceHelper.createVatIDValidators(countryCode);
  }

  invoiceContainerDisplay(form: UntypedFormGroup, country: string): void {
    if (this.countryWithoutInvoiceEmail.includes(country)) {
      this.isDisplayInvoiceContainer_.next(false);
    } else {
      if (form.get('type').value === 'B2B') {
        this.isDisplayInvoiceContainer_.next(!this.isCustomerExist);
      } else {
        this.isDisplayInvoiceContainer_.next(false);
      }
    }
  }

  get isCustomerNumberFound(): boolean {
    return this.customerNumberFound;
  }

  get accountExists(): boolean {
    return this.customerAccountExists;
  }

  set accountExists(value: boolean) {
    this.customerAccountExists = value;
  }
}
