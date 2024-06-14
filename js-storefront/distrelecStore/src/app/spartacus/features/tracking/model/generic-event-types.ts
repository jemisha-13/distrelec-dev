export enum ItemListEntity {
  SEARCH = 'search',
  CATEGORY = 'category',
  MANUFACTURER = 'manufacturer',
  PFP = 'pfp',
  PDP = 'pdp',
  ALTERNATIVE = 'alternative',
  ALTERNATIVE_UPPER = 'alternative_upper',
  ACCESSORIES = 'accessories',
  COMPATIBLE = 'compatible',
  OPTIONAL = 'optional',
  MANDATORY = 'mandatory',
  SHOPPING = 'shopping',
  COMPARE = 'compare',
  SUGGESTED_SEARCH = 'suggested_search',
  FEATURED = 'featured',
  CART_SEARCH = 'cart_search',
  CART = 'cart',
  CART_RELATED = 'cart_related',
  FEEDBACK_CAMPAIGN = 'feedback_campaign',
  STICKY_CART = 'sticky_cart',
  BACKORDER = 'backorder',
  BOM = 'bom',
  QUICK_ORDER = 'quick_order',
  ORDER_APPROVAL = 'order_approval',
  ORDER_DETAILS = 'order_details',
  ORDER_HISTORY = 'order_history',
  ORDER_HISTORY_DETAILS = 'order_history_details',
  QUOTE_HISTORY = 'quote_history',
  QUOTE_DETAILS = 'quote_details',
  INVOICE = 'invoice',
}

export interface RouteEventParams {
  code?: string;
  searchTerm?: string;
  pos?: string;
  origPos?: string;
  currentPage?: string;
  pageSize?: string;
  origPageSize?: string;
  trackQuery?: string;
  redirectQuery?: string;
  sort?: string;
  filters?: { name: string; value: string }[];
}

export interface EventEcommerceData {
  currencyCode?: string;
  items?: [];
  click?: any;
  checkout?: unknown;
}
