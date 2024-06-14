/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from "@spartacus/core";

export class Ga4ViewPromotionEvent extends CxEvent {
  event: string;
  ecommerce: {
    items: {
      promotion_id: string,
      promotion_name: string,
      creative_name: string,
      creative_slot: string
    }[];
  };
}
