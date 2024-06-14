import { CxEvent } from '@spartacus/core';
import { Ga4HeaderInteractionEventType } from '../model/event-ga-header-types';

export class HeaderInteractionEvent extends CxEvent {
  type: Ga4HeaderInteractionEventType;
}
