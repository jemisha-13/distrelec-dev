import { SortValueOptions } from '@features/pages/product/plp/product-list/product-list-main/product-list-main-options';

export enum FusionPaginationQueryKey {
  PAGE_SIZE = 'rows',
  SORT = 'sort',
  PAGE = 'start',
}

export const SORT_MAPPING = {
  [SortValueOptions.MANUFACTURER_ASC]: 'manufacturerName_s asc',
  [SortValueOptions.MANUFACTURER_DESC]: 'manufacturerName_s desc',
  [SortValueOptions.PRICE_ASC]: 'singleMinPriceGross_d asc',
  [SortValueOptions.PRICE_DESC]: 'singleMinPriceGross_d desc',
  [SortValueOptions.BEST]: '',
};
