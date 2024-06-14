import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';

export class Ga4AddToWishlistEvent extends CxEvent {
  event?: string = 'add_to_wishlist';

  ecommerce: {
    currency: string;
    value: number;
    items: EventProductGA4[];
  };

  user: EventUserDetails;
  page: EventPageDetails;
}
