import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../model/event-user-details';
import { EventPageDetails } from '../model/event-page-details';

export class SiteSearchNoResultsEvent extends CxEvent {
  static type = 'nilResultSearch';

  event = SiteSearchNoResultsEvent.type;
  searchTerm: string;

  user?: EventUserDetails;
  page: EventPageDetails;
}
