import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { CxEvent } from '@spartacus/core';

export class Ga4RegistrationStartEvent extends CxEvent {
  event?: string;
  user?: EventUserDetails;
  page: EventPageDetails;
}
