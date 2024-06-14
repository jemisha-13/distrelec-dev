import { Ga4CartEventType } from '@features/tracking/model/event-ga-cart-types';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_GA4_VIEW_CART_EVENT = {
  event: Ga4CartEventType.VIEW_CART,
  ecommerce: {
    currency: 'CHF',
    value: 5.45,
    items: [
      {
        item_id: '30158807',
        item_name: 'THT LED White 5500K 2.2cd 4.8mm Dome',
        affiliation: 'Distrelec Switzerland',
        index: 0,
        item_brand: 'RND',
        item_list_id: 'suggested_search_list',
        item_list_name: 'Suggested Search List',
        item_moq: 50,
        location_id: '30',
        price: 0.109,
        quantity: 50,
        item_category: 'Optoelectronics',
        item_category2: 'LEDs',
        item_category3: 'LEDs - Through Hole',
        item_category4: 'Through Hole LEDs - White Colour'
      },
    ],
  },
  user: {
    logged_in: false,
    language: 'english',
    customer_type: AnalyticsCustomerType.B2C,
    mg: false,
  },
  page: { 
    document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
    url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
    category: 'pdp page', 
    market: 'CH' 
  }
};