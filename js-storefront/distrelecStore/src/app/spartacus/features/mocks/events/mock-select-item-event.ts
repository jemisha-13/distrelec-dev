import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Ga4SelectItem } from '@features/tracking/events/ga4/ga4-select-item-event';

export const MOCK_SELECT_ITEM_EVENT: Ga4SelectItem = {
  event: 'select_item',
  ecommerce: {
    item_list_id: '',
    item_list_name: '',
    currency: '',
    items: []
  },
};
