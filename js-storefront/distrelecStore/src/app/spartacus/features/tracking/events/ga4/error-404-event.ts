import { CxEvent, User } from '@spartacus/core';
import { Ga4Error404EventType } from './ga4-error-404-event';

export class Error404Event extends CxEvent {
  event: Ga4Error404EventType.ERROR_404;
  user?: User | null;
}
