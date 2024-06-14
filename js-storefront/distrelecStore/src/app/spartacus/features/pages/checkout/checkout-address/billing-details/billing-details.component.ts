import { AfterViewInit, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import {
  faCheck,
  faLocationDot,
  faPen,
  faSquareCheck,
  faTimes,
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { catchError, first, map, switchMap, tap } from 'rxjs/operators';
import { LocalStorageService } from '@services/local-storage.service';
import { DeliveryModeEnum } from '@services/checkout.service';
import { CountryCodesEnum } from 'src/app/spartacus/site-context/services/country.service';
import { CartStoreService } from '@state/cartState.service';
import { CustomerType } from '@model/site-settings.model';
import { Address, Principal, WindowRef } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-billing-details',
  templateUrl: './billing-details.component.html',
})
export class BillingDetailsComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() billingForm: UntypedFormGroup;
  @Input() isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() activeSiteId: string;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;
  @Input() isSavedAddressValid: (address: Address, customerType: string) => boolean;
  @Input() checkIfAllFieldsArePrefilled: (
    addressForm: UntypedFormGroup,
    form: BehaviorSubject<{ isSaved: boolean }>,
    isDisplayErrors: boolean,
  ) => boolean;
  @Input() isInputOptional: (key: string) => boolean;
  @Input() updateDefaultFlagOnAddresses: (address: Address, addresses_: BehaviorSubject<Address[]>) => void;
  @Input() isExportShop: boolean;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();
  userType: string;
  billingAddressOnCart: Address;
  deliveryAddressOnCart: Address;
  billingAddressesFromDB: Address[];
  billingAddressForInfoMode: Address;

  billingDetails: UntypedFormGroup;
  deliveryDetails: UntypedFormGroup;
  isEditAddress_: BehaviorSubject<boolean> = new BehaviorSubject<any>(false);
  isDeliveryLoading_: BehaviorSubject<boolean> = this.checkoutService.isDeliveryLoading_;
  isBillingLoading_: BehaviorSubject<boolean> = this.checkoutService.isBillingLoading_;

  deliveryModeEnum = DeliveryModeEnum;

  isShippingDisabled = false;
  faCheck: IconDefinition = faCheck;
  faTimes: IconDefinition = faTimes;
  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;
  faPen: IconDefinition = faPen;
  faLocationDot: IconDefinition = faLocationDot;

  private subscriptions: Subscription = new Subscription();

  // B2B_KEY_ACCOUNT type of users cannot change billing addresses but they can select isDeliverySame
  // B2E type of users cannot select
  constructor(
    public checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
    private localStorageService: LocalStorageService,
    private cdRef: ChangeDetectorRef,
    private winRef: WindowRef,
  ) {}

  ngOnInit(): void {
    // @ts-ignore
    this.billingDetails = this.billingForm.get('billingDetails');
    // @ts-ignore
    this.deliveryDetails = this.billingForm.get('deliveryDetails');

    this.billingAddressOnCart = this.cartData_.getValue().billingAddress;
    this.deliveryAddressOnCart = this.cartData_.getValue().deliveryAddress;
    this.billingAddressesFromDB = this.checkoutService.billingAddresses_.getValue();
    this.userType = this.cartData_.getValue().user.type;

    if (this.cartStoreService.isCartUserGuest()) {
      this.validateAddressForGuest();
    } else {
      this.listenToBillingDetails();
    }

    this.subscriptions.add(this.checkoutService.isDeliverySame_.subscribe(() => this.cdRef.detectChanges()));
  }

  ngAfterViewInit(): void {
    if (this.checkoutService.isAddressEditClicked() === 'billing' && this.billingAddressesFromDB.length > 1) {
      const selectedElementId = 'selectBillingInput_' + this.cartData_.getValue().billingAddress.id;

      setTimeout(() =>
        this.winRef.document.getElementById(selectedElementId).scrollIntoView({
          behavior: 'smooth',
          block: 'center',
        }),
      );
      this.checkoutService.setIsAddressEditClicked(null);
    }
  }

  isDeliverySamePossible(userInfo: Principal, isBillingFormSaved: { isSaved: boolean }): boolean {
    return (
      (!this.isDeliveryPickup(this.billingForm.get('delivery').value.code) ||
        this.isPickupInExport(this.billingForm.get('delivery').value.code)) &&
      !this.cartStoreService.isCartUserGuest() &&
      !this.cartStoreService.isCartUserB2E() &&
      !this.isShippingDisabled &&
      isBillingFormSaved.isSaved &&
      this.billingAddressesFromDB.length === 1
    );
  }

  onSameDeliveryClick(): void {
    this.isBillingLoading_.next(true);
    this.isDeliveryLoading_.next(true);
    this.billingForm.patchValue({
      isDeliverySame: !this.billingForm.get('isDeliverySame')?.value,
    });
    this.checkoutService.isDeliverySame_.next(!this.billingForm.get('isDeliverySame')?.value);

    // if delivery address has been already selected and user clicks delivery address is same as billing
    // then clear the previous id of delivery address to disable continue button
    // if (this.billingForm.get('deliveryDetails').get('id').value) {
    this.billingForm.get('deliveryDetails').patchValue({
      id: '',
    });
    this.setDeliveryAddressId_.next('none');
    // uncheck previously selected address once user triggers checkbox
    // If user checks that billing details are the same as delivery details,
    // Then update details delivery component to hide details form, patch details and display saved state
    // Update the header state to display steps
    if (this.billingForm.get('isDeliverySame').value) {
      this.checkoutService
        .setDeliveryAddress(this.billingDetails.get('id').value)
        .pipe(
          first(),
          tap(() => this.hideSavedDeliveryDetails()),
          catchError(() => {
            this.isBillingLoading_.next(false);
            this.isDeliveryLoading_.next(false);
            return of();
          }),
        )
        .subscribe();
    } else {
      this.deleteSameDelivery();
    }
  }

  deleteSameDelivery(): void {
    this.isBillingLoading_.next(false);
    this.isDetailsFormSaved_.next({ isSaved: true });
    // Update the flag to disable continue button while addresses updating
    this.checkoutService
      .deleteDeliveryAddressFromCart(this.billingDetails.get('id').value)
      .pipe(
        first(),
        switchMap(() => {
          if (!this.checkoutService.deliveryAddresses_.getValue()) {
            return this.checkoutService.getDeliveryDetails();
          }
          return of(null);
        }),
        switchMap(() => {
          const deliveryAddresses: Address[] = this.checkoutService.deliveryAddresses_.getValue();
          const deliveryAddressesWithoutBillingAddress = deliveryAddresses.filter((address) => !address.billingAddress);
          if (deliveryAddressesWithoutBillingAddress.length === 1) {
            return this.setDeliveryAddressOnCart(deliveryAddressesWithoutBillingAddress[0]);
          } else {
            const defaultShippingAddress = deliveryAddressesWithoutBillingAddress.find(
              (address) => address.defaultShipping,
            );
            if (defaultShippingAddress) {
              return this.setDeliveryAddressOnCart(defaultShippingAddress);
            }
          }
          return of(true);
        }),
        map(() => {
          if (this.cartStoreService.isCartUserB2B()) {
            this.deliveryDetails.patchValue({
              companyName: this.billingDetails.get('companyName').value,
            });
          }
          this.populateDeliveryDetails();
          this.isDeliveryLoading_.next(false);
        }),
        catchError(() => of(this.isBillingLoading_.next(false), this.isDeliveryLoading_.next(false))),
      )
      .subscribe(() => this.isDeliveryLoading_.next(false));
  }

  populateDeliveryDetails(): void {
    // details form is set to false when 0 or 1 address is returned so the checks can be trigerred again
    if (this.checkoutService.deliveryAddresses_.getValue().length <= 1) {
      this.deliveryDetails.patchValue({
        id: '',
      });
      this.isDetailsFormSaved_.next({ isSaved: false });
      // in case there are no delivery addresses returned, populate with the country code from saved billing address
      this.deliveryDetails.patchValue({
        countryCode: this.checkoutService.deliveryAddresses_.getValue()[0]?.country?.isocode
          ? this.checkoutService.deliveryAddresses_.getValue()[0].country.isocode
          : this.billingDetails.get('countryCode').value,
      });
      this.deliveryDetails.setErrors(null);
    }
  }

  hideSavedDeliveryDetails(): void {
    this.isBillingLoading_.next(false);
    this.isDeliveryLoading_.next(false);
    this.isDetailsFormSaved_.next({ isSaved: false });
  }

  editDetails(): void {
    this.isEditAddress_.next(true);
    this.isBillingFormSaved_.next({ isSaved: false });
    this.selectBillingAddress(this.cartData_.getValue().billingAddress, false);
  }

  selectBillingAddress(address: Address, isSameDeliveryCheck: boolean): void {
    // We are storing currently edited address, so we can reset values to it on "Cancel"
    this.billingAddressForInfoMode = address;

    this.billingDetails.patchValue({
      // When user saves the open form
      id: address?.id,
      firstName: address?.firstName,
      lastName: address?.lastName,
      line1: address?.line1,
      line2: address?.line2,
      town: address?.town,
      // if no country data is rerurned, set the address according to webshop id
      countryCode: address?.country?.isocode ? address?.country?.isocode : this.activeSiteId,
      countryName: address?.country?.name,
      postalCode: address?.postalCode,
      phone: this.checkoutService.setMobileNumber(address),
    });

    if (address?.region) {
      this.billingDetails.get('region').setValue(address?.region?.name);
    }

    if (this.cartStoreService.isCartUserB2B()) {
      this.billingDetails.patchValue({
        companyName: address?.companyName,
        companyName2: address?.companyName2,
      });
    } else {
      this.billingDetails.patchValue({
        // This check needs to be performed for the only select control
        titleCode: address?.titleCode ? address.titleCode : null,
      });
      if (this.cartStoreService.isCartUserB2E()) {
        this.billingDetails.patchValue({
          email: address?.email,
        });
      }
      if (this.cartStoreService.isCartUserGuest() && this.activeSiteId === CountryCodesEnum.ITALY) {
        this.billingDetails.patchValue({
          codiceFiscale: address?.codiceFiscale,
        });
      }
    }

    if (isSameDeliveryCheck) {
      this.checkIfSameDeliveryDisabled(address);
    }
  }

  checkIfSameDeliveryDisabled(data: Address): void {
    // check if there is one billing address coming from database
    if (!data?.shippingAddress) {
      this.isShippingDisabled = true;

      this.billingForm.patchValue({
        isDeliverySame: false,
      });
      this.checkoutService.isDeliverySame_.next(false);

      // In case where no delivery addresses returned and shippingAddress is false, open the delivery details form
      if (
        !this.checkoutService.deliveryAddresses_.getValue() ||
        this.checkoutService.deliveryAddresses_.getValue()?.length < 1
      ) {
        this.isDetailsFormSaved_.next({ isSaved: false });
      }
    } else if (this.billingAddressesFromDB?.length > 1) {
      // If multiple billing addresses are available, hide the checkbox
      this.isShippingDisabled = true;
    }
  }

  // Validation is not trigerred when user is B2B_KEY_ACCOUNT or BIZ is not first time registered
  isBillingNeedsValidation(): boolean {
    return !(
      this.billingAddressesFromDB.length > 1 ||
      (this.activeSiteId === CountryCodesEnum.EXPORT &&
        !this.localStorageService.getItem('IS_NEW_REGISTRATION') &&
        !this.cartStoreService.isCartUserB2E())
    );
  }

  displayRelevantAddress(distAddresses: Address[]): void {
    this.billingAddressesFromDB = distAddresses;
    // If user has the billing address saved on cart, display it accordingly
    // If user has one address coming from DB, populate data with it
    // Ignore all these values if user is B2E
    if (this.cartStoreService.isCartUserB2E()) {
      this.handleB2EAccount(distAddresses);
      // For b2b key account when page has loaded, if one billing address is returned, then assign it to the cart
    } else if (this.cartStoreService.isCartUserB2BKey()) {
      this.handleB2BKeyAccount(distAddresses);
    } else {
      // If user is not B2E
      // If cart already has saved pre-selected billing
      if (this.billingAddressOnCart) {
        this.ifAddressSavedOnCart();
      } else {
        if (distAddresses.length === 1) {
          this.isSavedBillingIsOne(distAddresses[0]);
        } else if (distAddresses.length > 1) {
          this.isBillingFormSaved_.next({ isSaved: true });
        }
      }
    }
  }

  handleB2EAccount(distAddresses: Address[]): void {
    this.selectBillingAddress(distAddresses[0], false);
    this.checkIfSameDeliveryDisabled(distAddresses[0]);
    this.billingForm.patchValue({
      isDeliverySame: true,
    });
    this.checkoutService.isDeliverySame_.next(true);
    this.isBillingLoading_.next(false);
  }

  handleB2BKeyAccount(distAddresses: Address[]): void {
    this.isBillingFormSaved_.next({ isSaved: true });
    if (distAddresses.length <= 1) {
      // if returned billing address has missing fields, the form will be opened
      if (!this.isSavedAddressValid(distAddresses[0], CustomerType.B2B_KEY_ACCOUNT)) {
        this.selectBillingAddress(distAddresses[0], true);
        this.isBillingFormSaved_.next({ isSaved: false });
      } else {
        this.selectBillingAddress(distAddresses[0], true);
        // if no delivery address is select on cart, pre-select delivery and billing to be same
        if (!this.deliveryAddressOnCart) {
          this.billingForm.patchValue({
            isDeliverySame: true,
          });
          this.checkoutService.isDeliverySame_.next(true);
          this.setDeliveryAddressOnCart(distAddresses[0]).subscribe();
        }
      }
    }
  }

  ifAddressSavedOnCart(): void {
    // set addresses to be the same only when billing is not multiple
    if (this.billingAddressOnCart?.id === this.deliveryAddressOnCart?.id && this.billingAddressesFromDB.length <= 1) {
      this.billingForm.patchValue({
        isDeliverySame: true,
      });
      this.checkoutService.isDeliverySame_.next(true);
      this.setDeliveryAddressId_.next('none');
    }
    if (this.isBillingNeedsValidation() && !this.isSavedAddressValid(this.billingAddressOnCart, this.userType)) {
      this.checkIfAllFieldsArePrefilled(this.billingDetails, this.isBillingFormSaved_, false);
    } else {
      // If billing does not need validation, display addresses that returned from DB
      this.isBillingFormSaved_.next({ isSaved: true });
    }
    this.selectBillingAddress(this.billingAddressOnCart, true);
    this.isBillingLoading_.next(false);
  }

  isSavedBillingIsOne(address: Address): void {
    this.selectBillingAddress(address, true);
    // If the returned address from address controller is correct
    // And user is not B2B_KEY_ACCOUNT, validate if fields are correct
    // If correct, mark form as saved, populate fields and save it to cart
    if (this.isBillingNeedsValidation()) {
      // If address passes validation, close the form and set it on cart
      // Otherwise expand the form with populated fields
      if (this.isSavedAddressValid(address, this.userType)) {
        this.setBillingAddressOnCart(address);
        this.isBillingFormSaved_.next({ isSaved: true });
      } else {
        this.selectBillingAddress(address, true);
      }
    } else {
      // Set billing address for not validating
      this.setBillingAddressOnCart(address);
    }
  }

  setDeliveryAddressOnCart(address: Address): Observable<void> {
    return this.checkoutService.setDeliveryAddress(address.id).pipe(
      tap(() => {
        this.cartStoreService.updateCartState('deliveryAddress', address);
        this.setDeliveryAddressId_.next(address.id);
      }),
    );
  }

  setBillingAddressOnCart(address: Address): void {
    this.isBillingLoading_.next(true);
    this.checkoutService
      .setBillingAddress(address.id)
      .pipe(
        first(),
        tap(() => {
          this.cartStoreService.updateCartState('billingAddress', address);
          this.isBillingLoading_.next(false);
        }),
        catchError(() => of(this.isBillingLoading_.next(false))),
      )
      .subscribe();
  }

  listenToBillingDetails(): void {
    this.checkoutService.billingAddresses_.subscribe((data) => {
      if (data) {
        this.displayRelevantAddress(data);
      }
    });
  }

  validateAddressForGuest(): void {
    if (this.billingAddressOnCart) {
      this.selectBillingAddress(this.billingAddressOnCart, true);
      this.isBillingFormSaved_.next({ isSaved: true });
    } else {
      this.billingDetails.patchValue({
        countryCode: this.activeSiteId === 'EX' ? null : this.activeSiteId,
      });
    }
  }

  isPickUp(): boolean {
    return this.billingForm.get('delivery').value.code === this.deliveryModeEnum.COLLECTION_PICKUP;
  }

  setBillingDefault(id) {
    this.isBillingLoading_.next(true);
    this.checkoutService
      .setDefaultAddress(id)
      .pipe(
        tap((address: Address) => {
          this.updateDefaultFlagOnAddresses(address, this.checkoutService.billingAddresses_);
          this.isBillingLoading_.next(false);
        }),
        catchError(() => of(this.isBillingLoading_.next(false))),
      )
      .subscribe();
  }

  resetSingleBillingAddressValuesInInfoMode(): void {
    const address = this.billingAddressForInfoMode;

    if (address) {
      this.selectBillingAddress(address, false);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private isPickupInExport(deliveryCode: string) {
    return this.isExportShop && this.isDeliveryPickup(deliveryCode);
  }

  private isDeliveryPickup(deliveryCode: string): boolean {
    return deliveryCode === this.deliveryModeEnum.COLLECTION_PICKUP;
  }
}
