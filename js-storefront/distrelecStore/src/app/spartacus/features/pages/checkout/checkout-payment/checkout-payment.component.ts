import { Component, HostListener, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { LocalStorageService } from '@services/local-storage.service';
import { Address, createFrom, EventService, User, WindowRef } from '@spartacus/core';
import { filter } from 'rxjs/operators';
import { DistrelecUserService } from '@services/user.service';
import { faCircleExclamation } from '@fortawesome/free-solid-svg-icons';
import { CountryCodesEnum, CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { CustomerType } from '@model/site-settings.model';
import { CartStoreService } from '@state/cartState.service';
import { DistGuestUserService } from '@services/guest-user.service';
import { AppendComponentService } from '@services/append-component.service';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { CheckoutEvent } from '@features/tracking/events/checkout-event';
import { Location } from '@angular/common';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-checkout-payment',
  templateUrl: './checkout-payment.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class CheckoutPaymentComponent implements OnInit, OnDestroy {
  activeSiteId: string;
  paymentForm: UntypedFormGroup;
  codiceForm: UntypedFormGroup;
  customerType: CustomerType;
  billingAddress: Address;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();

  isPaymentLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isDisplayCardForm_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isExceededBudget_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  vatMessages_: BehaviorSubject<string[]> = new BehaviorSubject<any>(null);

  userDetails_: BehaviorSubject<User> = this.userService.userDetails_;
  isErrorMessage_: BehaviorSubject<string> = this.checkoutService.errorMessage_;
  errorMessage_: BehaviorSubject<string> = this.checkoutService.errorMessage_;
  successMessage_: BehaviorSubject<string> = this.checkoutService.successMessage_;
  faCircleExclamation = faCircleExclamation;

  newCreditCardSelected: boolean;
  isPaymentInfoLoaded_: BehaviorSubject<boolean> = new BehaviorSubject(false);
  showOrderRefLoadingSpinner_: BehaviorSubject<boolean> = new BehaviorSubject(false);

  private userDetailsSubscription: Subscription;
  private subscriptions: Subscription = new Subscription();

  constructor(
    private appendComponentService: AppendComponentService,
    private countryService: CountryService,
    private fb: UntypedFormBuilder,
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
    private router: Router,
    private localStorage: LocalStorageService,
    private winRef: WindowRef,
    private userService: DistrelecUserService,
    private guestUserService: DistGuestUserService,
    private eventService: EventService,
    private location: Location,
  ) {}

  // This listener if user uses evo payment and page components must be updated according to the payment response
  @HostListener('window:storage')
  onStorageChange() {
    if (this.localStorage.getItem('error')) {
      this.handlePaymentError();
    }
    if (this.localStorage.getItem('success')) {
      this.handlePaymentSuccess();
    }
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.countryService.getActive().subscribe((siteId: string) => {
        this.activeSiteId = siteId;
      }),
    );

    this.billingAddress = this.cartData_.getValue().billingAddress;
    if (!this.cartStoreService.isCartUserGuest()) {
      this.getAndAssignUserInfo();
    } else {
      this.customerType = this.cartData_.getValue().user.type;
    }

    this.paymentForm = this.fb.group({
      payment: new UntypedFormControl(this.cartData_.getValue().paymentMode?.code, [Validators.required]),
      paymentCardType: new UntypedFormControl(''),
      // card type from order summary
      selectedCard: new UntypedFormControl(''),
      checkoutReference: new UntypedFormControl(this.cartData_.getValue().projectNumber),
    });

    this.checkoutService.checkoutPageSteps_.next({
      loginRegisterStep: 'passed',
      billingStep: 'passed',
      confirmStep: 'current',
    });

    if (this.localStorage.getItem('error')) {
      this.handlePaymentError();
    }

    if (this.shouldReloadBillingAddress()) {
      this.checkoutService.getBillingDetails().subscribe();
    }

    this.appendComponentService.stopScreenLoading();

    this.dispatchCheckoutEvent();
  }

  ngOnDestroy(): void {
    this.checkoutService.errorMessage_.next(null);
    this.checkoutService.successMessage_.next(null);
    this.subscriptions.unsubscribe();
  }

  getAndAssignUserInfo(): void {
    this.userDetailsSubscription = this.userService.userDetails_.pipe(filter(Boolean)).subscribe((data: User) => {
      this.customerType = data.customerType;
      this.vatMessages_ = this.checkForVATMessage();

      // HDLS-1537 : codice destinario fields
      if (
        this.activeSiteId === CountryCodesEnum.ITALY &&
        (data.customerType === 'B2B' || data.customerType === 'B2B_KEY_ACCOUNT' || data.customerType === 'B2E')
      ) {
        this.createCodiceForm(data.vat4, data.legalEmail);
      }

      // HDLS-1559 : budget approval request flag
      if (data.budget?.active) {
        if (
          data.budget.orderBudget - this.cartData_.getValue().totalPrice.value < 0 ||
          data.budget.yearlyBudget - this.cartData_.getValue().totalPrice.value < 0
        ) {
          this.isExceededBudget_.next(true);
        }
      }
      this.userDetailsSubscription?.unsubscribe();
    });
  }

  createCodiceForm(vat4?: string, legalEmail?: string) {
    this.codiceForm = this.fb.group({
      codiceCUP: new UntypedFormControl(this.cartData_.getValue().codiceCUP, [Validators.minLength(15)]),
      codiceCIG: new UntypedFormControl(this.cartData_.getValue().codiceCIG, [Validators.minLength(10)]),
    });

    if (vat4) {
      this.codiceForm.addControl(
        'codiceDestinario',
        new UntypedFormControl({ value: this.userService.userDetails_.getValue().vat4, disabled: true }, [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(7),
        ]),
      );
    } else if (!vat4 && legalEmail) {
      this.codiceForm.addControl(
        'legalEmail',
        new UntypedFormControl({ value: this.userService.userDetails_.getValue().legalEmail, disabled: true }, [
          Validators.required,
          Validators.pattern(/^(\S+)@(\S+)?(legal|pec|cert|sicurezzapostale)(\S+)?\.(\S+)$/i),
        ]),
      );
    } else {
      this.codiceForm.addControl(
        'codiceDestinario',
        new UntypedFormControl(this.userService.userDetails_.getValue().vat4, [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(7),
        ]),
      );
    }
  }

  handlePaymentError() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scrollTo(0, 0);
    }
    if (this.localStorage.getItem('error') === 'loading') {
      this.isPaymentLoading_.next(true);
    } else if (this.localStorage.getItem('error') === 'PurchaseBlockedProducts') {
      this.router.navigate(['checkout/backorderDetails']);
      this.localStorage.removeItem('error');
    } else {
      this.errorMessage_.next(this.localStorage.getItem('error'));
      this.isErrorMessage_.next(this.localStorage.getItem('error'));
      this.localStorage.removeItem('error');
      this.isPaymentLoading_.next(false);
      this.checkoutService.isSummaryLoading_.next(false);
      this.isDisplayCardForm_.next(false);
    }
  }

  handlePaymentSuccess() {
    if (this.localStorage.getItem('success') === 'loading') {
      this.isPaymentLoading_.next(true);
      this.localStorage.removeItem('success');
    } else {
      this.localStorage.removeItem('success');
      this.localStorage.setItem('dispatchPurchaseEvent', true);
      this.router.navigate(['/checkout/orderConfirmation/' + this.localStorage.getItem('orderCode')]);
      this.isPaymentLoading_.next(false);
      this.localStorage.removeItem('addressId');
    }
  }

  checkForVATMessage(): BehaviorSubject<any> {
    if (this.activeSiteId === CountryCodesEnum.EXPORT) {
      let vatArray = this.vatMessages_.getValue() ? this.vatMessages_.getValue() : [];

      if (!vatArray.includes('checkout.vat_b2b') && !vatArray.includes('checkout.vat_general')) {
        if (
          this.cartData_.getValue().billingAddress?.country &&
          this.cartData_.getValue().billingAddress?.country.isocode !== 'GB'
        ) {
          vatArray = this.addVatKey(
            this.cartData_.getValue().type,
            'checkout.vat_b2b',
            'checkout.vat_general',
            vatArray,
          );
        }
      }

      return new BehaviorSubject<any>(vatArray);
    }
  }

  addVatKey(customerType, key1, key2, vatArray): [] {
    // eslint-disable-next-line @typescript-eslint/no-unused-expressions
    this.cartStoreService.isCartUserB2B() ? vatArray.push(key1) : vatArray.push(key2);
    return vatArray;
  }

  onNewCreditCardSelected(eventData) {
    this.newCreditCardSelected = eventData;
  }

  onPaymentInfoLoaded(isLoaded) {
    this.isPaymentInfoLoaded_.next(isLoaded);
  }

  onOrderRefEdited(eventData) {
    this.showOrderRefLoadingSpinner_.next(eventData);
  }

  private shouldReloadBillingAddress() {
    return (
      !this.guestUserService.isGuestSession() &&
      !this.cartStoreService.isCartUserB2E() &&
      !this.checkoutService.billingAddresses_.getValue()
    );
  }

  private dispatchCheckoutEvent(): void {
    const routeState = this.location.getState();
    // eslint-disable-next-line @typescript-eslint/dot-notation
    if (routeState['dispatchCheckoutEvent']) {
      this.eventService.dispatch(
        createFrom(CheckoutEvent, {
          isFastCheckout: this.checkoutService.isFastCheckout_.value,
          cart: this.cartData_.value,
          checkoutEventType: CheckoutGA4EventType.ADD_SHIPPING_INFO,
        }),
      );
    }
  }
}
