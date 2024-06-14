/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachManufacturerViewEvent extends CxEvent {
  static type = 'manufacturer_view';

  type: string;
  body: {
    manufacturer_url: string;
    manufacturer_id: string;
    manufacturer_brand: string;
  };
}
