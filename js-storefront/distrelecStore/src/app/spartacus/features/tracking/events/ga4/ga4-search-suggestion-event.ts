/* eslint-disable @typescript-eslint/naming-convention */
import { EventPageDetails } from '@features/tracking/model/event-page-details';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { CxEvent } from '@spartacus/core';

export class Ga4SearchSuggestionEvent extends CxEvent {
  static type = 'suggestedSearch';
  event = Ga4SearchSuggestionEvent.type;
  search_term: string;
  search_category?: string;

  user?: EventUserDetails;
  page?: EventPageDetails;
}
