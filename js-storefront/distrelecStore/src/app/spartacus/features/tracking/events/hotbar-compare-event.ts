import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../model/event-user-details';
import { EventPageDetails } from '../model/event-page-details';

export class HotBarCompareEvent extends CxEvent {
  static type = 'hotbar_compare';
  event = HotBarCompareEvent.type;
  page: EventPageDetails;
  user: EventUserDetails;
}
