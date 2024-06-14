import { CheckoutGA4Type } from '@features/tracking/model/checkout-type';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_CHECKOUT_EVENT = {
  event: CheckoutGA4EventType.BEGIN_CHECKOUT,
  checkout_type: CheckoutGA4Type.REGULAR_CHECKOUT,
  ecommerce: {
    currency: 'CHF',
    value: 77.5,
    items: [
      {
        item_id: '30132220',
        item_name: 'THT LED 5mm Round Red 30mcd 645nm',
        affiliation: 'Distrelec Switzerland',
        index: 0,
        item_brand: 'RND',
        item_moq: 50,
        location_id: '30',
        price: 7.75,
        quantity: 10,
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
