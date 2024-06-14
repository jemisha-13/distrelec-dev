import { CxEvent } from '@spartacus/core';

export class ProductImage3dClickEvent extends CxEvent {
  static type = '3dImageClick';

  event = ProductImage3dClickEvent.type;
  productImage: string;
}
