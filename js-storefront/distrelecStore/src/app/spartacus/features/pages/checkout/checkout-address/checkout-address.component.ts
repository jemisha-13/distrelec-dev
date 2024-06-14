import { Component, NgZone, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { LocalStorageService } from '@services/local-storage.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, first, switchMap, tap } from 'rxjs/operators';
import { CheckoutService, DeliveryModeEnum } from 'src/app/spartacus/services/checkout.service';
import { CountryCodesEnum, CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { faCircleExclamation, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CartStoreService } from '@state/cartState.service';
import { Warehouse } from '@model/order.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { PostalValidation } from '@helpers/postal-validations';
import { getDateFormat } from '@helpers/date-helper';
import { PhoneNumberService } from '@services/phonenumber.service';
import { Address, createFrom, EventService, SiteContextConfig } from '@spartacus/core';
import { isActiveSiteInternational } from '../../../../site-context/utils';
import { CheckoutEvent } from '@features/tracking/events/checkout-event';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-checkout-address',
  templateUrl: './checkout-address.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class CheckoutAddressComponent implements OnInit, OnDestroy {
  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();
  contactAddress: Address;

  isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }> = new BehaviorSubject<{ isSaved: boolean }>({
    isSaved: false,
  });
  isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }> = new BehaviorSubject<{ isSaved: boolean }>({
    isSaved: true,
  });
  isUserLoggedIn_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isBillingDetailsLoaded: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isDeliveryDetailsLoaded: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  errorMessage_: BehaviorSubject<string> = this.checkoutService.errorMessage_;
  successMessage_: BehaviorSubject<string> = this.checkoutService.successMessage_;

  setDeliveryAddressId_: BehaviorSubject<string> = new BehaviorSubject<string>('none');
  pickupData_: BehaviorSubject<Warehouse[]> = this.checkoutService.pickupData_;
  billingForm: UntypedFormGroup = this.fb.group({});

  activeSiteId: string;
  isExportShop: boolean;

  showDoubleOptInInfoEmail: string;
  dateFormat: string;
  countryCode: string;
  languageCode: string;

  faCircleExclamation: IconDefinition = faCircleExclamation;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
    private fb: UntypedFormBuilder,
    private countryService: CountryService,
    private localStorageService: LocalStorageService,
    private ngZone: NgZone,
    private allSiteSettingsService: AllsitesettingsService,
    private postalValidation: PostalValidation,
    private phoneNumberService: PhoneNumberService,
    private config: SiteContextConfig,
    private eventService: EventService,
  ) {}

  ngOnInit(): void {
    if (!this.errorMessage_.getValue()?.includes('voucher')) {
      this.errorMessage_.next(null);
    }
    this.handleDoubleOptInMessage();
    this.setForms();
    this.setSiteId();
    this.checkoutService.checkoutPageSteps_.next({ loginRegisterStep: 'passed', billingStep: 'current' });
    this.checkIfUserIsLoggedIn();
    this.dateFormat = getDateFormat(this.countryCode, this.languageCode);

    this.subscriptions.add(
      this.billingForm
        .get(['billingDetails'])
        .valueChanges.pipe(
          debounceTime(300),
          distinctUntilChanged(),
          tap(() => this.checkoutService.phoneValidationUpdated.emit(true)),
        )
        .subscribe(),
    );

    this.subscriptions.add(
      this.allSiteSettingsService.getCurrentChannelData().subscribe((data) => {
        this.countryCode = data.country;
        this.languageCode = data.language;
      }),
    );

    this.isExportShop = isActiveSiteInternational(this.config);

    this.dispatchCheckoutEvent();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.checkoutService.errorMessage_.next(null);
    this.checkoutService.successMessage_.next(null);

    if (this.billingForm.get('isDeliverySame').value) {
      this.cartStoreService.updateCartState('deliveryAddress', this.cartData_.getValue().billingAddress);
    }
  }

  handleDoubleOptInMessage(): void {
    if (this.localStorageService.getItem('showDoubleOptInInfoMessage')) {
      this.showDoubleOptInInfoEmail = this.cartStoreService.getCartUser().uid;

      this.ngZone.run(() => {
        setTimeout(() => {
          this.showDoubleOptInInfoEmail = null;
          this.localStorageService.removeItem('showDoubleOptInInfoMessage');
        }, 10000);
      });
    }
  }

  isPickUpDelivery(): boolean {
    return this.billingForm?.get('delivery')?.value.code === DeliveryModeEnum.COLLECTION_PICKUP;
  }

  addControlsForB2B(form): void {
    form.addControl('companyName', new UntypedFormControl('', [Validators.required]));
    form.addControl('companyName2', new UntypedFormControl(''));
  }

  addControlsForB2C(form): void {
    form.addControl('titleCode', new UntypedFormControl(null, [Validators.required]));
    form.addControl('firstName', new UntypedFormControl('', [Validators.required]));
    form.addControl('lastName', new UntypedFormControl('', [Validators.required]));
  }

  addControlsForPhone(form): void {
    form.addControl(
      'phone',
      new UntypedFormControl('', {
        asyncValidators: [this.phoneNumberService.createPhoneNumberValidator(form.get('countryCode'))],
      }),
    );
  }

  checkIfUserIsLoggedIn(): void {
    if (this.cartData_.getValue().user.uid !== 'anonymous') {
      this.isUserLoggedIn_.next(true);
    } else {
      // If user is guest, then the addresses information is not called
      // And they will always have delivery and billing set the same
      // @ts-ignore
      this.addControlsForB2C(this.billingForm.get('billingDetails'));
      this.isBillingFormSaved_.next({ isSaved: false });
      this.billingForm.patchValue({
        isDeliverySame: true,
      });
      this.checkoutService.isDeliverySame_.next(true);
    }

    this.listenToUserType();
  }

  listenToUserType(): void {
    if (this.cartStoreService.isCartUserGuest()) {
      this.setFormForGuest();
    } else {
      // Billing details must be rendered first before user gets delivery details
      // To determine if delivery details will be the same
      if (this.cartStoreService.isCartUserB2B()) {
        this.addControlsForB2B(this.billingForm?.get('billingDetails'));
        this.addControlsForB2B(this.billingForm?.get('deliveryDetails'));
      } else if (this.cartStoreService.isCartUserB2E()) {
        // since B2E will always have billing and delivery the same, no need to populate for delivery
        this.addControlsForB2C(this.billingForm?.get('billingDetails'));
      } else {
        this.addControlsForB2C(this.billingForm?.get('billingDetails'));
        this.addControlsForB2C(this.billingForm?.get('deliveryDetails'));
      }
      if (!this.cartStoreService.isCartUserB2E()) {
        this.handleBillindAndDeliveryAddresses();
      } else {
        this.populateAddressForB2E();
      }
    }
  }

  handleBillindAndDeliveryAddresses(): void {
    if (!this.checkoutService.billingAddresses_.getValue()) {
      this.checkoutService
        .getBillingDetails()
        .pipe(
          switchMap(() => {
            this.isBillingDetailsLoaded.next(true);
            return this.checkoutService.getDeliveryDetails().pipe(
              tap(() => {
                this.isDeliveryDetailsLoaded.next(true);
              }),
            );
          }),
        )
        .subscribe();
    } else {
      this.isBillingDetailsLoaded.next(true);
      if (!this.checkoutService.deliveryAddresses_.getValue()) {
        this.checkoutService
          .getDeliveryDetails()
          .pipe(
            tap(() => {
              this.isDeliveryDetailsLoaded.next(true);
            }),
          )
          .subscribe();
      } else {
        this.isDeliveryDetailsLoaded.next(true);
      }
    }
  }

  // Call this when user selects their address to ensure it has fields populated
  // This to ensure that older created accounts pass validation on backend
  // Fields to be validated: country, line1, town, postalCode, phone number is present and email isn't blocked
  isSavedAddressValid(address: Address, customerType: string): boolean {
    return !(
      !address?.line1 ||
      !address?.country ||
      !address?.town ||
      !address?.postalCode ||
      (!address?.phone && !address.cellphone) ||
      (!address?.email && customerType === 'B2E')
    );
  }

  // This is called when user has already opened form to update or save address
  // If all fields are prefilled correctly then the information block is shown instead of component
  checkIfAllFieldsArePrefilled(
    addressForm: UntypedFormGroup,
    pageForm: BehaviorSubject<{ isSaved: boolean }>,
    isDisplayErrors: boolean,
  ): boolean {
    let areFieldsCorrect = true;
    pageForm.next({ isSaved: true });
    // If user selects between the list of addresses, omit the checked
    Object.keys(addressForm.controls).forEach((key) => {
      if (addressForm.get(key).value === '' || (!addressForm.get(key).value && !this.isInputOptional(key))) {
        areFieldsCorrect = false;

        if (isDisplayErrors) {
          this.checkoutService.markFieldAsInvalid(key, addressForm);
        }
      } else if (key === 'postalCode') {
        if (!this.checkoutService.isPostalCodeValid(addressForm)) {
          areFieldsCorrect = false;

          if (isDisplayErrors) {
            this.checkoutService.markFieldAsInvalid(key, addressForm);
          }
        }
      } else if (key === 'phone') {
        if (addressForm.get(key).invalid) {
          areFieldsCorrect = false;

          if (isDisplayErrors) {
            this.checkoutService.markFieldAsInvalid(key, addressForm);
          }
        }
      }
    });

    if (!areFieldsCorrect) {
      pageForm.next({ isSaved: false });
    }

    return areFieldsCorrect;
  }

  public isInputOptional(key: string): boolean {
    return key === 'companyName2' || key === 'codiceCUP' || key === 'codiceCIG' || key === 'line2';
  }

  setFormForGuest(): void {
    // Billing and delivery will always be set as the same
    this.addControlsForB2C(this.billingForm.get('billingDetails'));
    if (this.activeSiteId === CountryCodesEnum.ITALY) {
      (this.billingForm?.get('billingDetails') as UntypedFormGroup).addControl(
        'codiceFiscale',
        new UntypedFormControl('', [Validators.required, Validators.pattern('[0-9A-Za-z]{16}')]),
      );
    }

    this.billingForm.patchValue({
      isDeliverySame: true,
    });
    this.checkoutService.isDeliverySame_.next(true);

    this.isBillingDetailsLoaded.next(true);
    this.isDeliveryDetailsLoaded.next(true);
  }

  updateDefaultFlagOnAddresses(defaultAddress: Address, addresses_: BehaviorSubject<Address[]>): void {
    const addresses = addresses_.value.map((address: Address) => {
      if (address.id === defaultAddress.id) {
        return defaultAddress;
      }
      if (address.defaultBilling) {
        address.defaultBilling = false;
      }
      if (address.defaultShipping) {
        address.defaultShipping = false;
      }
      return address;
    });
    addresses_.next(addresses);
  }

  // For B2E users, billing and delivery always the same and we need to check if addressId is saved in session
  // Otherwise display an empty form and save addressId to local storage on save
  populateAddressForB2E(): void {
    (<UntypedFormGroup>this.billingForm.get('billingDetails')).addControl(
      'email',
      new UntypedFormControl('', [Validators.required, Validators.email]),
    );
    this.billingForm.patchValue({
      isDeliverySame: true,
    });
    this.checkoutService.isDeliverySame_.next(true);
    if (this.localStorageService.getItem('addressId')) {
      this.checkoutService
        .getBillingDetail(this.localStorageService.getItem('addressId'))
        .pipe(
          tap(() => {
            this.isBillingFormSaved_.next({ isSaved: true });
            this.isBillingDetailsLoaded.next(true);
            this.isDeliveryDetailsLoaded.next(true);
          }),
        )
        .subscribe();
    } else {
      this.isBillingFormSaved_.next({ isSaved: false });
      this.checkoutService.billingAddresses_.next([]);
      this.isBillingDetailsLoaded.next(true);
      this.isDeliveryDetailsLoaded.next(true);
    }
  }

  setForms(): void {
    this.billingForm = this.fb.group({
      delivery: new UntypedFormControl({ code: '', name: '' }, [Validators.required]),
      combineOutOfStock: new UntypedFormControl(this.cartData_.getValue().completeDelivery),
      laterDelivery: new UntypedFormControl(false),
      // Delivery & Billing are the same by default
      isDeliverySame: new UntypedFormControl(false),
      checkoutReference: new UntypedFormControl('', { updateOn: 'change' }),
      selectedDate: new UntypedFormControl(''),

      payment: new UntypedFormControl('', [Validators.required]),
      // card type from payment options if saved
      paymentCardType: new UntypedFormControl(''),
      // card type from order summary
      selectedCard: new UntypedFormControl(),
      cardNumber: new UntypedFormControl(''),
      nameOnCard: new UntypedFormControl(''),
      expiryMonth: new UntypedFormControl(''),
      expiryYear: new UntypedFormControl(''),
      CVV: new UntypedFormControl(''),

      billingDetails: this.fb.group({
        formName: 'billingDetails',
        id: new UntypedFormControl(''),
        line1: new UntypedFormControl('', [Validators.required]),
        line2: new UntypedFormControl(''),
        town: new UntypedFormControl('', [Validators.required]),
        postalCode: new UntypedFormControl('', [Validators.required, this.postalCodeValidator('billingDetails')]),
        countryCode: new UntypedFormControl('', [Validators.required]),
        countryName: new UntypedFormControl(''),
        region: new UntypedFormControl(''),
      }),

      deliveryDetails: this.fb.group({
        formName: 'deliveryDetails',
        id: new UntypedFormControl(''),
        line1: new UntypedFormControl('', [Validators.required]),
        line2: new UntypedFormControl(''),
        town: new UntypedFormControl('', [Validators.required]),
        postalCode: new UntypedFormControl('', [Validators.required, this.postalCodeValidator('deliveryDetails')]),
        countryCode: new UntypedFormControl('', [Validators.required]),
        countryName: new UntypedFormControl(''),
        region: new UntypedFormControl(''),
      }),
    });

    this.addControlsForPhone(this.billingForm?.get('billingDetails'));
    this.addControlsForPhone(this.billingForm?.get('deliveryDetails'));
  }

  setSiteId() {
    this.countryService
      .getActive()
      .pipe(first())
      .subscribe((siteId) => {
        this.activeSiteId = siteId;
      });
  }

  postalCodeValidator(formName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      const formCountryCode = this.billingForm.get(formName)?.get('countryCode')?.value;

      if (!value) {
        return null;
      } else {
        return this.postalValidation.validatePostcode(value, formCountryCode) ? null : { postalCode: true };
      }
    };
  }

  private dispatchCheckoutEvent(): void {
    this.eventService.dispatch(
      createFrom(CheckoutEvent, {
        isFastCheckout: this.checkoutService.isFastCheckout_.value,
        cart: this.cartData_.value,
        checkoutEventType: CheckoutGA4EventType.BEGIN_CHECKOUT,
      }),
    );
  }
}
