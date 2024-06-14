/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachAccountActivationEvent extends CxEvent {
  static type = 'rs_registered';

  type: string;
  body: {
    time_stamp: string;
    erp_contact_id: string;
  };
}
