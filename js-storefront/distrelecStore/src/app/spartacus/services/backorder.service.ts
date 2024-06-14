import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService, UserIdService } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { DistCartService } from './cart.service';
import { first, map, switchMap, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LocalStorageService } from './local-storage.service';
import { CartStoreService } from '@state/cartState.service';
import { AppendComponentService } from './append-component.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { OrderEntryList } from '@model/order.model';

@Injectable({
  providedIn: 'root',
})
export class BackorderService {
  backorderEntries_: BehaviorSubject<OrderEntry[]> = new BehaviorSubject<OrderEntry[]>([]);

  url: string = this.occEndpointsService.buildUrl('/users', {});
  cartId;

  userId$ = this.userIdService.getUserId();

  constructor(
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private userIdService: UserIdService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    protected router: Router,
    private localStorage: LocalStorageService,
    private appendComponentService: AppendComponentService,
  ) {}

  getBackorderItems(): Observable<OrderEntryList> {
    // If user is redirected here because during order placement they had blocked products
    // Display the page with those products alone
    if (this.localStorage.getItem('purchaseBlockedProducts')?.length > 0) {
      return this.populateBlockedProductsData();
    } else {
      return combineLatest([this.userId$, this.cartService.returnBackorderCartData()]).pipe(
        first(),
        switchMap(([userId]) =>
          this.http.get<{ orderEntries: OrderEntry[] }>(
            this.occEndpointsService.buildUrl(
              `users/${userId}/carts/${this.cartStoreService.getCartId()}/unallowed-backorder-entries`,
            ),
          ),
        ),
      );
    }
  }

  populateBlockedProductsData(): Observable<{ orderEntries: OrderEntry[] }> {
    const entriesData = [];
    // in case if user placed an order via paypal and they had purchaseBlockedProducts error,
    // then the cart state will be empty and therefore cart needs to be called again
    return this.cartService.returnBackorderCartData().pipe(
      first(),
      map((cartData: Cart) => {
        this.cartStoreService.setCartState(cartData);
        const productCodes = cartData.entries.map((entry) => entry.product.code);
        this.localStorage.getItem('purchaseBlockedProducts')?.forEach((entry) => {
          if (productCodes.indexOf(entry) >= 0) {
            entriesData.push(cartData.entries[productCodes.indexOf(entry)]);
          }
        });
        return { orderEntries: entriesData };
      }),
    );
  }

  getAlternativeItems(productCode: string, quantity: number): Observable<any> {
    return this.http.get<any>(
      this.occEndpointsService.buildUrl(`backorder/alternate-products/${productCode}?quantity=${quantity}`),
    );
  }

  saveBackorderChangesWithAlternative(replacementProducts): void {
    this.appendComponentService.startScreenLoading();
    this.cartService
      .addBulkProducts(replacementProducts, false, ItemListEntity.BACKORDER)
      .pipe(
        first(),
        switchMap(() => this.updateBackorderItems()),
        tap((cartData) => {
          this.cartService.populateCartData(cartData);
          this.handleCheckoutRedirection(cartData);
        }),
      )
      .subscribe();
  }

  saveBackorderChangesWithoutAlternative(): void {
    this.appendComponentService.startScreenLoading();
    this.updateBackorderItems()
      .pipe(
        tap((cartData: Cart) => {
          this.cartService.populateCartData(cartData);
          this.handleCheckoutRedirection(cartData);
        }),
      )
      .subscribe();
  }

  handleCheckoutRedirection(cartData: Cart) {
    this.appendComponentService.stopScreenLoading();
    if (cartData.entries.length > 0 && cartData.eligibleForFastCheckout) {
      this.localStorage.removeItem('purchaseBlockedProducts');
      this.router.navigate(['checkout/review-and-pay']);
    } else if (cartData.entries.length > 0 && !cartData.eligibleForFastCheckout) {
      this.localStorage.removeItem('purchaseBlockedProducts');
      this.router.navigate(['checkout/delivery']);
    } else {
      this.router.navigate(['cart']);
    }
  }

  updateBackorderItems() {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.http.post<Cart>(
          this.occEndpointsService.buildUrl(
            `users/${userId}/carts/${this.cartStoreService.getCartId()}/update-backorder`,
          ),
          {},
        ),
      ),
    );
  }

  notifyMe(email, productCodes) {
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.http.post<any>(
          this.occEndpointsService.buildUrl(`users/${userId}/carts/${this.cartStoreService.getCartId()}/zeroStock`),
          {
            articleNumbers: productCodes,
            customerEmail: email,
          },
        ),
      ),
    );
  }
}
