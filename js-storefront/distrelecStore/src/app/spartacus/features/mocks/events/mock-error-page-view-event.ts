import { Ga4Error404EventType } from '@features/tracking/events/ga4/ga4-error-404-event';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_404_ERROR_EVENT = {
  event: Ga4Error404EventType.ERROR_404,
  user: {
    logged_in: true,
    language: undefined,
    customer_type: AnalyticsCustomerType.B2C,
    mg: false
  },
  page: {
    category: "pdp page",
    document_title: "RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland",
    market: "CH",
    url: "https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart"
  },
};
