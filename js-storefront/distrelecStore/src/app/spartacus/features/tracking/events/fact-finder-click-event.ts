import { CxEvent } from '@spartacus/core';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';

export class FactFinderClickEvent extends CxEvent implements FactFinderEvent {
  static type = 'fact-finder-click';

  event = 'click';
  sid: string;
  userId?: string;
  product: string;
  pos: string;
  origPos: string;
  page: string;
  pageSize: string;
  origPageSize: string;
  trackQuery: string;
}
