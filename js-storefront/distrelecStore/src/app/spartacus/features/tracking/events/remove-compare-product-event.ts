import { CxEvent } from '@spartacus/core';

export class RemoveCompareProductEvent extends CxEvent {
  productCodes: string[];
}
