import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';

export class Ga4SelectItem extends CxEvent {
  event = 'select_item';
  page?: EventPageDetails;
  ecommerce: {
    item_list_id: string;
    item_list_name: string;
    currency: string;
    items: EventProductGA4[];
  };
  
  user?: EventUserDetails;
}
