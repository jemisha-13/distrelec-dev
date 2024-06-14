import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { Ga4CartEventType } from '@features/tracking/model/event-ga-cart-types';
import { EventUserDetails } from '../../model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class Ga4CartEvent extends CxEvent {
  event: Ga4CartEventType;

  ecommerce: {
    currency: string;
    value: number;
    items: EventProductGA4[];
  };

  user?: EventUserDetails;
  page?: EventPageDetails;
}
