import { CxEvent } from '@spartacus/core';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';

export class FactFinderCheckoutEvent extends CxEvent implements FactFinderEvent {
  static type = 'fact-finder-checkout';

  event = 'checkout';
  order: string;
  channel: string;
  sid: string;
  userId?: string;
}
