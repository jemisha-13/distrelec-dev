import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class Ga4ViewItemListEvent extends CxEvent {
  static type = 'view_item_list';

  event? = Ga4ViewItemListEvent.type;

  user?: EventUserDetails;

  /* eslint-disable @typescript-eslint/naming-convention */
  ecommerce: {
    item_list_id: string;
    item_list_name: string;
    currency: string;
    items: EventProductGA4[];
    item_list_total_results?: number;
    item_list_code?: string;
    item_list_page_number?: number;
    item_list_page_size?: number;
    item_list_sort?: string;
    item_list_filters?: Ga4ViewItemListFilter[];
    redirect_search_term?: string;
  };
  page: EventPageDetails;
  filterPosition: string;
  viewPreference: string;
}

export interface Ga4ViewItemListFilter {
  filter_name: string;
  filter_value: string;
}
