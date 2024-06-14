import { Ga4CartEventType } from "@features/tracking/model/event-ga-cart-types";
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_ADD_TO_CART_EVENT = {
  event: Ga4CartEventType.ADD_TO_CART,
  ecommerce: {
    currency: 'CHF',
    value: 5.175,
    items: [
      {
        item_id: "30158802",
        item_name: "THT LED White 3000K 1.3cd 3mm Cylindrical",
        affiliation: 'Distrelec Switzerland',
        index: 0,
        item_brand: "RND",
        location_id: "30",
        price: 0.1035,
        quantity: 50,
        item_moq: 50,
        item_category: "Optoelectronics",
        item_category2: "LEDs",
        item_category3: "LEDs - Through Hole",
        item_category4: "Through Hole LEDs - White Colour"
      }
    ],
  },
  user: {
    customer_type: AnalyticsCustomerType.B2C,
    language: 'english',
    logged_in: false,
    mg: false
  },
  page: { 
    document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
    url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
    category: 'pdp page', 
    market: 'CH' 
  }
};
