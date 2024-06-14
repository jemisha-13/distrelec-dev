import { Ga4ViewItemListEvent } from '@features/tracking/events/ga4/ga4-view-item-list-event';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

/* eslint-disable @typescript-eslint/naming-convention */
export const MOCK_VIEW_ITEM_LIST_EVENT: Ga4ViewItemListEvent = {
  event: 'view_item_list',
  user: {
    logged_in: false,
    language: 'english',
    customer_type: AnalyticsCustomerType.B2C,
    mg: false,
  },
  ecommerce: {
    item_list_id: 'suggested_search_list',
    item_list_name: 'Suggested Search List',
    currency: 'CHF',
    items: [
      {
        item_id: '30158807',
        item_name: 'THT LED White 5500K 2.2cd 4.8mm Dome',
        affiliation: 'Distrelec Switzerland',
        index: 0,
        item_list_id: 'suggested_search_list',
        item_list_name: 'Suggested Search List',
        quantity: 1,
        item_moq: 50,
        price: 0.1177,
      },
      { 
        item_id: '30158802', 
        item_name: 'THT LED White 3000K 1.3cd 3mm Cylindrical', 
        affiliation: 'Distrelec Switzerland', 
        index: 1, 
        item_list_id: 'suggested_search_list', 
        item_list_name: 'Suggested Search List', 
        quantity: 1, 
        item_moq: 50, 
        price: 0.1177 
      },
      { 
        item_id: '30294964', 
        item_name: 'LED Driver, DALI Dimmable CV, 360W 30A 12V IP66', 
        affiliation: 'Distrelec Switzerland', 
        index: 2, 
        item_list_id: 'suggested_search_list', 
        item_list_name: 'Suggested Search List', 
        quantity: 1, 
        item_moq: 1, 
        price: 184.851 
      },
      { 
        item_id: '30158810', 
        item_name: 'THT LED White 5500K 1.7cd 5mm Cylindrical', 
        affiliation: 'Distrelec Switzerland', 
        index: 3, 
        item_list_id: 'suggested_search_list', 
        item_list_name: 'Suggested Search List', 
        quantity: 1, 
        item_moq: 50, 
        price: 0.1177 
      },
      { 
        item_id: '30176681', 
        item_name: 'Vandal Resistant LED IndicatorSoldering Lugs Fixed Red AC / DC 24V', 
        affiliation: 'Distrelec Switzerland', 
        index: 4, 
        item_list_id: 
        'suggested_search_list', 
        item_list_name: 'Suggested Search List', 
        quantity: 1, 
        item_moq: 1,
        price: 9.1344 
      }
    ],
  },
  page: { 
    document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
    url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
    category: 'pdp page', 
    market: 'CH' 
  },
  filterPosition: 'sidebar',
  viewPreference: 'compact'
};
