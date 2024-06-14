/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachPurchaseOrderEvent extends CxEvent {
  static type = 'sales_order';

  type: string;
  body: {
    purchase_id: string;
    purchase_status: string;
    voucher_code?: string;
    voucher_value?: number;
    shipping_cost: number;
    local_currency: string;
    location?: string;
    web_store_url: string;
    product_list: Array<string>;
    product_ids: Array<string>;
    total_quantity: number;
    total_price: number;
    total_price_without_tax: number;
    timestamp: string;
    customer_type: string;
    email?: string;
    order_dispatch_date?: string;
    erp_contact_id?: string;
    placement?: string;
  };
}
