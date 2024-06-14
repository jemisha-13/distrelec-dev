import { GaHomepageInteraction } from '@features/tracking/model/event-ga-list-types';
import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { CxEvent } from '@spartacus/core';

export class Ga4HomepageInteractionEvent extends CxEvent {
  event: 'homepage_content_interaction';
  interaction_type: GaHomepageInteraction;
  user?: EventUserDetails;
  page: EventPageDetails;
}
