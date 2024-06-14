import { Price, Product } from '@spartacus/core';

import { ICustomProduct } from '@model/product.model';

export interface List {
  name: string;
  totalUnitCount: number;
  uniqueId: string;
}

export interface ShoppingLists {
  list: ShoppingList[];
}

export interface ShoppingList {
  calculated: boolean;
  defaultWishlist: boolean;
  entries: any[];
  listType: string;
  name: string;
  productCodes: string[];
  punchOutProducts: PunchOutProducts[];
  subTotal: Price;
  totalPrice: Price;
  totalTax: Price;
  totalUnitCount: number;
  uniqueId: string;
}

export interface PunchOutProducts {
  isBuyable: boolean;
  isPunchedOut: boolean;
  productCode: string;
  salesStatus: string;
}

export interface ShoppingListPayloadItem {
  product: Partial<Product> | Partial<ICustomProduct>;
  desired?: number;
  comment?: string;
}

export interface ShoppingListUpdateEntryResponse {
  status: boolean;
}

export interface ShoppingListEntry {
  addedDate: string;
  desired: number;
  priceObject?: Price;
  product: Product;
}
