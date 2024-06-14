import { CxEvent } from '@spartacus/core';
import { FactFinderEvent } from '@features/tracking/events/fact-finder-event';

export class FactFinderRecommendationClickEvent extends CxEvent implements FactFinderEvent {
  static type = 'fact-finder-recommendation';

  event = 'recommendationClick';
  product: string;
  mainId: string;
  sid: string;
  userId?: string;
}
