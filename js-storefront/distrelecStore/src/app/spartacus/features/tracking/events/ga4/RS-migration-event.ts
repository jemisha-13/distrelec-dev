import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class RSMigrationEvent extends CxEvent {
  event: string;
  user?: EventUserDetails;
  page?: EventPageDetails;
}
