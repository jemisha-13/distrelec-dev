import { CxEvent, Page, Product } from '@spartacus/core';
import { ProductSuggestion } from '@model/product-suggestions.model';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ICustomProduct } from '@model/product.model';

export class ViewItemListEvent extends CxEvent {
  products: Product[] | ICustomProduct[] | ProductSuggestion[];
  listType: ItemListEntity;
  totalResults?: number;
  page?: Page;
}
