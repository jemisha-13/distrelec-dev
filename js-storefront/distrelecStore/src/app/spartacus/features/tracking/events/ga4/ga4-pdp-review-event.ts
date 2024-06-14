import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../../model/event-user-details';
import { EventPageDetails } from '@features/tracking/model/event-page-details';

export class Ga4ViewPdpReviewsEvent extends CxEvent {
  event = 'view_pdp_reviews';
  user?: EventUserDetails;
  page?: EventPageDetails;
}
