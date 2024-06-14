import { CxEvent } from '@spartacus/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ICustomProduct } from '@model/product.model';

export class ViewItemEvent extends CxEvent {
  product: ICustomProduct;
  itemListUrlParam?: ItemListEntity | string;
}
