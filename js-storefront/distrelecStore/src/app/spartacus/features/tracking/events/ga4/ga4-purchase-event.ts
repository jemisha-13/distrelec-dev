import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { EventUserDetails } from '../../model/event-user-details';
import { CheckoutGA4Type } from '@features/tracking/model/checkout-type';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export enum Ga4PurchaseEventType {
  PURCHASE = 'purchase',
}
export class Ga4PurchaseEvent extends CxEvent {
  event?: string = Ga4PurchaseEventType.PURCHASE;

  page?: EventPageDetails;
  /* eslint-disable @typescript-eslint/naming-convention */
  ecommerce: {
    currency: string;
    transaction_id: string;
    value: number;
    shipping?: number;
    tax?: number;
    items: EventProductGA4[];
  };

  user?: EventUserDetails;
  checkout_type?: CheckoutGA4Type;
}
