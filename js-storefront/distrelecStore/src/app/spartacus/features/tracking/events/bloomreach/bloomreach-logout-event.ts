import { CxEvent } from '@spartacus/core';

export class BloomreachLogoutEvent extends CxEvent {
  static type = 'logout';

  type: string;
}
