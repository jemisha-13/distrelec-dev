/* eslint-disable @typescript-eslint/naming-convention */
import { BloomreachPurchaseItemEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-item-event';

export const MOCK_SALES_ORDER__ITEM_EVENT: BloomreachPurchaseItemEvent = {
  type: 'sales_order_item',
  body: {
    purchase_id: 's2b00009R4L',
    purchase_status: 'CHECKED_VALID',
    local_currency: 'CHF',
    web_store_url: 'https://distrelec-ch.local:4200',
    product_id: '30235023',
    title: 'Monitor, VP, 27 (68.6 cm), 2560 x 1440, IPS, 16:9',
    brand: 'ViewSonic',
    item_line_price: 736,
    item_line_quantity: 1,
    placement: 'my_account',
  },
};
