import { CxEvent } from '@spartacus/core';
import { EventProductGA4 } from '@features/tracking/model/event-product';

export class Ga4ViewItemEvent extends CxEvent {
  static type = 'view_item';

  event? = Ga4ViewItemEvent.type;

  /* eslint-disable @typescript-eslint/naming-convention */
  ecommerce: {
    currency: string;
    value: number;
    items: EventProductGA4[];
  };
}
