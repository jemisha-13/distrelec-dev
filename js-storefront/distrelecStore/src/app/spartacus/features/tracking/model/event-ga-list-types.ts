export interface GaListType {
  id: string;
  name: string;
}

export enum GaListID {
  SEARCH = 'search_list',
  CATEGORY = 'cat_list',
  MANUFACTURER = 'man_list',
  PFP = 'pfp_list',
  PDP = 'pdp_list',
  ALTERNATIVE = 'alternative_list',
  ALTERNATIVE_UPPER = 'alternative_upper_list',
  ACCESSORIES = 'accessories_list',
  COMPATIBLE = 'compatible_list',
  OPTIONAL = 'optional_list',
  MANDATORY = 'mandatory_list',
  SHOPPING = 'shopping_list',
  COMPARE = 'compare_list',
  SUGGESTED_SEARCH = 'suggested_search_list',
  FEATURED = 'featured_list',
  CART_SEARCH = 'cart_search_list',
  CART_RELATED = 'cart_related_list',
  CART = 'cart_list',
  FEEDBACK_CAMPAIGN = 'feedback_campaign_list',
  STICKY_CART = 'sticky_cart_list',
  BACKORDER = 'backorder_list',
  BOM = 'bom_list',
  QUICK_ORDER = 'quick_order_list',
  ORDER_APPROVAL = 'order_approval_list',
  ORDER_DETAILS = 'order_details_list',
}

export enum GaListName {
  SEARCH = 'Product Search List',
  CATEGORY = 'Product Category List',
  MANUFACTURER = 'Product Manufacturer List',
  PFP = 'Product Family List',
  PDP = 'Product Details List',
  ALTERNATIVE = 'Alternative List',
  ALTERNATIVE_UPPER = 'Alternative Upper List',
  ACCESSORIES = 'Accessories List',
  COMPATIBLE = 'Compatible List',
  OPTIONAL = 'Optional List',
  MANDATORY = 'Mandatory List',
  SHOPPING = 'Shopping List',
  COMPARE = 'Compare List',
  SUGGESTED_SEARCH = 'Suggested Search List',
  FEATURED = 'Featured Products List',
  CART_SEARCH = 'Cart Search List',
  CART_RELATED = 'Cart Related Products List',
  CART = 'Cart List',
  FEEDBACK_CAMPAIGN = 'Feedback Campaign List',
  STICKY_CART = 'Sticky Cart List',
  BACKORDER = 'Backorder List',
  BOM = 'BOM List',
  QUICK_ORDER = 'Quick Order List',
  ORDER_APPROVAL = 'Order Approval List',
  ORDER_DETAILS = 'Order Details List',
}

export type GaHomepageInteraction = 'login_click' | 'registration_click';
