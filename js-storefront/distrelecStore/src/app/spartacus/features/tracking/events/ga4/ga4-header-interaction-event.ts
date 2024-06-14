import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../../model/event-user-details';
import { Ga4HeaderInteractionEventType } from '@features/tracking/model/event-ga-header-types';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class Ga4HeaderInteractionEvent extends CxEvent {
  event?: Ga4HeaderInteractionEventType;
  user?: EventUserDetails;
  page?: EventPageDetails;
}
