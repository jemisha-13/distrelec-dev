import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_IMAGE_CLICK = {
  event: 'imageClick',
  productImage: '30132220',
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
