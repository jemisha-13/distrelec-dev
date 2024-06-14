import { CxEvent } from '@spartacus/core';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';

export class FactFinderLoginEvent extends CxEvent implements FactFinderEvent {
  static type = 'fact-finder-login';

  event = 'login';
  sid: string;
  userId?: string;
}
