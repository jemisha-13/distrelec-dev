/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachCustomerEvent extends CxEvent {
  // static type = 'customer';
  type: string;
  email_id: string;
  erp_contact_id: string;
}
