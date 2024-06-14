import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';

export const MOCK_HEADER_INTERACTION = {
  event: Ga4HeaderInteractionEventType.HEADER_CLICK_SETTINGS,
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
    market: 'CH', 
  },
};
