import { VolumePriceMap } from '@model/price.model';
import { Breadcrumb } from '@model/breadcrumb.model';
import { Category } from '@spartacus/core';

declare module '@spartacus/core' {
  interface Suggestion {
    products?: ProductSuggestion[];
    termSuggestions?: Suggestions[];
    categorySuggestions?: Suggestions[];
    manufacturerSuggestions?: Suggestions[];
  }
}

export interface ProductSuggestion {
  code: string;
  name: string;
  url: string;
  image: string;
  articleNr: string;
  typeName: string;
  itemMin: number | string;
  itemStep: number | string;
  priceData: Price;
  energyEfficiency: string;
  distManufacturer?: {
    name?: string;
  };
  salesStatus?: string;
  orderQuantityMinimum?: number;
  volumePricesMap?: VolumePriceMap[];
  breadcrumbs?: Breadcrumb[];
  categories?: Category[];
}

interface Price {
  price: string;
  currency: string;
}

export interface Suggestions {
  name: string;
  url: string;
}
