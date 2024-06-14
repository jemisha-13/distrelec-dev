import { CxEvent } from '@spartacus/core';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class PromotionClickEvent extends CxEvent {
  static type = 'select_promotion';

  page?: EventPageDetails;
  event = PromotionClickEvent.type;
  ecommerce: {
    promotion_id: string;
    promotion_name: string;
    creative_slot: string;
    creative_name: string;
    campaign_date: string;
    location_id: string;
    items: Record<string, string>[];
  };
}
