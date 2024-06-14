/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class BloomreachPasswordPageViewEvent extends CxEvent {
  static type = 'rs_password_pageview';

  type: string;
  body: {
    time_stamp: string;
    email_id: string;
    web_store_url: string;
    language: string;
  };
}
