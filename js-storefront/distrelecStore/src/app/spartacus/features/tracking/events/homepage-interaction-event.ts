import { CxEvent } from '@spartacus/core';
import { GaHomepageInteraction } from '../model/event-ga-list-types';

export class HomepageInteractionEvent extends CxEvent {
  type: GaHomepageInteraction;
}
