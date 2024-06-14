import { CxEvent, Product } from '@spartacus/core';
import { EventEcommerceData, ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductSuggestion } from '@model/product-suggestions.model';
import { OrderEntry } from '@spartacus/cart/base/root';
import { EventUserDetails } from '@features/tracking/model/event-user-details';
import { ICustomProduct } from '@model/product.model';

export class ProductClickEvent extends CxEvent {
  static type = 'productClick';

  event = ProductClickEvent.type;
  product?: Product | ProductSuggestion | ICustomProduct;
  entry?: OrderEntry;
  listType: ItemListEntity;
  index: number;
  user?: EventUserDetails;
  ecommerce?: EventEcommerceData;
}
