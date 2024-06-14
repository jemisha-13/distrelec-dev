import { CxEvent } from '@spartacus/core';
import { EventUserDetails } from '../model/event-user-details';

export class HotBarClearAllEvent extends CxEvent {
  static type = 'hotbar_clear_all';
  event = HotBarClearAllEvent.type;
  user: EventUserDetails;
}
