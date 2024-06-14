import { CxEvent } from '@spartacus/core';
import { Order } from '@spartacus/order/root';

export class PurchaseEvent extends CxEvent {
  orderData: Order;
  isFastCheckout?: boolean;
}
