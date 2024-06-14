/* eslint-disable @typescript-eslint/naming-convention */
import { CheckoutEvent } from '@features/tracking/events/checkout-event';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';

export const MOCK_CHECKOUT_STEP_EVENT: CheckoutEvent = {
  checkoutEventType: CheckoutGA4EventType.BEGIN_CHECKOUT,
  user: {
    logged_in: false,
    language: 'english',
    customer_type: AnalyticsCustomerType.B2C,
  },
  ecommerce: {
    currencyCode: 'EUR',
    checkout: {
      actionField: {
        step: 1,
      },
      products: [
        {
          id: '30216307',
          quantity: 1,
          brand: 'PeakTech',
          category: 'Insulation Testers',
          name: 'Insulation Tester, 200GOhm ... 2TOhm, ±5 %',
          price: '713',
          total: '713',
        },
      ],
    },
  },
};
