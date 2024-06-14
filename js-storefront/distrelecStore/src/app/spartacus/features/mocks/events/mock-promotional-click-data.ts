import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';
import { PromotionClickEvent } from '@features/tracking/events/ga4/promotion-click-event';

export const MOCK_PROMOTIONAL_CLICK_DATA: PromotionClickEvent = {
  event: 'select_promotion',
  ecommerce: {
    "campaign_date": "2411",
    "promotion_id": "2411newproducts",
    "promotion_name": "newproducts",
    "creative_name": "rspro",
    "creative_slot": "lp-hero",
    "location_id": "0",
    "items": [{}],
  },
  page: {
    document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland',
    url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart',
    category: 'pdp page',
    market: 'CH'
  },
}
