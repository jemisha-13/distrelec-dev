import { CxEvent } from '@spartacus/core';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { EventEcommerceData } from '@features/tracking/model/generic-event-types';
import { PaymentMethod } from '@model/checkout.model';
import { Cart } from '@spartacus/cart/base/root';

export class CheckoutEvent extends CxEvent {
  isFastCheckout?: boolean;
  cart?: Cart;
  checkoutEventType?: CheckoutGA4EventType;
  paymentType?: PaymentMethod;
  user?: EventUserDetails;
  ecommerce?: EventEcommerceData;
}
