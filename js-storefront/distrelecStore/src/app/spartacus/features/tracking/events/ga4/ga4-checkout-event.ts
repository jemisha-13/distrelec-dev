import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { EventUserDetails } from '../../model/event-user-details';
import { CheckoutGA4Type } from '@features/tracking/model/checkout-type';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class Ga4CheckoutEvent extends CxEvent {
  event?: CheckoutGA4EventType;
  page?: EventPageDetails;
  /* eslint-disable @typescript-eslint/naming-convention */
  ecommerce: {
    currency: string;
    value: number;
    payment_type?: string;
    shipping_tier?: string;
    items: EventProductGA4[];
  };

  user?: EventUserDetails;
  checkout_type?: CheckoutGA4Type;
}
