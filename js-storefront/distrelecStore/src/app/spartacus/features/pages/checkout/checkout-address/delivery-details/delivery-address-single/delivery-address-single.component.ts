import { AfterViewInit, Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faExclamationTriangle, faLocationDot, faTimes, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CartStoreService } from '@state/cartState.service';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { Address, WindowRef } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-delivery-address-single',
  templateUrl: './delivery-address-single.component.html',
  styleUrls: ['./delivery-address-single.component.scss'],
})
export class DeliveryAddressSingleComponent implements AfterViewInit {
  @Input() addressPosition: number;
  @Input() deliveryDetails: UntypedFormGroup;
  @Input() address;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDeliveryLoading_?: BehaviorSubject<boolean>;
  @Input() deliveryAddresses_: BehaviorSubject<any>;
  @Input() billingForm: UntypedFormGroup;
  @Input() isEditable_: BehaviorSubject<boolean>;

  @Input() displayRelevantAddress: () => void;
  @Input() selectDeliveryAddress: (address: Address, isFormSaved_: BehaviorSubject<{ isSaved: boolean }>) => void;
  @Input() editSpecificAddress: (address: Address) => void;
  @Input() getAddressDetails: () => void;
  @Input() setDeliveryAddress: (address: Address) => void;
  @Input() checkIfAllFieldsArePrefilled: () => void;
  @Input() isSavedAddressValid: (address: Address) => void;
  @Input() isB2BAccount: () => boolean;
  @Input() isInputOptional: () => void;
  @Input() getCountryDetails: () => void;
  @Input() updateDefaultFlagOnAddresses: (address: Address, addresses_: BehaviorSubject<Address[]>) => void;

  isDelete: boolean;

  billingAddresses_: BehaviorSubject<Address[]> = this.checkoutService.billingAddresses_;
  isBillingLoading_: BehaviorSubject<boolean> = this.checkoutService.isBillingLoading_;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();

  faLocationDot: IconDefinition = faLocationDot;
  faExclamationTriange: IconDefinition = faExclamationTriangle;
  faTimes: IconDefinition = faTimes;

  constructor(
    private cartStoreService: CartStoreService,
    public checkoutService: CheckoutService,
    private winRef: WindowRef,
  ) {}

  ngAfterViewInit(): void {
    if (this.checkoutService.isAddressEditClicked() === 'delivery') {
      const selectedElementId = 'selectDeliveryAddressInput_' + this.setDeliveryAddressId_.getValue();

      setTimeout(() =>
        this.winRef.document.getElementById(selectedElementId).scrollIntoView({
          behavior: 'smooth',
          block: 'center',
        }),
      );
      this.checkoutService.setIsAddressEditClicked(null);
    }
  }

  removeAddress(addressId: string): void {
    this.isDeliveryLoading_.next(true);
    // If user deletes selected address,
    // Disable the continue to review button by assigning id to none
    if (this.setDeliveryAddressId_.getValue() === addressId) {
      this.removeDeliveryOnForm();
    }

    this.checkoutService
      .removeDeliveryAddress(addressId)
      .pipe(
        switchMap(() => {
          const data = this.checkoutService.deliveryAddresses_.getValue();
          this.checkoutService.updateAddressSubject(this.deliveryAddresses_, this.addressPosition);
          // If the only available address is billing address and billing address has shippingAddress true
          // Set billing and delivery as same, otherwise expand the form
          if (data.length === 1 && data[0].billingAddress && this.billingAddresses_.getValue().length === 1) {
            return of(this.validateAddressIfLastRemoved());
          } else if (data.length === 1) {
            return this.setDeliveryAddressToCart(data[0]);
          } else if (this.setDeliveryAddressId_.getValue() === 'none') {
            // If more than one delivery is available and the deleted address was previously selected,
            // Then close the form and pre-select the first address from the list that is not billing address
            const deliveryAddressToSet = data[0]?.billingAddress ? data[1] : data[0];
            return this.setDeliveryAddressToCart(deliveryAddressToSet);
          }
          return EMPTY;
        }),
      )
      .subscribe();
  }

  setDeliveryAddressToCart(address: Address): Observable<void> {
    this.isDeliveryLoading_.next(true);
    return this.checkoutService.setDeliveryAddress(address.id).pipe(
      first(),
      catchError(() => of(this.isDeliveryLoading_.next(false))),
      tap(() => {
        this.selectDeliveryAddress(address, this.isDetailsFormSaved_);
        this.cartStoreService.updateCartState('deliveryAddress', address);
        this.setDeliveryAddressId_.next(address.id);
        this.isDetailsFormSaved_.next({ isSaved: true });
        this.isDeliveryLoading_.next(false);
      }),
    );
  }

  validateAddressIfLastRemoved(): void {
    if (this.billingAddresses_.getValue().length === 1 && this.billingAddresses_.getValue()[0].shippingAddress) {
      this.isBillingLoading_.next(true);
      this.setDeliveryAddressToCart(this.billingAddresses_.getValue()[0]).subscribe(() => {
        this.isBillingLoading_.next(false);
        this.isDeliveryLoading_.next(false);
        this.billingForm.patchValue({
          isDeliverySame: true,
        });
        this.checkoutService.isDeliverySame_.next(true);
      });
    } else {
      this.isDetailsFormSaved_.next({ isSaved: false });
      this.deliveryDetails.reset();
    }
  }

  setDeliveryDefault(): void {
    this.isDeliveryLoading_.next(true);
    this.checkoutService
      .setDefaultAddress(this.address.id)
      .pipe(
        tap((address: Address) => {
          this.updateDefaultFlagOnAddresses(address, this.checkoutService.deliveryAddresses_);
          this.isDeliveryLoading_.next(false);
        }),
        catchError(() => of(this.isDeliveryLoading_.next(false))),
      )
      .subscribe();
  }

  removeDeliveryOnForm() {
    this.setDeliveryAddressId_.next('none');
  }
}
