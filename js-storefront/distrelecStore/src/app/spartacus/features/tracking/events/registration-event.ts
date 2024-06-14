import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { EventPageDetails } from '../model/event-page-details';

export class RegistrationEvent extends CxEvent {
  static type = 'register';

  event = RegistrationEvent.type;
  user: EventUserDetails;
  page: EventPageDetails;
}
