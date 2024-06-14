import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../../model/event-user-details';
import { EventProductGA4 } from '../../model/event-product';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class CompareProductEvent extends CxEvent {
  static type = 'compare_products';

  event = CompareProductEvent.type;
  compareProduct: string;

  ecommerce: {
    item_list_id: string;
    item_list_name: string;
    currency: string;
    items: EventProductGA4[];
  };

  page?: EventPageDetails;
  user?: EventUserDetails;
}
