/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent, ImageGroup } from '@spartacus/core';

export class BloomreachPdpViewEvent extends CxEvent {
  static type = 'pageview';

  type: string;
  body: {
    product_id: string;
    pdp_url: string;
    pdp_brand: string;
    pdp_mpn: string;
    pdp_product_title: string;
    pdp_price: number;
    pdp_currency: string;
    pdp_image_url?: ImageGroup;
    pdp_stock: number;
  };
}
