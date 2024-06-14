import {
  AfterContentInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import {
  faAngleDown,
  faCheck,
  faCircleExclamation,
  faSquareCheck,
  faTimes,
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { SiteConfigService } from '@services/siteConfig.service';
import { LocalStorageService } from '@services/local-storage.service';
import { catchError, first, switchMap, take, tap } from 'rxjs/operators';
import { CartStoreService } from '@state/cartState.service';
import { HttpErrorResponse } from '@angular/common/http';
import { SiteIdEnum } from '@model/site-settings.model';
import { PostalValidation } from '@helpers/postal-validations';
import { CountryService } from '@context-services/country.service';
import { Address } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';
import { Countries } from '@model/address.model';

@Component({
  selector: 'app-address-form-billing',
  templateUrl: './address-form.component.html',
})
export class BillingAddressFormComponent implements OnInit, OnDestroy, AfterContentInit {
  @Input() billingForm: UntypedFormGroup;
  @Input() isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() activeSiteId: string;
  @Input() isEditAddress_: BehaviorSubject<boolean>;
  @Input() isExportShop: boolean;
  @Output() cancelEditing = new EventEmitter<boolean>();

  billingDetails: UntypedFormGroup;

  countryData_: BehaviorSubject<Countries> = new BehaviorSubject<Countries>(null);
  billingCountryData_: BehaviorSubject<Countries> = this.checkoutService.billingCountryData_;
  deliveryGuestCountryData_: BehaviorSubject<Countries> = this.checkoutService.deliveryGuestCountryData_;
  isLoading_: BehaviorSubject<boolean> = this.checkoutService.isBillingLoading_;
  isPhoneValidationVisible_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  faCheck: IconDefinition = faCheck;
  faTimes: IconDefinition = faTimes;
  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;
  faAngleDown: IconDefinition = faAngleDown;
  faCircleExclamation: IconDefinition = faCircleExclamation;

  currentSiteId: string = this.siteConfigService.getCurrentSiteId();

  errorMessages: [{ control: string; message: string }] = [{ control: '', message: '' }];
  postalCodeModel: string;
  isUserGuest: boolean = this.cartStoreService.isCartUserGuest();
  isUserB2B: boolean = this.cartStoreService.isCartUserB2B();
  isUserB2BKey: boolean = this.cartStoreService.isCartUserB2BKey();

  private subscriptions: Subscription = new Subscription();
  private phoneChangeSubscription: Subscription = new Subscription();
  private saveAddressForGuestSubscription: Subscription;

  constructor(
    private checkoutService: CheckoutService,
    private siteConfigService: SiteConfigService,
    private cartStoreService: CartStoreService,
    private localStorageService: LocalStorageService,
    private postalValidation: PostalValidation,
    private cdRef: ChangeDetectorRef,
    private countryService: CountryService,
  ) {}

  ngOnInit(): void {
    this.isLoading_.next(true);

    // @ts-ignore
    this.billingDetails = this.billingForm.get('billingDetails');

    // For BIZ and GUEST we are fetching countries from "/countries/delivery/guest" endpoint
    // Other shops (except BIZ shop) uses "/countries" endpoint
    // On BIZ we are using country from user
    if (this.currentSiteId === SiteIdEnum.EX) {
      if (this.isUserGuest) {
        if (!this.deliveryGuestCountryData_.getValue()) {
          this.subscriptions.add(
            this.checkoutService
              .getDeliveryGuestCountries()
              .subscribe((data) => this.handleCountriesAndUnsubscribe(data)),
          );
        } else {
          this.countryData_ = this.deliveryGuestCountryData_;
          this.isLoading_.next(false);
        }
      } else {
        this.countryData_.next({
          countries: [
            {
              name: this.countryName().value,
              isocode: this.countryCode().value,
            },
          ],
        });

        this.isLoading_.next(false);
      }
    } else {
      if (!this.billingCountryData_.getValue()) {
        this.subscriptions.add(
          this.checkoutService.getCountries('BILLING').subscribe((data) => this.handleCountriesAndUnsubscribe(data)),
        );
      } else {
        this.countryData_ = this.billingCountryData_;
        this.isLoading_.next(false);
      }
    }

    this.phoneChangeSubscription.add(
      this.checkoutService.phoneValidationUpdated.subscribe(() => this.cdRef.detectChanges()),
    );

    this.isPhoneValidationVisible_.next(!this.isNewAddress());
  }

  ngAfterContentInit(): void {
    this.updatePostalCodeModel();
  }

  cancelChanges(): void {
    this.isEditAddress_.next(false);
    this.isBillingFormSaved_.next({ isSaved: true });
    this.resetSingleBillingAddressValuesInInfoMode();
  }

  triggerDetailsToSave(): void {
    // run another validation before saving or updating address
    this.checkoutService.triggerPostcodeValidation(this.billingDetails);

    if (this.billingDetails.valid) {
      this.isLoading_.next(true);
      // Update or save billing address for not guest users
      if (!this.cartStoreService.isCartUserGuest()) {
        // If user is has id in the form, then it needs updating, if no addresses have been retuned previously then the new address must be created first and then set
        if (this.billingDetails.get('id').value) {
          this.updateBillingAddress();
        } else {
          this.saveBillingAddress();
        }
        // Update or save billing address for guest user
      } else {
        this.triggerSaveForGuest();
      }
    }
  }

  updateBillingAddress(): void {
    let response: Address;
    this.checkoutService
      .updateBillingAdress(this.billingDetails)
      .pipe(
        first(),
        // check if cart already has the address set with the same blling id
        // if not, set it
        switchMap((data) => {
          response = data;
          if (
            this.cartStoreService.getCartState().getValue()?.billingAddress?.id !== this.billingDetails.get('id').value
          ) {
            return this.checkoutService.setBillingAddress(this.billingDetails.get('id').value);
          }
          return of(null);
        }),
        switchMap(() => {
          if (this.cartStoreService.isCartUserB2E() || this.billingForm.get('isDeliverySame').value) {
            if (
              this.cartStoreService.getCartState().getValue()?.deliveryAddress?.id !==
              this.billingDetails.get('id').value
            ) {
              return this.checkoutService.setDeliveryAddress(this.billingDetails.get('id').value).pipe(
                tap(() => {
                  this.finishLoading(false, true, response);
                }),
              );
            } else {
              this.finishLoading(false, true, response);
            }
          }
          this.finishLoading(false, true, response);
          return of();
        }),
        catchError((res) => of(this.handleFormErrorResponse(res))),
      )
      .subscribe();
  }

  handleFormErrorResponse(res: HttpErrorResponse): void {
    this.isLoading_.next(false);
    // Go through every error that came from backend
    res.error?.errors?.forEach((error) => {
      // Check each control that requires validation
      Object.keys(this.billingDetails.controls).forEach((key: any) => {
        this.checkoutService.handleFormErrorResponseFields(this.billingDetails, error.subject, key);
      });
    });
  }

  getErrorMessage(control: string): string {
    let message: string;
    this.errorMessages.forEach((error) => {
      if (error.control === control) {
        message = error.message;
      }
    });
    return message;
  }

  saveBillingAddress(): void {
    this.checkoutService
      .createBillingAddress(this.billingDetails, this.billingForm.get('isDeliverySame').value)
      .pipe(
        first(),
        switchMap((data: Address) => {
          // After user has saved B2E address, set it on the cart for billing and delivery addresses
          if (this.cartStoreService.isCartUserB2E()) {
            this.localStorageService.setItem('addressId', data.id);

            return this.checkoutService.setBillingAddress(data.id).pipe(
              tap(() => this.checkoutService.billingAddresses_.next([data])),
              switchMap(() => {
                this.isLoading_.next(true);
                return this.checkoutService.setDeliveryAddress(data.id);
              }),
              tap(() => this.finishLoading(true, true, data)),
            );
          } else {
            this.finishLoading(true, true, data);
            return of();
          }
        }),
        catchError((res: HttpErrorResponse) => of(this.handleFormErrorResponse(res))),
      )
      .subscribe();
  }

  finishLoading(isPatchId: boolean, isFormSaved: boolean, data?: Address): void {
    if (isFormSaved && data) {
      this.cartStoreService.updateCartState('billingAddress', data);
      this.billingDetails.patchValue({
        countryName: data.country?.name,
      });
    }
    this.isBillingFormSaved_.next({ isSaved: isFormSaved });
    this.isLoading_.next(false);
    this.isEditAddress_.next(false);
    if (isPatchId && data) {
      this.billingDetails.patchValue({
        id: data.id,
      });
    }
  }

  triggerSaveForGuest(): void {
    this.updateCountryForGuestInExport();
    if (this.isNewAddress()) {
      this.saveAddressForGuestSubscription = this.checkoutService
        .createAddressForGuest(this.billingDetails)
        .pipe(
          first(),
          tap((cart: Cart) => {
            this.billingForm.patchValue({
              isDeliverySame: true,
            });
            this.checkoutService.isDeliverySame_.next(true);
            this.finishLoading(true, true, cart.billingAddress);
            this.saveAddressForGuestSubscription.unsubscribe();
            this.cartStoreService.setCartState(cart);
          }),
          catchError((res: HttpErrorResponse) =>
            of(this.handleFormErrorResponse(res), this.saveAddressForGuestSubscription.unsubscribe()),
          ),
        )
        .subscribe();
    } else {
      this.saveAddressForGuestSubscription = this.checkoutService
        .updateGuestAddress(this.billingDetails)
        .pipe(
          first(),
          tap((cart: Cart) => {
            this.finishLoading(true, true, cart.billingAddress);
            this.cartStoreService.setCartState(cart);
            this.saveAddressForGuestSubscription.unsubscribe();
          }),
          catchError((res: HttpErrorResponse) =>
            of(this.handleFormErrorResponse(res), this.saveAddressForGuestSubscription.unsubscribe()),
          ),
        )
        .subscribe();
    }
  }

  displayTitleFirstAndLast(): boolean {
    return !this.cartStoreService.isCartUserB2B();
  }

  onControlTouch(controlName: string): void {
    this.checkoutService.onControlTouch(controlName, this.billingDetails);

    if (controlName === 'countryCode') {
      this.checkoutService.triggerPostcodeValidation(this.billingDetails);
      this.isPhoneValidationVisible_.next(true);
      this.phone()?.updateValueAndValidity();
    }
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
      this.subscriptions.unsubscribe();
    }

    this.isLoading_.next(false);
  }

  resetSingleBillingAddressValuesInInfoMode(): void {
    this.cancelEditing.emit();
  }

  getPostalCodeValidationKey(): string {
    return this.countryCode().value
      ? 'validations.enter_postal_' + this.countryCode().value
      : 'validations.enter_postal_';
  }

  checkIfFieldIsValid(controlName: string): boolean {
    return this.checkoutService.checkIfFieldIsValid(controlName, this.billingDetails);
  }

  companyName2(): AbstractControl {
    return this.billingDetails.get('companyName2');
  }

  titleCode(): AbstractControl {
    return this.billingDetails.get('titleCode');
  }

  firstName(): AbstractControl {
    return this.billingDetails.get('firstName');
  }

  lastName(): AbstractControl {
    return this.billingDetails.get('lastName');
  }

  email(): AbstractControl {
    return this.billingDetails.get('email');
  }

  line1(): AbstractControl {
    return this.billingDetails.get('line1');
  }

  line2(): AbstractControl {
    return this.billingDetails.get('line2');
  }

  town(): AbstractControl {
    return this.billingDetails.get('town');
  }

  countryCode(): AbstractControl {
    return this.billingDetails.get('countryCode');
  }

  countryName(): AbstractControl {
    return this.billingDetails.get('countryName');
  }

  postalCode(): AbstractControl {
    return this.billingDetails.get('postalCode');
  }

  phone(): AbstractControl {
    return this.billingDetails.get('phone');
  }

  codiceFiscale(): AbstractControl {
    return this.billingDetails.get('codiceFiscale');
  }

  codiceDestinario(): AbstractControl {
    return this.billingDetails.get('codiceDestinario');
  }

  codiceCUP(): AbstractControl {
    return this.billingDetails.get('codiceCUP');
  }

  codiceCIG(): AbstractControl {
    return this.billingDetails.get('codiceCIG');
  }

  region(): AbstractControl {
    return this.billingDetails.get('region');
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.phoneChangeSubscription.unsubscribe();
  }

  private isNewAddress(): boolean {
    return !this.billingDetails.get('id').value;
  }

  private updateCountryForGuestInExport(): void {
    if (this.isExportShop) {
      this.countryService
        .getActive()
        .pipe(take(1))
        .subscribe((country) => {
          if (!!this.countryCode().value && country !== this.countryCode().value) {
            this.countryService.setActive(this.countryCode().value);
          }
        });
    }
  }
}
