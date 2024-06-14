import { CxEvent } from '@spartacus/core';

export class BannerClickEvent extends CxEvent {
  constructor(public componentUID: string) {
    // inherit properties and methods of CxEvent
    super();
  }
}
