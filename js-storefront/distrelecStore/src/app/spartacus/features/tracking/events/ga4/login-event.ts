import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class LoginEvent extends CxEvent {
  static type = 'login';

  event = LoginEvent.type;
  user: EventUserDetails;
  page: EventPageDetails;
}
