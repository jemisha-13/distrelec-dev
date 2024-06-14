import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_COMPARE_PRODUCT = {
  event: 'compareProducts',
  compareProduct: '30132220',
  ecommerce: {
    item_list_id: 'compare_list',
    item_list_name: 'Compare List',
    currency: 'CHF',
    items: [
      {
        item_id: '30132220',
        item_name: 'THT LED 5mm Round Red 30mcd 645nm',
        affiliation: undefined,
        index: 0,
        item_brand: 'RND',
        item_moq: 50,
        location_id: '30',
        price: 0.0965,
        quantity: 50,
      },
    ],
  },
  user: {
    logged_in: false,
    language: undefined,
    user_id: undefined,
    customer_type: AnalyticsCustomerType.B2C,
    email: undefined,
    guest_checkout: false,
    mg: false,
  },
};
