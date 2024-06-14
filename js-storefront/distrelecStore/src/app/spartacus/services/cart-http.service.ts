import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LanguageService, OccEndpointsService, StateUtils } from '@spartacus/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CartActions, getCartIdByUserId, MULTI_CART_DATA, StateWithMultiCart } from '@spartacus/cart/base/core';
import { Store } from '@ngrx/store';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { AddBulkResponse, AddToCartQuotationRequest, RequestQuote, ShareCartFormData } from '@model/cart.model';
import { Cart, CartModification } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export class DistCartHttpService {
  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private languageService: LanguageService,
    private store: Store<StateWithMultiCart>,
  ) {}

  getHttpMiniCart(userId: string, cartToken: string): Observable<Cart> {
    return this.http.get<Cart>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartToken}/mini`, {}), {});
  }

  getHttpBackorderCart(userId: string, cartToken: string): Observable<Cart> {
    return this.http.get<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartToken}`, {
        queryParams: {
          removeUnavailable: false,
        },
      }),
    );
  }

  getHttpCart(userId: string, cartToken: string): Observable<Cart> {
    return this.http.get<Cart>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartToken}`)).pipe(
      tap((cart) => {
        this.store.dispatch(
          new CartActions.LoadCartSuccess({
            userId,
            cart,
            cartId: getCartIdByUserId(cart, userId),
          }),
        );
      }),
    );
  }

  getHttpCarts(userId: string): Observable<any> {
    return this.http.get<any>(this.occEndpointsService.buildUrl(`/users/${userId}/carts`));
  }

  postHttpCarts(userId: string): Observable<Cart> {
    return this.http.post<Cart>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/`), {}).pipe(
      tap((cart: Cart) => {
        this.store.dispatch(new CartActions.SetActiveCartId(userId === 'current' ? cart.code : cart.guid));
      }),
    );
  }

  postHttpMergeCarts(userId: string, oldCartId: string, toMergeCartGuid: string): Observable<Cart> {
    return this.http.post<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts`, {
        queryParams: { oldCartId, toMergeCartGuid },
      }),
      {},
    );
  }

  postHttpAddEntry(
    userId: string,
    cartId: string,
    entryCode: string,
    quantity: number,
    itemListEntity: ItemListEntity,
  ): Observable<CartModification> {
    this.store.dispatch(new StateUtils.EntityProcessesIncrementAction(MULTI_CART_DATA, cartId));

    return this.http
      .post<CartModification>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartId}/entries`), {
        product: { code: entryCode },
        quantity,
        addedFrom: itemListEntity,
      })
      .pipe(
        tap((cartModification: CartModification) =>
          this.store.dispatch(
            new CartActions.CartAddEntrySuccess({
              userId,
              cartId,
              productCode: entryCode,
              quantity,
              ...JSON.parse(JSON.stringify(cartModification)), // Clone it or it can't be mutated later (we add moq properties)
            }),
          ),
        ),
      );
  }

  postHttpAddBulk(
    userId: string,
    cartId: string,
    products,
    itemListEntity: ItemListEntity,
  ): Observable<AddBulkResponse> {
    return this.http.post<AddBulkResponse>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartId}/bulk`, {}),
      {
        products,
        addedFrom: itemListEntity,
      },
    );
  }

  postHttpAddQuote(userId: string, cartId: string, quoteRequest: AddToCartQuotationRequest): Observable<Cart> {
    return this.http.post<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartId}/add-quotation`),
      quoteRequest,
    );
  }

  patchHttpRecalculateCart(userId: string, cartCode: string): Observable<Cart> {
    return this.http.patch<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/recalculate`),
      {},
    );
  }

  deleteHttpProduct(userId: string, cartCode: string, index: number): Observable<Cart> {
    this.store.dispatch(new StateUtils.EntityProcessesIncrementAction(MULTI_CART_DATA, cartCode));

    return this.http
      .delete<Cart>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/entries/${index}`))
      .pipe(
        tap(() =>
          this.store.dispatch(
            new CartActions.CartRemoveEntrySuccess({
              userId,
              cartId: cartCode,
              entryNumber: index.toString(),
            }),
          ),
        ),
      );
  }

  postHttpEmptyCart(userId: string, cartCode: string): Observable<Cart> {
    return this.http
      .post<Cart>(this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/emptyCart`), {})
      .pipe(
        tap((cart) =>
          this.store.dispatch(
            new CartActions.LoadCartSuccess({
              userId,
              cart,
              cartId: getCartIdByUserId(cart, userId),
            }),
          ),
        ),
      );
  }

  patchHttpUpdateQuantity(
    userId: string,
    cartCode: string,
    entryNumber: number,
    quantity: number,
  ): Observable<CartModification> {
    return this.http.patch<CartModification>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/entries/${entryNumber}`, {
        queryParams: { quantity },
      }),
      {},
    );
  }

  postHttpValidateVoucher(userId: string, cartCode: string, voucherId: string): Observable<Cart | null> {
    return this.http.post<Cart | null>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/vouchers`, { queryParams: { voucherId } }),
      {},
    );
  }

  deleteHttpVoucher(userId: string, cartCode: string, voucherId: string): Observable<Cart> {
    return this.http.delete<Cart>(
      this.occEndpointsService.buildUrl(`users/${userId}/carts/${cartCode}/vouchers/${voucherId}`, {}),
      {},
    );
  }

  getHttpPdfLink(cartGuid: string): Observable<string> {
    return this.languageService.getActive().pipe(map((language) => `/cart/pdf?cartGuid=${cartGuid}&lang=${language}`));
  }

  postHttpRequestQuote(userId: string, cartCode: string): Observable<RequestQuote> {
    return this.http.post<RequestQuote>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/requestQuote`),
      {},
    );
  }

  postHttpSendToFriend(userId: string, cartCode: string, form: ShareCartFormData): Observable<void> {
    return this.http.post<void>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/sendToFriend`),
      form,
    );
  }

  putHttpCalibrateProduct(
    userId: string,
    cartCode: string,
    entryNumber: number,
    newQty: string,
    source: string,
    target: string,
  ): Observable<Cart> {
    return this.http.put<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/entries/${entryNumber}`),
      {
        newQty,
        source,
        target,
      },
    );
  }

  postHttpSetReevoo(userId: string, cartCode: string, value: boolean): Observable<Cart> {
    return this.http.post<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/reevoo`, {
        queryParams: { reevooEligible: value },
      }),
      {},
    );
  }

  postHttpUpdateReference(
    userId: string,
    cartCode: string,
    customerReference,
    entryNumber,
  ): Observable<CartModification> {
    return this.http.post<CartModification>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/update/reference`, {
        queryParams: { customerReference, entryNumber },
      }),
      {},
    );
  }

  postHttpRemoveQuotation(userId: string, cartCode: string, quotationId: string): Observable<Cart> {
    return this.http.delete<Cart>(
      this.occEndpointsService.buildUrl(`/users/${userId}/carts/${cartCode}/quotation/${quotationId}`),
    );
  }
}
