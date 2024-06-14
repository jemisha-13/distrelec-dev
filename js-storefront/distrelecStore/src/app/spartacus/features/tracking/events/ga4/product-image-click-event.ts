import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../../model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class ProductImageClickEvent extends CxEvent {
  static type = 'imageClick';

  event = ProductImageClickEvent.type;
  productImage: string;
  page?: EventPageDetails;
  user?: EventUserDetails;
}
