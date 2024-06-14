import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../../model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export enum Ga4Error404EventType {
  ERROR_404 = 'error_404',
}
export class Ga4Error404Event extends CxEvent {
  event = Ga4Error404EventType.ERROR_404;
  user?: EventUserDetails;
  page?: EventPageDetails;
}
