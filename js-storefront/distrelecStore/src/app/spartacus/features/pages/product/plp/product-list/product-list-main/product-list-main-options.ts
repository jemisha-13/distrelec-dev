export interface ListFilters {
  queryKey: PaginationQueryKey;
  options: SelectOptions[];
}

export interface SelectOptions {
  value: string | number;
  label: string;
}

export enum PaginationQueryKey {
  PAGE_SIZE = 'pageSize',
  SORT = 'sort',
  PAGE = 'currentPage',
}

export enum SortValueOptions {
  MANUFACTURER_ASC = 'Manufacturer:asc',
  MANUFACTURER_DESC = 'Manufacturer:desc',
  PRICE_ASC = 'Price:asc',
  PRICE_DESC = 'Price:desc',
  BEST = '',
}

export const PRODUCTS_PER_PAGE_OPTIONS: ListFilters = {
  queryKey: PaginationQueryKey.PAGE_SIZE,
  options: [
    { value: 10, label: '10' },
    { value: 25, label: '25' },
    { value: 50, label: '50' },
    { value: 100, label: '100' },
  ],
};

export const SORT_OPTIONS: ListFilters = {
  queryKey: PaginationQueryKey.SORT,
  options: [
    { value: '', label: 'best_match' },
    { value: 'Manufacturer:asc', label: 'manufacturer_a-z' },
    { value: 'Manufacturer:desc', label: 'manufacturer_z-a' },
    { value: 'Price:asc', label: 'price_low_to_high' },
    { value: 'Price:desc', label: 'price_high_to_low' },
  ],
};

export const VIEW_OPTIONS: SelectOptions[] = [
  { value: 'compact', label: 'Compact View' },
  { value: 'detailed', label: 'Detailed View' },
];
