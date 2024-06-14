/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachUpdateCartEvent extends CxEvent {
  static type = 'update_cart';

  type: string;

  body: {
    action: string;
    page_type: string;
    product_id: string;
    brand: string;
    category_id: string;
    price: number;
    currency: string;
    discount_percentage?: number;
    discount_value?: number;
    original_price?: number;
    local_currency: string;
    web_store_url: string;
    webshop_url_lang: string;
    language: string;
    product_list?: string;
    product_ids?: string;
  };
}
