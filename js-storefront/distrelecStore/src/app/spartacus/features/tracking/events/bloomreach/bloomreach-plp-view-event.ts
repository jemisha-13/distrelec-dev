/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachPlpViewEvent extends CxEvent {
  static type = 'plp_view';

  type: string;
  body: {
    plp_category_name: string;
    plp_url: string;
    plp_id: string;
    plp_category_path: string;
    search_text?: string;
  };
}
