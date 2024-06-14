import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../model/event-user-details';
import { ItemListEntity } from '../model/generic-event-types';
import { EventPageDetails } from '../model/event-page-details';

export class Ga4PrintPageEvent extends CxEvent {
  event = 'print_page';
  user: EventUserDetails;
  pageType: ItemListEntity;
  page: EventPageDetails;
}

export class PrintPageEvent extends CxEvent {
  context: {
    pageType: ItemListEntity;
  };
}
