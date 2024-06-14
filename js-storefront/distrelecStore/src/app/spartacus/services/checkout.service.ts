/* eslint-disable max-len */
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { EventEmitter, Injectable } from '@angular/core';
import {
  Address,
  ClientAuthenticationTokenService,
  InterceptorUtil,
  OAuthLibWrapperService,
  OccEndpointsService,
  Region,
  USE_CLIENT_TOKEN,
  UserIdService,
  WindowRef,
} from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';
import { Order } from '@spartacus/order/root';
import { UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap, take, tap } from 'rxjs/operators';
/** Custom service  */
import { DistCartService } from './cart.service';
import { LocalStorageService } from './local-storage.service';
import { DistrelecUserService } from './user.service';
import { DistCountryCode, DistNationalNumber, PhoneNumberService } from './phonenumber.service';
/** Helpers */
import { PostalValidation } from '@helpers/postal-validations';
/** Interfaces */
import {
  GuestSubmitForm,
  HiddenPaymentForm,
  Payment,
  PaymentDetailsResponse,
  ProgressBarInterface,
  RetrieveERPCode,
  VatRequestBody,
} from '@model/checkout.model';
import { Warehouse } from '@model/order.model';
import { CartStoreService } from '@state/cartState.service';
import { BIZCountryCodesEnum, CountryCodesEnum } from '../site-context/services/country.service';
import { AddressResponse, DeliveryModes } from '@model/cart.model';
import { AddressType, Countries } from '@model/address.model';

export enum DeliveryModeEnum {
  ECONOMY = 'SAP_E1',
  STANDARD = 'SAP_N1',
  EXPRESS = 'SAP_X4',
  EXPRESS_ON_DEMAND = 'SAP_X1',
  PICKUP_TO_PLACE = 'SAP_N2',
  COLLECTION_PICKUP = 'SAP_A1',
  ECONOMY_PICKUP = 'SAP_E2',
}

export enum WarehouseEnum {
  NANIKON = '7374',
  CDC = '7371',
}

export enum RegistrationTypeEnum {
  CHECKOUT = 'CHECKOUT',
  STANDALONE = 'STANDALONE',
}

@Injectable({
  providedIn: 'root',
})
export class CheckoutService {
  checkoutPageSteps_: BehaviorSubject<ProgressBarInterface> = new BehaviorSubject<ProgressBarInterface>({});
  isFastCheckout_ = new BehaviorSubject<boolean>(null);
  // Declare payment loading spinner here to use it when user selects different payments mode or cards
  isPaymentDetailsLoading_ = new BehaviorSubject<boolean>(false);
  isSummaryLoading_ = new BehaviorSubject<boolean>(false);
  // Declare delivery loading spinner here to use it when user clicks same delivery inside billing component
  isDeliveryLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isBillingLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isDeliveryOptLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  deliveryModes_: BehaviorSubject<DeliveryModes> = new BehaviorSubject<DeliveryModes>(null);
  pickupData_: BehaviorSubject<Warehouse[]> = new BehaviorSubject<Warehouse[]>(null);
  paymentData_: BehaviorSubject<Payment> = new BehaviorSubject<Payment>(null);
  billingAddresses_: BehaviorSubject<Address[]> = new BehaviorSubject<Address[]>(null);
  deliveryAddresses_: BehaviorSubject<Address[]> = new BehaviorSubject<Address[]>(null);

  billingCountryData_: BehaviorSubject<Countries> = new BehaviorSubject<Countries>(null);
  shippingCountryData_: BehaviorSubject<Countries> = new BehaviorSubject<Countries>(null);
  deliveryGuestCountryData_: BehaviorSubject<Countries> = new BehaviorSubject<Countries>(null);

  isEditDeliveryPhoneNumber_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isDeliverySame_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  isAddressEditClicked_: BehaviorSubject<string> = new BehaviorSubject<string>('');

  resetPwdToken: string;
  isTokenExpired: boolean;
  isRegistrationSuccess: boolean;
  isNewCardSelected: boolean;

  errorMessage_: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  successMessage_: BehaviorSubject<string> = new BehaviorSubject<string>(null);

  userId$: Observable<string> = this.userIdService.getUserId();

  triggerDeliveryInlineValidationOnLoad_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  scrollToDeliveryFormOnError_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  phoneValidationUpdated = new EventEmitter();
  triggerPaymentIframe_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private occEndpointsService: OccEndpointsService,
    private userIdService: UserIdService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private http: HttpClient,
    private localStorageService: LocalStorageService,
    private phoneNumberService: PhoneNumberService,
    private userService: DistrelecUserService,
    private clientAuthenticationTokenService: ClientAuthenticationTokenService,
    private oAuthLibWrapperService: OAuthLibWrapperService,
    private postalValidation: PostalValidation,
    private winRef: WindowRef,
  ) {}

  clearCheckoutData() {
    this.deliveryModes_.next(null);
    this.pickupData_.next(null);
    this.billingAddresses_.next(null);
    this.deliveryAddresses_.next(null);
    this.isRegistrationSuccess = false;
  }

  getCountries(type: AddressType): Observable<Countries> {
    return this.http
      .get<Countries>(
        this.occEndpointsService.buildUrl(`countries`, {
          queryParams: {
            type,
            fields: 'FULL',
          },
        }),
      )
      .pipe(
        tap((data) =>
          type === 'BILLING' ? this.billingCountryData_.next(data) : this.shippingCountryData_.next(data),
        ),
      );
  }

  getDeliveryGuestCountries(): Observable<Countries> {
    return this.http
      .get<Countries>(this.occEndpointsService.buildUrl(`countries/delivery/guest`))
      .pipe(tap((data) => this.deliveryGuestCountryData_.next(data)));
  }

  prepareForCheckout(): Observable<Cart> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http
          .post<Cart>(
            this.occEndpointsService.buildUrl(`/users/${userId}/carts/${this.cartStoreService.getCartId()}/checkout`),
            {},
          )
          .pipe(
            tap((data: Cart) => {
              this.isFastCheckout_.next(data.eligibleForFastCheckout);
              this.cartService.populateCartData(data);
            }),
          ),
      ),
    );
  }

  validateDeliveryPage(): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.get<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/validate-delivery-page`,
          ),
        ),
      ),
    );
  }

  isBillingEditable(activeSiteId: string): boolean {
    if (this.cartStoreService.isCartUserGuest() || this.cartStoreService.isCartUserB2E()) {
      return true;
    }

    if (this.isWebshopBiz(activeSiteId)) {
      return !!this.localStorageService.getItem('IS_NEW_REGISTRATION');
    }

    if (this.cartStoreService.isCartUserB2BKey()) {
      return false;
    }

    return true;
  }

  isMultipleBillingAddresses(): boolean {
    return this.billingAddresses_.getValue()?.length > 1;
  }

  isWebshopBiz(activeSiteId): boolean {
    return activeSiteId === CountryCodesEnum.EXPORT || Object.values(BIZCountryCodesEnum).includes(activeSiteId);
  }

  getDeliveryOptions(): Observable<DeliveryModes> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http
          .get<DeliveryModes>(
            this.occEndpointsService.buildUrl(
              `/users/${userId}/carts/${this.cartStoreService.getCartId()}/deliverymodes`,
            ),
          )
          .pipe(
            tap((data: DeliveryModes) => {
              this.pickupData_.next(data.warehouses);
            }),
          ),
      ),
    );
  }

  setDeliveryMode(modeCode: string, wareHouseId?: string): Observable<Cart> {
    this.isSummaryLoading_.next(true);
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<Cart>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/deliverymode`,
            {
              queryParams: {
                deliveryModeId: modeCode,
                wareHouseId,
                fields: 'DEFAULT,user',
              },
            },
          ),
          {},
        ),
      ),
    );
  }

  getBillingDetails(): Observable<AddressResponse> {
    return this.http
      .get<AddressResponse>(
        this.occEndpointsService.buildUrl(`/users/current/addresses?fields=DEFAULT&type=BILLING`, {}),
      )
      .pipe(tap((data: AddressResponse) => this.billingAddresses_.next(data.distAddresses)));
  }

  getBillingDetail(billingId: string): Observable<Address> {
    return this.http
      .get<Address>(this.occEndpointsService.buildUrl(`/users/current/addresses/${billingId}`, {}))
      .pipe(tap((data: Address) => this.billingAddresses_.next([data])));
  }

  createBillingAddress(form: UntypedFormGroup, isDeliverySame: boolean): Observable<Address> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<Address>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses`,
            {},
          ),
          { billingAddress: true, ...this.assignAddressObject(form, false) },
        ),
      ),
    );
  }

  createDeliveryAddress(form: UntypedFormGroup): Observable<Address> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<Address>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses`,
            {},
          ),
          { shippingAddress: true, ...this.assignAddressObject(form, false) },
        ),
      ),
    );
  }

  createAddressForGuest(form: UntypedFormGroup): Observable<Cart> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<Cart>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/guest`,
          ),
          { billingAddress: true, shippingAddress: true, ...this.assignAddressObject(form, false) },
        ),
      ),
    );
  }

  updateBillingAdress(form: UntypedFormGroup): Observable<Address> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.patch<Address>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/${form.get('id').value}`,
            {},
          ),
          { billingAddress: true, shippingAddress: true, ...this.assignAddressObject(form, true) },
        ),
      ),
    );
  }

  updateDeliveryAdress(form: UntypedFormGroup, index: number): Observable<Address> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http
          .patch<Address>(
            this.occEndpointsService.buildUrl(
              `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/${form.get('id').value}`,
              {},
            ),
            { shippingAddress: true, ...this.assignAddressObject(form, true) },
          )
          .pipe(tap((data) => this.updateAddressSubject(this.deliveryAddresses_, index, data))),
      ),
    );
  }

  updateGuestAddress(form: UntypedFormGroup): Observable<Cart> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<Cart>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/guest/${form.get('id').value}`,
            {},
          ),
          { billingAddress: true, shippingAddress: true, ...this.assignAddressObject(form, true) },
        ),
      ),
    );
  }

  setBillingAddress(addressId: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/billing?addressId=${addressId}`,
            {},
          ),
          {},
        ),
      ),
    );
  }

  setDeliveryAddress(addressId: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/delivery?addressId=${addressId}`,
            {},
          ),
          {},
        ),
      ),
    );
  }

  deleteDeliveryAddressFromCart(addressId: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.delete<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/addresses/delivery?addressId=${addressId}`,
            {},
          ),
        ),
      ),
    );
  }

  setDefaultAddress(addressId): Observable<Address> {
    return this.userId$.pipe(
      switchMap((userId) =>
        this.http.post<Address>(
          this.occEndpointsService.buildUrl(`/users/${userId}/addresses/${addressId}/setDefaultAddress`),
          {},
        ),
      ),
    );
  }

  getDeliveryDetails(): Observable<Address[]> {
    return this.http
      .get<AddressResponse>(
        this.occEndpointsService.buildUrl(`/users/current/addresses?fields=DEFAULT&type=SHIPPING`) + '',
      )
      .pipe(
        map((data: AddressResponse) => data.distAddresses),
        tap((data: Address[]) => {
          this.deliveryAddresses_.next(data);
        }),
      );
  }

  scheduleDelivery(date?: string): Observable<Cart> {
    const headers = new HttpHeaders({
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'Content-Type': 'application/json',
    });

    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http
          .post<Cart>(
            this.occEndpointsService.buildUrl(`/users/${userId}/carts/${this.cartStoreService.getCartId()}/schedule`),
            date,
            { headers },
          )
          .pipe(tap((data: Cart) => this.cartService.populateCartData(data))),
      ),
    );
  }

  onCompleteDelivery(isCompleteDelivery: boolean): Observable<Cart> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<Cart>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/setCompleteDelivery?completeDelivery=${isCompleteDelivery}`,
          ),
          {},
        ),
      ),
    );
  }

  getPaymentMethods(): Observable<Payment> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http
          .get<Payment>(
            this.occEndpointsService.buildUrl(
              `/users/${userId}/carts/${this.cartStoreService.getCartId()}/payments`,
              {},
            ),
          )
          .pipe(tap((data: Payment) => this.paymentData_.next(data))),
      ),
    );
  }

  getCreditCardDetails(): Observable<PaymentDetailsResponse> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.get<PaymentDetailsResponse>(
          this.occEndpointsService.buildUrl(`/users/${userId}/payment/details`, {
            queryParams: {
              saved: true,
            },
          }),
        ),
      ),
    );
  }

  setCreditDetails(id: string): Observable<PaymentDetailsResponse> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<PaymentDetailsResponse>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/payment/details`,
            {
              queryParams: { paymentDetailsId: id },
            },
          ),
          {},
        ),
      ),
    );
  }

  removeDeliveryAddress(addressId: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.delete<any>(this.occEndpointsService.buildUrl(`/users/${userId}/addresses/${addressId}`, {})),
      ),
    );
  }

  deleteSavedCardDetails(id: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.delete<any>(this.occEndpointsService.buildUrl(`/users/${userId}/payment/details/${id}`, {})),
      ),
    );
  }

  setAsDefaultCard(id: string): Observable<PaymentDetailsResponse> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<PaymentDetailsResponse>(
          this.occEndpointsService.buildUrl(`/users/${userId}/set-payment-info/${id}`, {
            queryParams: { paymentOption: 'CreditCard' },
          }),
          {},
        ),
      ),
    );
  }

  getHiddenPaymentForm(): Observable<HiddenPaymentForm> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.get<HiddenPaymentForm>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/payment/hiddenPaymentForm`,
            {},
          ),
          {},
        ),
      ),
    );
  }

  setPaymentMode(paymentModeCode): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<any>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/payment/paymentMode`,
            {
              queryParams: {
                paymentModeCode,
                fields: 'BASIC',
              },
            },
          ),
          {},
        ),
      ),
    );
  }

  placeOrder(): Observable<Cart> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<any>(
          this.occEndpointsService.buildUrl(`/users/${userId}/orders`, {
            queryParams: {
              cartId: this.cartStoreService.getCartId(),
              fields: 'DEFAULT',
            },
          }),
          {},
        ),
      ),
    );
  }

  requestInvoicePayment(): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.put<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/payment/requestInvoicePaymentMode`,
            {
              queryParams: {
                cartId: userId,
                fields: 'DEFAULT',
              },
            },
          ),
          {},
        ),
      ),
    );
  }

  assignAddressObject(form, isIdIncluded: boolean): Address {
    const countryIsoCode = form.get('countryCode')?.value === 'null' ? null : form.get('countryCode')?.value;
    let object: Address = {
      titleCode: form.get('titleCode')?.value ? form.get('titleCode')?.value : '',
      firstName: form.get('firstName')?.value,
      lastName: form.get('lastName')?.value,
      country: {
        name: form.get('countryName')?.value,
        isocode: countryIsoCode,
      },
      companyName: form.get('companyName')?.value,
      companyName2: form.get('companyName2')?.value,
      line1: form.get('line1')?.value,
      line2: form.get('line2')?.value,
      town: form.get('town')?.value,
      postalCode: this.postalValidation.formatPostalCode(form.get('postalCode')?.value, countryIsoCode),
      checkoutPhone: form.get('phone')?.value,
      region: this.returnRegion(form),
      codiceFiscale: form.get('codiceFiscale')?.value,
    };

    if (isIdIncluded) {
      object = { id: form.get('id')?.value, ...object };
    }

    if (form.get('email')) {
      object = { email: form.get('email')?.value, ...object };
    }

    return object;
  }

  returnRegion(form): Region {
    if (form.get('formName').value === 'billingDetails') {
      return this.billingCountryData_
        .getValue()
        ?.countries[0]?.regions?.find((region) => region.name === form.get('region')?.value);
    }
    return this.shippingCountryData_
      .getValue()
      ?.countries[0]?.regions?.find((region) => region.name === form.get('region')?.value);
  }

  getFunctions(): Observable<void> {
    return this.http.get<any>(this.occEndpointsService.buildUrl(`functions`, {}));
  }

  getDepartments(): Observable<void> {
    return this.http.get<any>(this.occEndpointsService.buildUrl(`departments`, {}));
  }

  updateUserProfile(functionCode: string, departmentCode: string): Observable<any> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<any>(this.occEndpointsService.buildUrl(`/users/${userId}/update-profile-info`), {
          departmentCode,
          functionCode,
        }),
      ),
    );
  }

  getERPCode(orderCode: string): Observable<RetrieveERPCode> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.get<RetrieveERPCode>(this.occEndpointsService.buildUrl(`/users/${userId}/erpCode/${orderCode}`, {})),
      ),
    );
  }

  submitForApproval(): Observable<Order> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.get<Order>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/approve/invoice`,
            {},
          ),
        ),
      ),
    );
  }

  setCodiceFields(body: VatRequestBody): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<void>(
          this.occEndpointsService.buildUrl(`/users/${userId}/carts/${this.cartStoreService.getCartId()}/vat`),
          {
            vat4: body.vat4,
            legalEmail: body.legalEmail,
            codiceCUP: this.isValidField(body.codiceCUP, 15) ? body.codiceCUP : '',
            codiceCIG: this.isValidField(body.codiceCIG, 10) ? body.codiceCIG : '',
          },
        ),
      ),
    );
  }

  isValidField(value: string, maxLength: number): boolean {
    return value?.length === maxLength && value?.indexOf(' ') < 0;
  }

  updateAddressSubject(addresses_: BehaviorSubject<any>, index: number, newAddress?): void {
    const newBillingData: any[] = addresses_.getValue();
    // replace or completely remove the address on that index
    if (newAddress) {
      newBillingData.splice(index, 1, newAddress);
    } else {
      newBillingData.splice(index, 1);
    }
    addresses_.next(newBillingData);
  }

  requestInvoicePaymentModeForCurrentUser(): Observable<any> {
    return this.http.post<any>(this.occEndpointsService.buildUrl(`/users/current/request-invoice-payment-mode`), {});
  }

  // TODO: add the type here when user creates account from guest
  createDistrelecAccountFromGuest(data: GuestSubmitForm): Observable<any> {
    return this.http
      .post<any>(this.occEndpointsService.buildUrl(`/users?registerGuest=true&type=B2C`), {
        password: data.password,
        checkPwd: data.checkPwd,
        uid: data.uid,
        guid: data.guid,
      })
      .pipe(
        tap((cartData: Cart) => {
          this.cartStoreService.setCartState(cartData);
          this.cartService.setCartIdToLocalStorage();
        }),
      );
  }

  onControlTouch(controlName: string, form: UntypedFormGroup): void {
    // All elements which have are inside form with inline validation
    const elements = this.winRef.document.querySelectorAll('.js-validate');
    // Elements which are optional
    const optionalElements = this.winRef.document.querySelectorAll('.js-validate-optional');
    const array = [];
    const optionalsArray = [];

    // Iterate through all elements and add their "formcontrolname" into array
    elements.forEach((key) => {
      // @ts-ignore
      array.push(key.attributes.formcontrolname.nodeValue);
    });

    // Iterate through optional elements and add their "formcontrolname" into array
    optionalElements.forEach((key) => {
      // @ts-ignore
      optionalsArray.push(key.attributes.formcontrolname.nodeValue);
    });

    // With this we are controlling that only fields before clicked field are validated, not all of them
    array.every((key) => {
      if (optionalsArray.indexOf(key) >= 0) {
        // Once current item in iteration is optional field and it is item on which user made change, we are returning FALSE
        // So only fields until this will be validated
        return key !== controlName;
      } else {
        if (
          form.get(key).value === '' ||
          !form.get(key).value ||
          form.get(key).value === 'null' ||
          !form.get(key).value ||
          !!form.get(key).errors
        ) {
          this.markFieldAsInvalid(key, form);
        } else if (key === 'postalCode') {
          this.triggerPostcodeValidation(form);
        } else if (key === 'phone') {
          this.triggerPhoneFieldValidation(form);
        } else {
          this.markFieldAsValid(key, form);
        }

        // Once iteration gets to the changed item, return FALSE so only fields before this field will be validated
        return key !== controlName;
      }
    });
  }

  checkIfFieldIsValid(controlName: string, form: UntypedFormGroup): boolean {
    const controlValue = form.get(controlName).value;

    if (
      controlValue === '' ||
      controlValue === null ||
      controlValue === 'null' ||
      !controlValue ||
      !!form.get(controlName).errors
    ) {
      return false;
    } else if (controlName === 'postalCode') {
      return this.isPostalCodeValid(form);
    } else if (controlName === 'phone') {
      return form.get(controlName).valid;
    } else {
      return true;
    }
  }

  markFieldAsInvalid(controlName: string, form: UntypedFormGroup): void {
    form.get(controlName).setErrors({ erroring: true });
    form.get(controlName).markAsTouched();
  }

  markFieldAsValid(controlName: string, form: UntypedFormGroup): void {
    form.get(controlName).setErrors(null);
    form.get(controlName).markAsPristine();
  }

  triggerPostcodeValidation(form: UntypedFormGroup): void {
    // If user has not selected the country code, mark both controls with errors
    if (!form.get('countryCode').value) {
      this.markFieldAsInvalid('postalCode', form);
      this.markFieldAsInvalid('countryCode', form);
    } else {
      if (this.isPostalCodeValid(form)) {
        this.markFieldAsValid('postalCode', form);
      } else {
        this.markFieldAsInvalid('postalCode', form);
      }
    }
  }

  isPostalCodeValid(form: UntypedFormGroup): boolean {
    if (!form.get('countryCode').value) {
      return false;
    } else {
      // Checking with helper function to validate postal code using regex
      return this.postalValidation.validatePostcode(form.get('postalCode').value, form.get('countryCode').value);
    }
  }

  triggerPhoneFieldValidation(form: UntypedFormGroup) {
    const phoneNumber: DistNationalNumber = form.get('phone').value;
    const countryIsoCode: DistCountryCode = form.get('countryCode').value;

    this.phoneNumberService
      .isValidNumberForRegion(phoneNumber, countryIsoCode)
      .pipe(
        take(1),
        tap((isValid) => {
          if (isValid) {
            this.markFieldAsValid('phone', form);
          } else {
            this.markFieldAsInvalid('phone', form);
          }

          this.phoneValidationUpdated.emit(true);
        }),
      )
      .subscribe();
  }

  // Method for fetching phone number from "address" OR from "contactAddress" in case "address" DON'T have phone number saved
  setMobileNumber(address?: Address): string {
    let phoneNumber: string;

    // If there is mobile number saved on address, use it
    // Otherwise, use the one from concatAddress set on cart
    if (!!this.setMobileNumberWithoutFallback(address)) {
      phoneNumber = this.setMobileNumberWithoutFallback(address);
    } else {
      if (!this.userService.getUserDetails()?.getValue()?.name) {
        this.userService
          .getUserInformation()
          .pipe(
            tap((data) => {
              phoneNumber = this.getPhoneFromContactAddress(data);
            }),
          )
          .subscribe();
      } else {
        phoneNumber = this.getPhoneFromContactAddress(this.userService.getUserDetails().getValue());
      }
    }

    return phoneNumber;
  }

  // Method for fetching phone number from address ONLY
  setMobileNumberWithoutFallback(address?: Address): string {
    return address?.cellphone ? address?.cellphone : address?.phone ? address?.phone : '';
  }

  getPhoneFromContactAddress(address): string {
    return address?.contactAddress?.cellphone
      ? address?.contactAddress?.cellphone
      : address?.contactAddress?.phone
      ? address?.contactAddress?.phone
      : '';
  }

  submitForgottenPwdCheckout(email): Observable<boolean> {
    const url = this.occEndpointsService.buildUrl('checkout/forgottenpasswordtokens', {
      queryParams: {
        userId: email,
      },
    });

    // We must create the token first before calling forgotten pwd endpoint
    this.clientAuthenticationTokenService.loadClientAuthenticationToken();

    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    headers = InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
    return this.http.post<any>(url, {}, { headers }).pipe(
      take(1),
      switchMap(() => this.oAuthLibWrapperService.revokeAndLogout()),
      catchError(() => of(true)),
      map(() => false),
    );
  }

  setOrderReference(reference: string): Observable<void> {
    return this.userId$.pipe(
      take(1),
      switchMap((userId) =>
        this.http.post<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/setProjectNumber`,
            {
              queryParams: {
                projectNumber: reference,
              },
            },
          ),
          {},
        ),
      ),
      tap(() => this.cartStoreService.updateCartState('projectNumber', reference)),
    );
  }

  handleFormErrorResponseFields(form: UntypedFormGroup, errorSubject, fieldName): void {
    if (errorSubject === fieldName) {
      this.markFieldAsInvalid(fieldName, form);
    }

    if (errorSubject === 'country.isocode') {
      this.markFieldAsInvalid('countryCode', form);
    }

    if (errorSubject === 'cellphone') {
      this.markFieldAsInvalid('phone', form);
    }

    if (errorSubject === fieldName || errorSubject === 'country.isocode' || errorSubject === 'cellphone') {
      this.errorMessage_?.next(null);
    }
  }

  setIsAddressEditClicked(editType: string) {
    this.isAddressEditClicked_.next(editType);
  }

  isAddressEditClicked(): string {
    return this.isAddressEditClicked_.value;
  }

  isScheduledDeliveryDisplayed() {
    return !this.cartStoreService.isCartUserGuest() && !this.cartStoreService.isCartContainsWaldomProduct();
  }
}
