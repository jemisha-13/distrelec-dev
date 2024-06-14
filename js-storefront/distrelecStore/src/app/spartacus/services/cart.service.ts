import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { CartStateListener } from '@helpers/cart-state-listener';
import { ObservableQueue } from '@helpers/observable-queue';
import { isCartNotFoundError, MultiCartStatePersistenceService } from '@spartacus/cart/base/core';
import {
  ActiveCartFacade,
  Cart,
  CartAddEntrySuccessEvent,
  CartModification,
  CartRemoveEntrySuccessEvent,
  MultiCartFacade,
  OrderEntry,
} from '@spartacus/cart/base/root';
import {
  AuthStorageService,
  BaseSiteService,
  createFrom,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  UserIdService,
  WindowRef,
} from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  filter,
  first,
  map,
  pluck,
  switchMap,
  take,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { CountryCodesEnum } from '../site-context/services/country.service';
import { AppendComponentService } from './append-component.service';
import { DistCartHttpService } from './cart-http.service';
import { LocalStorageService } from './local-storage.service';
import { BloomreachUpdateCartTracking } from '@features/tracking/bloomreach/bloomreach-update-cart.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { CartComponentService } from '@features/pages/cart/cart-component.service';
import {
  AddBulkResponse,
  AddToCartQuotationRequest,
  BulkProductData,
  RequestQuote,
  ShareCartFormData,
} from '@model/cart.model';

export const recentlyAddedLimit = 100;

@Injectable({
  providedIn: 'root',
})
export class DistCartService {
  activeSiteId: string;
  // current or anonymous
  userId$: Observable<string> = this.userIdService.getUserId();

  // We need this flag when the user deletes the cart completely
  renderRecommendations$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  // When user adds product, display a pop up in the header with quantity added
  productsRecentlyAdded_: BehaviorSubject<OrderEntry[]> = new BehaviorSubject<OrderEntry[]>(null);
  phasedOutProducts_: BehaviorSubject<string> = new BehaviorSubject<string>('');
  isCartLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isRecalculateInProgress_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isQuantityChanged_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  // Boolean to display the recalculate cart option
  updateCart_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  areProductAvailabilitiesLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  private addToCartQueue = new ObservableQueue<Cart>();

  constructor(
    private activeCartService: ActiveCartFacade,
    private appendComponentService: AppendComponentService,
    protected authStorageService: AuthStorageService,
    private baseSiteService: BaseSiteService,
    private cartStoreService: CartStoreService,
    private cartStateListener: CartStateListener,
    private cartComponentService: CartComponentService,
    private cartHttpService: DistCartHttpService,
    private eventService: EventService,
    private bloomreachUpdateCartTracking: BloomreachUpdateCartTracking,
    private userIdService: UserIdService,
    private multiCartService: MultiCartFacade,
    private localStorage: LocalStorageService,
    private productAvailabilityService: ProductAvailabilityService,
    private priceService: PriceService,
    private winRef: WindowRef,
    private multiCartStatePersistenceService: MultiCartStatePersistenceService,
    private globalMessageService: GlobalMessageService,
  ) {
    this.cartStateListener.listenToCartChanges();
  }

  // Do not use this if you require the cart to be calculated
  getStableCart(): Observable<Cart> {
    // return only active cart for guest because cart is always set as unstable
    if (this.authStorageService.getItem('guest')) {
      return this.activeCartService.getActive();
    } else {
      return combineLatest([
        this.activeCartService.getActive(),
        this.activeCartService.getLoading(),
        this.activeCartService.isStable(),
      ]).pipe(
        filter(([cartData, isLoading]) => Array.isArray(cartData.entries) && !isLoading),
        map(([stableLoadedCart]) => stableLoadedCart),
      );
    }
  }

  getCartDataFromStore(): Observable<Cart> {
    return this.cartStoreService.getCartState().asObservable();
  }

  handleCartNotFound(error: HttpErrorResponse) {
    const notFoundError = this.getErrors(error)?.find((err) => isCartNotFoundError(err));
    if (notFoundError) {
      return this.updateActiveCartFromDB();
    }
    return throwError(error);
  }

  getErrors(response: HttpErrorResponse): any[] {
    return response.error?.errors;
  }

  updateActiveCartFromDB(): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.getHttpCarts(userId)),
      pluck('carts'),
      switchMap((carts: Cart[]) => {
        if (Array.isArray(carts) && carts.length > 0) {
          const firstCart = carts[0];
          this.cartStoreService.setCartState(firstCart);
          this.setCartIdToLocalStorage(firstCart.code);
          return of(firstCart);
        }
        return this.createNewCart();
      }),
    );
  }

  returnBackorderCartData(): Observable<Cart> {
    const cartToken: string = this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.getHttpBackorderCart(userId, cartToken)),
    );
  }

  returnCartDataFromDB(cartId?: string): Observable<Cart> {
    const cartToken: string = cartId ? cartId : this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.getHttpCart(userId, cartToken)),
      tap((cartData: Cart) => {
        this.cartStoreService.setCartState(cartData);
      }),
    );
  }

  assignCartEntries(): Observable<Cart> {
    return this.userId$.pipe(
      distinctUntilChanged(),
      switchMap((userId) =>
        this.getCartTokenFromLocalStorage().pipe(
          switchMap((cartToken) => {
            // do not make get cart call if user has logged in and the carts are still merging
            // do not make call to get cart if user has just logged out and storage has not been updated
            if (
              (userId === 'current' && (cartToken?.length > 12 || cartToken?.includes('temp'))) ||
              (userId === 'anonymous' && cartToken?.length < 12) ||
              !cartToken
            ) {
              this.areProductAvailabilitiesLoading_.next(false);
              return this.cartStoreService.getCartState().asObservable();
            } else {
              return this.returnCartDataFromDB(cartToken).pipe(
                filter((data) => Array.isArray(data.entries)),
                switchMap((cartData) => {
                  // Check if the cart has expired, else populate cart data
                  if (this.cartStoreService.getCartEntries().length > 0) {
                    return this.getProductsAvailability(cartData);
                  }
                  this.areProductAvailabilitiesLoading_.next(false);
                  return of(this.cartStoreService.getCartState().value);
                }),
                catchError((error) => this.handleGetCartError(error.error?.errors[0])),
              );
            }
          }),
        ),
      ),
    );
  }

  handleGetCartError(error) {
    if (error?.reason === 'expired') {
      this.setCartIdToLocalStorage(error?.subject);
      return this.returnCartDataFromDB(error?.subject);
    } else {
      this.areProductAvailabilitiesLoading_.next(false);
      this.removeCartToken();
      throw error;
    }
  }

  // When user adds a product to the cart, first is checked whether the GUID number has been created
  // And the guid_expiration cookie is present
  // If cookie is not present, but the GUID is - it is removed and a new call to create the news GUID is made
  // Then the product is added to the cart under the new GUID
  addProductToCart(
    productCode: string,
    quantity: number,
    isCart: boolean,
    itemListEntity: ItemListEntity,
    cartForm?: UntypedFormGroup,
  ): Observable<Cart> {
    this.isQuantityChanged_.next(true);
    this.updateCartQuickOrderInput(cartForm);

    const formatProductNumber = productCode.replace(/-/g, '');
    const addToCartObservable = this.getCartTokenFromLocalStorage().pipe(
      first(),
      switchMap((cartToken: string) => {
        if (cartToken) {
          return this.addProduct(formatProductNumber, quantity, isCart, itemListEntity, cartToken);
        } else {
          return this.createNewCart().pipe(
            switchMap(() => this.addProduct(formatProductNumber, quantity, isCart, itemListEntity)),
          );
        }
      }),
      tap(() => {
        this.setCartExpirationToken();
      }),
      first(),
    );

    return this.addToCartQueue.queue(addToCartObservable);
  }

  createNewCart(): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      switchMap((user: string) =>
        this.cartHttpService.postHttpCarts(user).pipe(
          tap((newCartData: Cart) => {
            this.cartStoreService.setCartState(newCartData);
            this.setCartIdToLocalStorage();
          }),
        ),
      ),
    );
  }

  getCartTokenFromLocalStorage(): Observable<string> {
    if (!this.activeSiteId) {
      return this.baseSiteService.getActive().pipe(
        map((siteActive) => {
          this.activeSiteId = siteActive;
          // update spartacus local storage value to persist cart on page load
          return this.localStorage.getItem(`spartacus⚿${this.activeSiteId}⚿cart`)?.active;
        }),
      );
    } else {
      return of(this.localStorage.getItem(`spartacus⚿${this.activeSiteId}⚿cart`)?.active);
    }
  }

  setCartIdToLocalStorage(cartId?: string) {
    this.localStorage.setItem(`spartacus⚿${this.activeSiteId}⚿cart`, {
      active: cartId ? cartId : this.cartStoreService.getCartId(),
    });
  }

  setCartExpirationToken(): Observable<string> {
    return this.userId$.pipe(
      first(),
      tap((userId) => {
        if (!this.cartStoreService.getCartEntries() && userId === 'anonymous') {
          const date = new Date();
          date.setHours(date.getHours() + 1);
          this.authStorageService.setItem('expires_at', '' + date);
        }
      }),
    );
  }

  addProduct(
    entryCode: string,
    quantity: number,
    isCart: boolean,
    itemListEntity: ItemListEntity,
    cartId?: string,
  ): Observable<Cart> {
    cartId = cartId ? cartId : this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.postHttpAddEntry(userId, cartId, entryCode, quantity, itemListEntity)),
      catchError((error) => this.tryUpdateCartFromDbOnCartError(error, entryCode, quantity, isCart, itemListEntity)),
      filter(Boolean),
      tap((cartData: CartModification) => {
        if (
          cartData.statusCode &&
          cartData.statusCode !== 'success' &&
          cartData.statusCode !== 'moqAdjusted' &&
          cartData.statusCode !== 'stepAdjusted'
        ) {
          this.handleInvalidStatusCodePopup(cartData.statusCode);
        }
        if (cartData.entry) {
          cartData.entry.moqAdjusted = cartData.statusCode === 'moqAdjusted';
          cartData.entry.stepAdjusted = cartData.statusCode === 'stepAdjusted';
          this.updateCart_.next(true);
          this.cartStoreService.findEntryInCartById(cartData.entry);
          this.updateRecentlyAddedProducts([cartData.entry]);
          this.dispatchAddEntryEvent(cartData.entry, cartData.quantityAdded);
          this.bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', cartData.entry);
        }
      }),
      switchMap((cartData) => {
        let cartState$: Observable<Cart>;

        if (isCart) {
          cartState$ = this.getProductsAvailability(this.cartStoreService.getCartState().value);
        } else {
          cartState$ = this.cartStoreService.getCartState().asObservable();
        }

        return combineLatest([cartState$, of(cartData.statusCode)]).pipe(
          map(([cartState, statusCode]) => ({ ...cartState, statusCode })),
        );
      }),
    );
  }

  tryUpdateCartFromDbOnCartError(
    error: HttpErrorResponse,
    entryCode: string,
    quantity: number,
    isCart: boolean,
    itemListEntity: ItemListEntity,
  ): Observable<Cart> {
    return this.handleCartNotFound(error).pipe(
      switchMap((newCart: Cart) => this.addProduct(entryCode, quantity, isCart, itemListEntity, newCart.code)),
    );
  }

  handleInvalidStatusCodePopup(status: string): void {
    switch (status) {
      case 'noStock': {
        this.appendComponentService.appendWarningPopup(null, null, 'cart.nonstock.phaseout.product', null, 'error');
        break;
      }
      case 'lowStock': {
        this.appendComponentService.appendWarningPopup(
          null,
          null,
          null,
          'basket.information.quantity.reducedNumberOfItemsAdded.lowStock',
          'warning',
        );
        break;
      }
    }
  }

  public updateRecentlyAddedProducts(items: OrderEntry[]): void {
    this.productsRecentlyAdded_.next(items.slice(0, recentlyAddedLimit));
  }

  // Add products in bulk into the cart. specially for order detail page > reorder
  addBulkProductsToCart(productEntries, itemListEntity: ItemListEntity): Observable<any> {
    const cartId = this.cartStoreService.getCartId();
    this.isQuantityChanged_.next(true);

    if (cartId) {
      // If cartId already exists, we add product and get the entries again
      return this.addBulkProducts(productEntries, true, itemListEntity);
    } else {
      // If the user cart has not been yet created
      return this.createNewCart().pipe(
        first(),
        withLatestFrom(this.userId$),
        tap(([cartData, userId]) => {
          this.cartStoreService.setCartState(cartData);
          // We set this cartId to retrieve the expiration date and check
          // If the cart is valid on each cart entry action
          // If the user is not logged in, we create the expiration time
          if (userId === 'anonymous') {
            const date = new Date();
            date.setHours(date.getHours() + 1);
            this.authStorageService.setItem('expires_at', '' + date);
          }
        }),
        switchMap(() => this.addBulkProducts(productEntries, true, itemListEntity)),
      );
    }
  }

  addBulkProducts(
    productEntries: any[],
    isUpdateCartState: boolean,
    itemListEntity: ItemListEntity,
  ): Observable<AddBulkResponse> {
    const products = [];
    productEntries.forEach((entry) => {
      const data: BulkProductData = {
        itemNumber: entry.itemNumber ?? entry.entryNumber ?? '',
        productCode: entry.productCode ?? entry.product.code,
        quantity: entry.quantity,
        reference: entry.reference ?? '',
      };
      products.push(data);
    });

    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService.postHttpAddBulk(userId, this.cartStoreService.getCartId(), products, itemListEntity).pipe(
          first(),
          map((bulkResponse: AddBulkResponse) => {
            this.updateCart_.next(true);
            if (isUpdateCartState) {
              bulkResponse.cartModifications.forEach((entry: CartModification) => {
                this.cartStoreService.findEntryInCartById(entry.entry);
                this.dispatchAddEntryEvent(entry.entry, entry.entry.quantity);
                this.bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', entry.entry);
              });
              this.multiCartStatePersistenceService.initSync();

              if (bulkResponse.punchOutProducts.length) {
                const punchOutProductKeys: string = bulkResponse.punchOutProducts
                  .map((product) => product.code)
                  .join(', ');
                this.cartComponentService.showPunchOutErrorGlobalMessage(punchOutProductKeys);
              }
              this.updateRecentlyAddedProducts(
                bulkResponse.cartModifications.map((value: CartModification) => value.entry),
              );

              if (bulkResponse.phaseOutProducts.length > 0) {
                this.bulkAddWarningMessage(bulkResponse.phaseOutProducts);
              }

              if (bulkResponse.blockedProducts.length > 0) {
                this.showBlockedProductCodes(bulkResponse.blockedProducts);
              }
            }

            return bulkResponse;
          }),
        ),
      ),
    );
  }

  // Add products in bulk into the cart. specially for order detail page > reorder
  addQuotationToCart(quoteRequest: AddToCartQuotationRequest): Observable<Cart> {
    return this.getCartTokenFromLocalStorage().pipe(
      first(),
      switchMap((token) => {
        if (token) {
          // If cartId already exists, we add product and get the entries again
          return this.addQuotationProductsToCart(quoteRequest, token);
        } else {
          // If the user cart has not been yet created
          return this.createNewCart().pipe(
            first(),
            withLatestFrom(this.userId$),
            tap(([cartData, userId]) => {
              this.cartStoreService.setCartState(cartData);
              // We set this cartId to retrieve the expiration date and check
              // If the cart is valid on each cart entry action
              // If the user is not logged in, we create the expiration time
              if (userId === 'anonymous') {
                const date = new Date();
                date.setHours(date.getHours() + 1);
                this.authStorageService.setItem('expires_at', '' + date);
              }
            }),
            switchMap(() => this.addQuotationProductsToCart(quoteRequest)),
          );
        }
      }),
    );
  }

  addQuotationProductsToCart(quoteRequest: AddToCartQuotationRequest, cartCode?: string): Observable<Cart> {
    const cartId = cartCode ? cartCode : this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService.postHttpAddQuote(userId, cartId, quoteRequest).pipe(
          tap((quoteResponse) => {
            this.cartStoreService.setCartState(quoteResponse);
          }),
        ),
      ),
    );
  }

  removeQuotationFromCart(quotationId: string): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService.postHttpRemoveQuotation(userId, this.cartStoreService.getCartId(), quotationId).pipe(
          tap((cart) => {
            this.cartStoreService.updateCartKeys(cart);
            this.updateCart_.next(true);
          }),
        ),
      ),
    );
  }

  recalculateCartEntries(userId: string): Observable<Cart> {
    this.isRecalculateInProgress_.next(true);
    const cartCode = this.cartStoreService.getCartId();
    return this.cartHttpService.patchHttpRecalculateCart(userId, cartCode).pipe(
      map((data) => {
        if (data.calculated) {
          this.updateCart_.next(false);
        }
        this.cartStoreService.setCartState(data);
        return data;
      }),
    );
  }

  removeCartToken(): void {
    this.getCartTokenFromLocalStorage()
      .pipe(
        tap(() => {
          // update spartacus local storage value to persist cart on page load
          this.localStorage.setItem(`spartacus⚿${this.activeSiteId}⚿cart`, { active: '' });
        }),
      )
      .subscribe();
  }

  populateCartData(cartData: Cart): void {
    const cartEntries: OrderEntry[] = this.cartStoreService.getCartEntries();
    this.cartStoreService.setCartState(cartData);
  }

  removeProductFromCart(entryNumber: number, position: number, entry: OrderEntry): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService.deleteHttpProduct(userId, this.cartStoreService.getCartId(), entryNumber).pipe(
          tap((cartData: Cart) => {
            this.updateCart_.next(true);
            this.cartStoreService.updateCartKeys(cartData);
            this.dispatchDeleteEntryEvent(entry);
            this.bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('remove', entry);
          }),
        ),
      ),
      switchMap(() => this.getProductsAvailability(this.cartStoreService.getCartState().value)),
    );
  }

  deleteCart(): Observable<string> {
    return this.userId$.pipe(
      first(),
      tap((userId) => {
        this.multiCartService.deleteCart(this.cartStoreService.getCartId(), userId);
        this.revokeCartEntries();
        if (this.winRef.isBrowser()) {
          this.winRef.nativeWindow.scrollTo(0, 0);
        }
        this.bloomreachUpdateCartTracking.trackEmptyProductCartEvent();
      }),
    );
  }

  emptyCart(entry?: OrderEntry): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      tap(() => {
        if (!entry) {
          this.cartStoreService.getCartEntries().forEach((entryItem) => {
            this.dispatchDeleteEntryEvent(entryItem);
          });
        }
      }),
      switchMap((userId) =>
        this.cartHttpService.postHttpEmptyCart(userId, this.cartStoreService.getCartId()).pipe(
          tap((data) => {
            if (entry) {
              this.dispatchDeleteEntryEvent(entry);
              this.bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('remove', entry);
            }
            this.cartStoreService.setCartState(data);
            this.localStorage.removeItem('purchaseBlockedProducts');
            if (this.winRef.isBrowser()) {
              this.winRef.nativeWindow.scrollTo(0, 0);
            }
            this.bloomreachUpdateCartTracking.trackEmptyProductCartEvent();
          }),
        ),
      ),
    );
  }

  revokeCartEntries() {
    this.removeCartToken();
    if (this.authStorageService.getItem('expires_at')) {
      this.authStorageService.removeItem('expires_at');
    }
    this.cartStoreService.resetCart();
    this.multiCartStatePersistenceService.initSync();
  }

  updateCartQuantity(quantity: number, entryNumber: number): Observable<CartModification> {
    const cartId = this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService.patchHttpUpdateQuantity(userId, cartId, entryNumber, quantity).pipe(
          tap((response: CartModification) => {
            this.cartStoreService.updateCartEntries(entryNumber, response.entry);
            this.bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', response.entry);
            this.updateCart_.next(true);
          }),
        ),
      ),
    );
  }

  updateCartQuickOrderInput(cartForm: UntypedFormGroup) {
    if (cartForm) {
      cartForm.get('orderInput').patchValue('');
    }
  }

  validateVoucher(voucherCode: string): Observable<Cart | null> {
    const cartId = this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.postHttpValidateVoucher(userId, cartId, voucherCode)),
    );
  }

  removeVoucher(voucherCode): Observable<Cart> {
    const cartId = this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.deleteHttpVoucher(userId, cartId, voucherCode)),
    );
  }

  pdfGenerationLink(): Observable<string> {
    const cartGuid = this.cartStoreService.getCartGuid();
    return this.cartHttpService.getHttpPdfLink(cartGuid);
  }

  requestQuotation(): Observable<RequestQuote> {
    const cartId = this.cartStoreService.getCartId();
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.postHttpRequestQuote(userId, cartId)),
    );
  }

  sendCartViaEmail(form: ShareCartFormData): Observable<void> {
    return this.userId$.pipe(
      first(),
      switchMap((userId) => this.cartHttpService.postHttpSendToFriend(userId, this.cartStoreService.getCartId(), form)),
    );
  }

  calibrateProduct(sourceCode: string, targetCode: string, entryNumber: number, quantity: string) {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService
          .putHttpCalibrateProduct(
            userId,
            this.cartStoreService.getCartId(),
            entryNumber,
            quantity,
            sourceCode,
            targetCode,
          )
          .pipe(tap((data: Cart) => this.cartStoreService.updateCartKeys(data))),
      ),
    );
  }

  setReevooEligible(value: boolean): Observable<Cart> {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService
          .postHttpSetReevoo(userId, this.cartStoreService.getCartId(), value)
          .pipe(tap((data: Cart) => this.cartStoreService.updateCartKeys(data))),
      ),
    );
  }

  updateEntryReference(entryNumber: number, customerReference: string) {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.cartHttpService
          .postHttpUpdateReference(userId, this.cartStoreService.getCartId(), customerReference, entryNumber)
          .pipe(
            tap((response: CartModification) => {
              this.cartStoreService.updateCartEntries(entryNumber, response.entry, false);
            }),
          ),
      ),
    );
  }

  getProductsAvailability(cartData): Observable<Cart> {
    const productsCodesArray = cartData?.entries?.map((entry) => entry.product.code);

    return this.productAvailabilityService.getProductAvailabilities(productsCodesArray).pipe(
      map(() => {
        this.areProductAvailabilitiesLoading_.next(false);
        return cartData;
      }),
    );
  }

  isDangerousGoodMessageDisplay(): boolean {
    const siteId = this.activeSiteId.split('_')[1];
    return (
      siteId !== CountryCodesEnum.SWITZERLAND &&
      siteId !== CountryCodesEnum.GERMANY &&
      siteId !== CountryCodesEnum.NETHERLANDS &&
      siteId !== CountryCodesEnum.BELGIUM
    );
  }

  isBelowMinimumOrderValue(cartData: Cart): boolean {
    return cartData.movLimit > cartData.subTotal.value;
  }

  haveCartEntries(cart: Cart): boolean {
    return !!cart.entries?.length;
  }

  isCalculationFailed(cart: Cart): boolean {
    return cart.calculationFailed;
  }

  isCustomerBlockedInErp(cart: Cart): boolean {
    return cart.customerBlockedInErp;
  }

  isProductCodeMisalignment(cart: Cart): boolean {
    return !!cart.productCodeMisalignment;
  }

  havePunchedOutProducts(cart: Cart): boolean {
    return !!cart.punchedOutProducts;
  }

  haveEndOfLifeProducts(cart: Cart): boolean {
    return !!cart.endOfLifeProducts?.length;
  }

  havePhasedOutProducts(cart: Cart): boolean {
    return !!cart.phasedOutProducts?.length;
  }

  haveMOQ(cart: Cart): boolean {
    return !!cart.moq;
  }

  hasUnallowedBackorder(cart: Cart): boolean {
    return cart.hasUnallowedBackorder;
  }

  isNotAllowedToEnterCheckout(cart: Cart, isAddToCartDisabled: boolean): boolean {
    return (
      isAddToCartDisabled ||
      !this.haveCartEntries(cart) ||
      this.isCalculationFailed(cart) ||
      this.isCustomerBlockedInErp(cart) ||
      this.isProductCodeMisalignment(cart) ||
      this.havePunchedOutProducts(cart) ||
      this.haveEndOfLifeProducts(cart) ||
      this.havePhasedOutProducts(cart)
    );
  }

  private dispatchAddEntryEvent(entry: OrderEntry, quantity: number) {
    this.loadPrices(entry.product.code)
      .pipe(take(1))
      .subscribe((prices) => {
        entry.basePrice.currencyIso = this.priceService.getCurrencyFromPrice(prices.volumePricesMap);
        entry.basePrice.value = this.priceService.getPriceForQuantity(prices.volumePricesMap, entry.quantity, 'B2B');
        entry.totalPrice.value = entry.basePrice.value;

        this.eventService.dispatch(
          createFrom(CartAddEntrySuccessEvent, {
            entry,
            quantity,
          } as CartAddEntrySuccessEvent),
        );
      });
  }

  private dispatchDeleteEntryEvent(entry: OrderEntry): void {
    this.eventService.dispatch(
      createFrom(CartRemoveEntrySuccessEvent, {
        entry,
      } as CartRemoveEntrySuccessEvent),
    );
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }

  private bulkAddWarningMessage(products): void {
    const productCodes = products.map((product) => product.code);

    if (productCodes.length > 0) {
      this.phasedOutProducts_.next('(' + productCodes.join(', ') + ')');
    }
  }

  private showBlockedProductCodes(blockedProducts) {
    const productCodes: string[] = blockedProducts.map((entry) => entry.code);
    this.globalMessageService.add(
      {
        key: 'cart.punchout_error',
        params: { code: productCodes.join(', ') },
      },
      GlobalMessageType.MSG_TYPE_ERROR,
    );
  }
}
