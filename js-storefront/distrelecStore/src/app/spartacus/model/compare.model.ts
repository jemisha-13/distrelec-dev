import { Price, Product } from '@spartacus/core';

export interface CompareShortData {
  listType: string;
  totalUnitCount: number;
  uniqueId: string;
}

export interface CompareList {
  uniqueId?: string;
  totalUnitCount?: number;
  products?: Product[];
}

export interface CompareProductAddedResponse {
  products: Product[];
}

export interface CompareProductRemovedResponse {
  status: boolean;
}

export interface CompareEntry {
  addedDate?: string;
  comment: string;
  desired: string;
  product: { code: string };
}

export interface IAddedCompareProduct {
  calculated: boolean;
  defaultWishlist: boolean;
  description: string;
  listType: string;
  name: string;
  totalUnitCount: number;
  uniqueId: string;
  entries: CompareEntry[];
  subTotal: Price;
  totalPrice: Price;
  totalTax: Price;
}
