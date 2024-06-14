/* eslint-disable @typescript-eslint/naming-convention */
import { BloomreachPurchaseOrderEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-order-event';

export const MOCK_SALES_ORDER_EVENT: BloomreachPurchaseOrderEvent = {
  type: 'sales_order',
  body: {
    purchase_id: 's2b00009R4L',
    purchase_status: 'CHECKED_VALID',
    voucher_code: 'ALD7A93',
    voucher_value: undefined,
    shipping_cost: 0,
    local_currency: 'CHF',
    web_store_url: 'https://distrelec-ch.local:4200',
    product_list: ['Monitor, VP, 27 (68.6 cm), 2560 x 1440, IPS, 16:9'],
    product_ids: ['30235023'],
    total_quantity: 1,
    total_price: 755,
    total_price_without_tax: 736,
    timestamp: '2023-08-18T06:23:58+0000',
    customer_type: 'B2C',
    placement: 'my_account',
    order_dispatch_date: '2023-08-18T06:23:58+0000',
    location: 'https://distrelec-ch.local:4200/en/checkout/orderConfirmation/4ca45324-9ab8-4c03-a858-85b0588b7c6a',
  },
};

export const MOCK_SALES_ORDER_GUEST_EVENT: BloomreachPurchaseOrderEvent = {
  type: 'sales_order',
  body: {
    purchase_id: 's2b0000DGL3',
    purchase_status: 'CHECKED_VALID',
    voucher_code: undefined,
    voucher_value: undefined,
    shipping_cost: 0,
    local_currency: 'CHF',
    location: 'https://distrelec-ch.local:4200/en/checkout/orderConfirmation/4ca45324-9ab8-4c03-a858-85b0588b7c6a',
    web_store_url: 'https://distrelec-ch.local:4200',
    product_list: ['Monitor, VP, 27 (68.6 cm), 2560 x 1440, IPS, 16:9'],
    product_ids: ['30235023'],
    total_quantity: 1,
    total_price: 755,
    total_price_without_tax: 701,
    timestamp: '2023-10-16T10:02:57+0000',
    customer_type: 'GUEST',
    placement: 'guest',
    order_dispatch_date: '2023-10-16T10:02:57+0000',
    email: 'royovom2678@mugadget.com',
  },
};
