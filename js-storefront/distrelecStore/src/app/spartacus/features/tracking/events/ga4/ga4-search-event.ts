/* eslint-disable @typescript-eslint/naming-convention */
import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { CxEvent } from '@spartacus/core';

export class Ga4SearchEvent extends CxEvent {
  static type = 'search';

  event = Ga4SearchEvent.type;
  search_term: string;
  search_category?: string;

  user?: EventUserDetails;
  page?: EventPageDetails;
}
