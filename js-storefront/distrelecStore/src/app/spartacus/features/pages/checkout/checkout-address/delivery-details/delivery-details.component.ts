import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faCheck, faSquareCheck, faTimes, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CartStoreService } from '@state/cartState.service';
import { BIZCountryCodesEnum } from 'src/app/spartacus/site-context/services/country.service';
import { BehaviorSubject } from 'rxjs';
import { filter, tap } from 'rxjs/operators';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { Address, Principal } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-delivery-details',
  templateUrl: './delivery-details.component.html',
  styleUrls: ['./delivery-details.component.scss'],
})
export class DeliveryDetailsComponent implements OnInit {
  @Input() billingForm: UntypedFormGroup;
  @Input() isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() activeSiteId: string;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;
  @Input() isSavedAddressValid: (address: Address, customerb2bType: string) => boolean;
  @Input() checkIfAllFieldsArePrefilled: (
    addressForm: UntypedFormGroup,
    form: BehaviorSubject<{ isSaved: boolean }>,
    isDisplayErrors: boolean,
  ) => boolean;
  @Input() updateDefaultFlagOnAddresses: (address: Address, addresses_: BehaviorSubject<Address[]>) => void;
  @Input() isInputOptional: (key: string) => boolean;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();
  userInfo: Principal;

  billingDetails: UntypedFormGroup;
  deliveryDetails: UntypedFormGroup;

  deliveryAddresses_: BehaviorSubject<any> = this.checkoutService.deliveryAddresses_;
  isDeliveryLoading_: BehaviorSubject<boolean> = this.checkoutService.isDeliveryLoading_;

  isEditable_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  faCheck: IconDefinition = faCheck;
  faTimes: IconDefinition = faTimes;
  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;
  triggerDeliveryInlineValidationOnLoad_ = this.checkoutService.triggerDeliveryInlineValidationOnLoad_;
  scrollToDeliveryFormOnError_ = this.checkoutService.scrollToDeliveryFormOnError_;

  // In the old implementation, the FE receives a flag currentShippingAddressId in only address is available
  // Guest users and B2E type of users would not have delivery details pre-populated
  constructor(
    private checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
  ) {}

  ngOnInit() {
    (<AbstractControl>this.billingDetails) = this.billingForm.get('billingDetails');
    (<AbstractControl>this.deliveryDetails) = this.billingForm.get('deliveryDetails');
    this.userInfo = this.cartData_.getValue().user;

    if (!this.isUserTypeAlwaysHasSameDelivery()) {
      // this is called if user is on addresses page
      this.listenDeliveryAddressDetails();
    }
  }

  isUserTypeAlwaysHasSameDelivery() {
    return this.userInfo.type.toUpperCase() === 'GUEST' || this.userInfo.type.toUpperCase() === 'B2E';
  }

  listenDeliveryAddressDetails(): void {
    this.isDeliveryLoading_.next(true);
    this.checkoutService.deliveryAddresses_
      .pipe(
        filter(Boolean),
        tap((data: Address[]) => this.displayRelevantAddress(data)),
      )
      .subscribe();
  }

  displayRelevantAddress(data: Address[]): void {
    const addressOnCart: Address = this.cartData_.getValue().deliveryAddress;
    const deliveryAddressesFromDB: Address[] = this.checkoutService.deliveryAddresses_.getValue();
    // set delivery and billing the same only when
    // billing address has been set for the user
    // there is only one billing address returned from database
    // billing address and delivery address from the cart have the same id
    if (
      addressOnCart &&
      this.checkoutService.billingAddresses_.getValue().length <= 1 &&
      addressOnCart?.id === this.cartData_.getValue().billingAddress.id
    ) {
      this.billingForm.patchValue({
        isDeliverySame: true,
      });
      this.checkoutService.isDeliverySame_.next(true);
      this.isDeliveryLoading_.next(false);
      return;
    }
    // If user has a saved shipping address on their cart, it must be validated
    // Otherwise the checks for shipping addresses from DB are continued
    if (addressOnCart && deliveryAddressesFromDB.map((addresses) => addresses.id).indexOf(addressOnCart.id) >= 0) {
      if (this.isSavedAddressValid(addressOnCart, this.userInfo.type)) {
        this.selectDeliveryAddress(addressOnCart, true);
        this.isDetailsFormSaved_.next({ isSaved: true });
      } else {
        this.selectDeliveryAddress(addressOnCart, true);
        this.checkoutService.triggerDeliveryInlineValidationOnLoad_.next(
          !this.checkIfAllFieldsArePrefilled(this.deliveryDetails, this.isDetailsFormSaved_, false),
        );
        this.isEditable_.next(false);
        this.checkoutService.scrollToDeliveryFormOnError_.next(true);
      }
      this.setDeliveryAddressId_.next(addressOnCart.id);
    } else if (!addressOnCart && data.length === 1 && data[0].billingAddress) {
      // If no delivery addresses is saved
      // this must be set to the form so that postal and phone number validations work
      this.deliveryDetails.patchValue({
        countryCode: this.getCountryDetails('countryCode'),
      });
      this.isDetailsFormSaved_.next({ isSaved: false });
    } else if (data.length < 1) {
      // Or only one address is returned and it is set as billing
      this.ifSavedDeliveryNone();
    } else if (data.length === 1 && this.isSavedAddressValid(data[0], this.userInfo.type)) {
      // If one delivery address is returned and it is not billingAddress
      this.ifSavedDeliveryIsOne(data[0]);
    } else if (data.length > 1) {
      // If cart has no address saved to cart and there are multiple shipping addresses, then just display the list
      this.isDetailsFormSaved_.next({ isSaved: true });
    }

    this.isDeliveryLoading_.next(false);
  }

  editSpecificAddress(address: Address): void {
    // Mark as untouched if user has previously opened the form
    let savedData: Address;
    this.isDetailsFormSaved_.next({ isSaved: false });
    this.isEditable_.next(true);
    this.checkoutService.triggerDeliveryInlineValidationOnLoad_.next(!!address);
    this.checkoutService.scrollToDeliveryFormOnError_.next(false);

    // This is run when B2B user clicks to add new delivery address
    if (!address && this.cartStoreService.isCartUserB2B()) {
      // @ts-ignore
      const isCountryEU = Object.values(BIZCountryCodesEnum).includes(this.cartData_.getValue().billingAddress);

      savedData = {
        companyName: this.billingDetails?.get('companyName')?.value,
        country: {
          isocode: this.getCountryDetails('countryCode'),
          // HDLS-1975: If country is EU, then translation will be based on countryCode, otherwise display the country name
          name: isCountryEU ? '' : this.getCountryDetails('countryName'),
        },
      };
    } else {
      savedData = address;
    }

    if (!address) {
      this.deliveryDetails.reset();
    }

    this.selectDeliveryAddress(savedData, true);
  }

  getAddressDetails(): void {
    this.isDeliveryLoading_.next(true);
    this.checkoutService.getDeliveryDetails().subscribe((data: any) => {
      if (data) {
        this.checkoutService.deliveryAddresses_.next(data);

        this.displayRelevantAddress(data);
        this.isDeliveryLoading_.next(false);
      }
    });
  }

  setDeliveryAddress(address: Address): void {
    const isAddressValid = this.isSavedAddressValid(address, this.cartData_.getValue().user.type);

    this.isDeliveryLoading_.next(true);
    this.selectDeliveryAddress(address, true);
    this.checkoutService.triggerDeliveryInlineValidationOnLoad_.next(!isAddressValid);

    this.checkoutService
      .setDeliveryAddress(address.id)
      .pipe(
        tap(() => {
          const delivery = address;
          this.cartStoreService.updateCartState('deliveryAddress', delivery);
          this.isDetailsFormSaved_.next({ isSaved: true });
          this.setDeliveryAddressId_.next(address.id);
        }),
      )
      .subscribe(() => this.isDeliveryLoading_.next(false));
  }

  selectDeliveryAddress(address: any, isIdSame: boolean): void {
    this.isDeliveryLoading_.next(true);

    this.deliveryDetails.patchValue({
      id: isIdSame ? address?.id : '',
      firstName: address?.firstName,
      lastName: address?.lastName,
      line1: address?.line1,
      line2: address?.line2,
      town: address?.town,
      countryCode: this.getCountryDetails('countryCode', address?.country?.isocode),
      countryName: address?.country?.name,
      postalCode: address?.postalCode,
      phone: this.checkoutService.setMobileNumberWithoutFallback(address),
    });

    // HDLS-2631: display regions
    if (address?.region?.name) {
      this.deliveryDetails.get('region').setValue(address?.region?.name);
    } else {
      this.deliveryDetails.get('region').setValue('');
    }

    if (this.cartStoreService.isCartUserB2B()) {
      this.deliveryDetails.patchValue({
        companyName: address?.companyName,
        companyName2: address?.companyName2,
      });
    } else {
      this.deliveryDetails.patchValue({
        // This check needs to be performed for the only select control to avoid returning undefined
        titleCode: address?.titleCode ? address?.titleCode : null,
      });
    }
  }

  getCountryDetails(controlName?: string, value?: string): string {
    if (value) {
      return value;
    } else {
      return this.billingForm?.get('billingDetails')?.get(controlName)?.value;
    }
  }

  isAddressDisplayable(address: Address): boolean {
    // Display address if it's not marked as billingAddress or there are multiple billing addresses available
    if (!address.billingAddress || this.checkoutService.billingAddresses_.getValue().length > 1) {
      return true;
    } else {
      return false;
    }
  }

  ifSavedDeliveryNone(): void {
    // If no delivery addresses are returned and shippingAddress on billing is true, then set shipping as billing
    if (
      this.checkoutService.billingAddresses_.getValue()?.length <= 1 &&
      this.checkoutService.billingAddresses_.getValue()[0]?.shippingAddress
    ) {
      this.billingForm.patchValue({
        isDeliverySame: true,
      });
      this.checkoutService.isDeliverySame_.next(true);
    } else {
      this.deliveryDetails.patchValue({
        countryCode: this.activeSiteId,
      });
      // If user has not shipping address saved, populate country code and name from the billing addresses
      if (this.cartStoreService.isCartUserB2B()) {
        this.deliveryDetails.patchValue({
          companyName: this.cartData_.getValue().billingAddress.companyName,
        });
      }
    }
  }

  ifSavedDeliveryIsOne(data: Address): void {
    // Set the default address by the address returned from cart response
    if (this.cartData_.getValue().deliveryAddress) {
      this.selectDeliveryAddress(this.cartData_.getValue().deliveryAddress, true);
      // pre-select the address
      this.setDeliveryAddressId_.next(this.cartData_.getValue().deliveryAddress.id);
    } else {
      // If cart has no delivery address saved yet, populate the fields with the first address response
      // Pass false for isIdSame to save the address with new id
      if (!data[0]?.billingAddress || this.checkoutService.billingAddresses_.getValue().length > 1) {
        this.selectDeliveryAddress(data[0], true);
        this.isDetailsFormSaved_.next({ isSaved: true });
      } else {
        this.selectDeliveryAddress(data[0], false);
        this.isDetailsFormSaved_.next({ isSaved: false });
        this.isEditable_.next(false);
      }
    }
  }

  isB2BAccount(): boolean {
    return this.cartStoreService.isCartUserB2B();
  }
}
