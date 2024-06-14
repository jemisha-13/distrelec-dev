/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachCategoryViewEvent extends CxEvent {
  static type = 'category';

  type: string;
  body: {
    category_name: string;
    category_url: string;
    category_id: string;
    category_path: string;
  };
}
