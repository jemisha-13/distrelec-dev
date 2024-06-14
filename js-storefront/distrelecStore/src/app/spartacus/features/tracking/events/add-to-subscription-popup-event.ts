import { CxEvent } from '@spartacus/core';

export class AddToSubscriptionPopupEvent extends CxEvent {
  static type = 'optimize.activate.subscriptionpopup';
  event = AddToSubscriptionPopupEvent.type;
}
