import { CxEvent } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';

export class CartViewEvent extends CxEvent {
  cart: Cart;
}
