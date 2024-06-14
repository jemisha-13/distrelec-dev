import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

export const MOCK_CLICK_EVENT: ProductClickEvent = {
  event: 'productClick',
  listType: ItemListEntity.ACCESSORIES,
  index: 0,
  ecommerce: {
    currencyCode: 'EUR',
    click: {
      actionField: {
        list: 'search suggestion',
      },
      products: [
        {
          id: '17680400',
        },
      ],
    },
  },
};
