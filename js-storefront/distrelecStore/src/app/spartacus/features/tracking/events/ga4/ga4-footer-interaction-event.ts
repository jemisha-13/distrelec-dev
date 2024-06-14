/* eslint-disable @typescript-eslint/naming-convention */
import { CxEvent } from '@spartacus/core';

export class FooterInteractionEvent extends CxEvent {
  context: {
    menu_name: string;
    menu_item: string;
  };
}

export class Ga4FooterInteractionEvent extends CxEvent {
  event: 'footer_interaction' = 'footer_interaction';
  menu_name: string;
  menu_item: string;
}
