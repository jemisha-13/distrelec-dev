/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachProductFamilyViewEvent extends CxEvent {
  static type = 'product_family_view';

  type: string;
  body: {
    product_family_url: string;
    product_family_id: string;
    product_family_manufacturer: string;
  };
}
