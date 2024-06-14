import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CustomerType } from '@model/site-settings.model';
import { filter, map } from 'rxjs/operators';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { Principal } from '@spartacus/core';

const initialState: Cart = {
  billingAddress: null,
  calculated: false,
  code: '',
  guid: '',
  creditBlocked: false,
  deliveryAddress: null,
  deliveryCost: null,
  deliveryMode: null,
  entries: [],
  paymentInfo: null,
  paymentMode: null,
  subTotal: null,
  totalItems: 0,
  totalPrice: null,
  totalPriceWithTax: null,
  totalTax: null,
  type: 'cartWsDTO',
  user: null,
};

/**
 * Cart data is set to single Behavior Subject which is then can be reused and updated across components without need to call cart
 */
@Injectable({
  providedIn: 'root',
})
export class CartStoreService {
  private readonly store$ = new BehaviorSubject<Cart>(initialState);

  setCartState(cartData: Cart): void {
    this.store$.next(cartData);
  }

  updateCartState(key: string, newValue: any): void {
    this.store$.next({ ...this.store$.getValue(), [key]: newValue });
  }

  // map through each key from cartData object in case it's not returned fully and setCartState can't be used
  updateCartKeys(cartData: Cart) {
    Object.keys(cartData).forEach((cartKey: string) =>
      this.updateCartState(cartKey, cartData[cartKey as keyof typeof cartData]),
    );
  }

  addNewCartEntry(entry) {
    this.updateCartState('entries', [...this.store$.value.entries, entry]);
    this.updateCartState('calculated', false);
  }

  updateCartEntries(entryIndex: number, newEntry: OrderEntry, updateCalculated: boolean = true): void {
    const entries: OrderEntry[] = [...this.store$.value.entries];
    entries.splice(entryIndex, 1, newEntry);

    this.updateCartState('entries', entries);
    if (updateCalculated) {
      this.updateCartState('calculated', false);
    }
  }

  getCartState(): BehaviorSubject<Cart> {
    return this.store$;
  }

  getCartEntries(): OrderEntry[] {
    return this.store$.getValue().entries;
  }

  getCartEntryById(productCode: string): OrderEntry {
    return this.store$.getValue().entries.filter((entry) => entry.product.code === productCode)[0];
  }

  findEntryInCartById(entry) {
    const arrayOfEntriesId: string[] = this.store$.getValue().entries.map((productEntry) => productEntry.product?.code);
    if (arrayOfEntriesId.indexOf(entry.product?.code) >= 0) {
      this.updateCartEntries(arrayOfEntriesId.indexOf(entry.product?.code), entry);
    } else {
      this.addNewCartEntry(entry);
    }
  }

  getCartUser(): Principal {
    return this.store$.getValue().user;
  }

  isCartUserB2BKey(): boolean {
    return this.store$.value.user?.type === CustomerType.B2B_KEY_ACCOUNT;
  }

  isCartUserB2B(): boolean {
    return (
      this.store$.value.user?.type === CustomerType.B2B || this.store$.value.user?.type === CustomerType.B2B_KEY_ACCOUNT
    );
  }

  isCartUserGuest(): boolean {
    return this.store$.value.user?.type === CustomerType.GUEST;
  }

  isCartUserB2E(): boolean {
    return this.store$.value.user?.type === CustomerType.B2E;
  }

  isCartContainsWaldomProduct(): boolean {
    return this.store$.value?.waldom || false;
  }

  getCartId(): string {
    return !this.store$.value.user ||
      this.store$.value.user?.uid === 'anonymous' ||
      this.store$.value.user?.type === CustomerType.GUEST
      ? this.store$.value.guid
      : this.store$.value.code;
  }

  getCartIdAsObservable(): Observable<string> {
    return this.store$.asObservable().pipe(
      map((cart) =>
        !cart.user || cart.user?.uid === 'anonymous' || cart.user?.type === CustomerType.GUEST ? cart.guid : cart.code,
      ),
      filter((cartId) => cartId.length > 0),
    );
  }

  getCartGuid(): string {
    return this.store$.value.guid;
  }

  resetCart(): void {
    this.store$.next(initialState);
  }
}
