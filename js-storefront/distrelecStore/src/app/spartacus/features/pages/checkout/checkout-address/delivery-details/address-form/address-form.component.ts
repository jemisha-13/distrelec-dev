import { AfterContentInit, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faAngleDown, faCheck, faSquareCheck, faTimes, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { Address, WindowRef } from '@spartacus/core';
import { SiteConfigService } from '@services/siteConfig.service';
import { catchError, first, map, switchMap, tap } from 'rxjs/operators';

import { CartStoreService } from '@state/cartState.service';
import { HttpErrorResponse } from '@angular/common/http';
import { SiteIdEnum } from '@model/site-settings.model';
import { PostalValidation } from '@helpers/postal-validations';
import { Countries } from '@model/address.model';

@Component({
  selector: 'app-address-form-delivery',
  templateUrl: './address-form.component.html',
})
export class AddressFormDeliveryComponent implements OnInit, AfterContentInit, OnDestroy {
  @Input() deliveryDetails: UntypedFormGroup;
  @Input() billingForm: UntypedFormGroup;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;
  @Input() activeSiteId: string;
  @Input() isDeliveryLoading_: BehaviorSubject<boolean>;
  @Input() isEditable_: BehaviorSubject<boolean>;
  @Input() isB2BAccount: () => boolean;
  @Input() triggerDeliveryInlineValidationOnLoad: boolean;
  @Input() scrollToDeliveryFormOnError: boolean;

  deliveryAddresses_: BehaviorSubject<any> = this.checkoutService.deliveryAddresses_;
  countryData_: BehaviorSubject<Countries> = new BehaviorSubject<Countries>(null);
  shippingCountryData_: BehaviorSubject<Countries> = this.checkoutService.shippingCountryData_;
  deliveryGuestCountryData_: BehaviorSubject<Countries> = this.checkoutService.deliveryGuestCountryData_;
  isBillingLoading_: BehaviorSubject<boolean> = this.checkoutService.isBillingLoading_;
  isPhoneValidationVisible_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  errorMessages: [{ control: string; message: string }] = [{ control: '', message: '' }];

  postalCodeModel: string;
  currentSiteId: string = this.siteConfigService.getCurrentSiteId();

  faCheck: IconDefinition = faCheck;
  faTimes: IconDefinition = faTimes;
  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;
  faAngleDown: IconDefinition = faAngleDown;

  isUserGuest: boolean = this.cartStoreService.isCartUserGuest();
  isUserB2BKey: boolean = this.cartStoreService.isCartUserB2BKey();

  private phoneChangeSubscription: Subscription = new Subscription();

  constructor(
    private renderer: Renderer2,
    private winRef: WindowRef,
    public checkoutService: CheckoutService,
    private siteConfigService: SiteConfigService,
    private cartStoreService: CartStoreService,
    private postalValidation: PostalValidation,
    public cdRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.isDeliveryLoading_.next(true);

    // For BIZ and GUEST we are fetching countries from "/countries/delivery/guest" endpoint
    // Other shops (except BIZ shop) uses "/countries" endpoint
    // On BIZ we are using country from user
    if (this.currentSiteId === SiteIdEnum.EX) {
      if (this.isUserGuest) {
        if (!this.deliveryGuestCountryData_.getValue()) {
          this.checkoutService
            .getDeliveryGuestCountries()
            .subscribe((data) => this.handleCountriesAndUnsubscribe(data));
        } else {
          this.countryData_ = this.deliveryGuestCountryData_;
          this.isDeliveryLoading_.next(false);
        }
      } else {
        // We are fetching countryCode from billing form in case we don't have countryCode set on delivery address yet
        const billingCountryCode: string = this.billingForm?.get('billingDetails')?.get('countryCode')?.value;
        const billingCountryName: string = this.billingForm?.get('billingDetails')?.get('countryName')?.value;

        this.countryData_.next({
          countries: [
            {
              name: this.countryName().value !== '' ? this.countryName().value : billingCountryName,
              isocode: this.countryCode().value !== '' ? this.countryCode().value : billingCountryCode,
            },
          ],
        });

        this.isDeliveryLoading_.next(false);
      }
    } else {
      if (!this.shippingCountryData_.getValue()) {
        this.checkoutService.getCountries('SHIPPING').subscribe((data) => this.handleCountriesAndUnsubscribe(data));
      } else {
        this.countryData_ = this.shippingCountryData_;
        this.isDeliveryLoading_.next(false);
      }
    }

    this.hidePhoneValidationIfNewAddress();

    this.phoneChangeSubscription.add(
      this.checkoutService.phoneValidationUpdated.subscribe(() => this.cdRef.detectChanges()),
    );
  }

  ngOnDestroy(): void {
    this.checkoutService.isEditDeliveryPhoneNumber_.next(false);
    this.phoneChangeSubscription.unsubscribe();
  }

  ngAfterContentInit(): void {
    this.updatePostalCodeModel();

    if (this.scrollToDeliveryFormOnError) {
      this.winRef.document.getElementById('deliveryAddressFormWrapper').scrollIntoView({
        behavior: 'smooth',
        block: 'start',
        inline: 'nearest',
      });
    }
  }

  triggerDetailsToSave(): void {
    this.isDeliveryLoading_.next(true);
    // If user is has id in the form, then it needs updating, if no addresses have been retuned previously then the new address must be created first and then set
    if (this.deliveryDetails.get('id').value) {
      this.updateAddress();
    } else {
      this.saveAddress();
    }
  }

  updateAddress(): void {
    const index: number = this.deliveryAddresses_
      .getValue()
      .map((addresses) => addresses.id)
      .indexOf(this.deliveryDetails.get('id').value);

    const addressId: string = this.deliveryAddresses_.getValue()[index].id;

    this.checkoutService
      .updateDeliveryAdress(this.deliveryDetails, index)
      .pipe(
        first(),
        catchError((res) => of(this.completeLoading(false), this.handleFormErrorResponse(res))),
        switchMap((address: Address) => {
          // update the cart state if the edited address is also set to cart
          if (this.cartStoreService.getCartState().getValue().deliveryAddress?.id === address?.id) {
            this.cartStoreService.updateCartState('deliveryAddress', address);
            this.completeLoading(true);
          } else if (this.checkoutService.isEditDeliveryPhoneNumber_.getValue()) {
            // set delivery to cart if user selected this address but it had phone validation errors

            return this.checkoutService.setDeliveryAddress(addressId).pipe(
              tap(() => {
                this.setDeliveryAddressId_.next(addressId);
                this.cartStoreService.updateCartState('deliveryAddress', address);
                this.completeLoading(true);
              }),
            );
          }
          return of();
        }),
      )
      .subscribe();
  }

  saveAddress(): void {
    this.checkoutService
      .createDeliveryAddress(this.deliveryDetails)
      .pipe(
        switchMap((data: Address) => this.checkoutService.setDeliveryAddress(data.id).pipe(map(() => data))),
        tap((data: Address) => {
          this.completeLoading(true, true, data);
          this.setDeliveryAddressId_.next(data.id);
        }),
        catchError((res: HttpErrorResponse) => of(this.completeLoading(false), this.handleFormErrorResponse(res))),
      )
      .subscribe();
  }

  completeLoading(isCloseForm: boolean, isPushNewAddress?: boolean, address?: Address): void {
    if (isPushNewAddress) {
      this.cartStoreService.updateCartState('deliveryAddress', address);
      const newBillingData: Address[] = this.deliveryAddresses_.getValue();
      newBillingData.push(address);
      this.deliveryAddresses_.next(newBillingData);
    }
    this.isDetailsFormSaved_.next({ isSaved: isCloseForm });
    this.isDeliveryLoading_.next(false);
  }

  handleFormErrorResponse(res: HttpErrorResponse): void {
    this.isDeliveryLoading_.next(false);
    // Go through every error that came from backend
    res.error?.errors?.forEach((error) => {
      // Check each control that requires validation
      Object.keys(this.deliveryDetails.controls).forEach((key: any) => {
        this.checkoutService.handleFormErrorResponseFields(this.deliveryDetails, error.subject, key);
      });
    });
  }

  getErrorMessage(control): string {
    let message: string;
    this.errorMessages.forEach((error) => {
      if (error.control === control) {
        message = error.message;
      }
    });
    return message;
  }

  cancelChanges(): void {
    this.isDetailsFormSaved_.next({ isSaved: true });
    // If user cancels changes when no delivery address has been saved, then set delivery & billing as same
    if (!this.deliveryDetails.get('id').value && this.deliveryAddresses_.getValue().length <= 1) {
      this.isDeliveryLoading_.next(true);
      this.isBillingLoading_.next(true);

      // Set delivery to be the same value as billing
      this.checkoutService
        .setDeliveryAddress(this.billingForm.get('billingDetails').get('id').value)
        .pipe(
          first(),
          tap(() => {
            this.isDeliveryLoading_.next(false);
            this.isBillingLoading_.next(false);
            this.billingForm.patchValue({
              isDeliverySame: true,
            });
            this.checkoutService.isDeliverySame_.next(true);
          }),
          // If error occurs, then turn off loading screen and open the form again
          catchError(() =>
            of(
              this.isDeliveryLoading_.next(false),
              this.isBillingLoading_.next(false),
              this.isDetailsFormSaved_.next({ isSaved: false }),
            ),
          ),
        )
        .subscribe();
    }
  }

  displayTitleFirstAndLast(): boolean {
    return !this.cartStoreService.isCartUserB2B();
  }

  onControlTouch(controlName: string): void {
    this.checkoutService.onControlTouch(controlName, this.deliveryDetails);

    if (controlName === 'countryCode') {
      this.checkoutService.triggerPostcodeValidation(this.deliveryDetails);
      this.isPhoneValidationVisible_.next(true);
      this.phone()?.updateValueAndValidity();
    }
  }

  checkIfFieldIsValid(controlName: string): boolean {
    return this.checkoutService.checkIfFieldIsValid(controlName, this.deliveryDetails);
  }

  getTranslationKey(id: string): string {
    return 'countries.country_distrelec_' + id;
  }

  updatePostalCodeModel(): void {
    // TODO: Check error msgs, for SE message is not correct
    this.postalCodeModel = this.postalValidation.formatPostalCode(this.postalCode().value, this.countryCode().value);
  }

  handleCountriesAndUnsubscribe(data: Countries): void {
    if (data?.countries.length) {
      this.countryData_.next(data);
    }

    this.isDeliveryLoading_.next(false);
  }

  hidePhoneValidationIfNewAddress(): void {
    this.isPhoneValidationVisible_.next(this.deliveryDetails.get('id').value);
  }

  getPostalCodeValidationKey(): string {
    return this.countryCode().value !== 'null'
      ? 'validations.enter_postal_' + this.countryCode().value
      : 'validations.enter_postal_';
  }

  companyName(): AbstractControl {
    return this.deliveryDetails.get('companyName');
  }

  companyName2(): AbstractControl {
    return this.deliveryDetails.get('companyName2');
  }

  titleCode(): AbstractControl {
    return this.deliveryDetails.get('titleCode');
  }

  firstName(): AbstractControl {
    return this.deliveryDetails.get('firstName');
  }

  lastName(): AbstractControl {
    return this.deliveryDetails.get('lastName');
  }

  email(): AbstractControl {
    return this.deliveryDetails.get('email');
  }

  line1(): AbstractControl {
    return this.deliveryDetails.get('line1');
  }

  line2(): AbstractControl {
    return this.deliveryDetails.get('line2');
  }

  town(): AbstractControl {
    return this.deliveryDetails.get('town');
  }

  countryCode(): AbstractControl {
    return this.deliveryDetails.get('countryCode');
  }

  countryName(): AbstractControl {
    return this.deliveryDetails.get('countryName');
  }

  postalCode(): AbstractControl {
    return this.deliveryDetails.get('postalCode');
  }

  phone(): AbstractControl {
    return this.deliveryDetails.get('phone');
  }

  codiceDestinario(): AbstractControl {
    return this.deliveryDetails.get('codiceDestinario');
  }

  codiceCUP(): AbstractControl {
    return this.deliveryDetails.get('codiceCUP');
  }

  codiceCIG(): AbstractControl {
    return this.deliveryDetails.get('codiceCIG');
  }

  region(): AbstractControl {
    return this.deliveryDetails.get('region');
  }
}
