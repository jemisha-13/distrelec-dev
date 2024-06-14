/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachPurchaseItemEvent extends CxEvent {
  static type = 'sales_order_item';

  type: string;
  body: {
    purchase_id: string;
    purchase_status: string;
    local_currency: string;
    web_store_url: string;
    product_id: string;
    title: string;
    brand: string;
    item_line_price: number;
    item_line_quantity: number;
    placement?: string;
  };
}
