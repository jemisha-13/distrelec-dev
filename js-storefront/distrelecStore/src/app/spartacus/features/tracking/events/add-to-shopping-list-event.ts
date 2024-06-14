import { CxEvent } from '@spartacus/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingListEntry } from '@model/shopping-list.model';

export class AddToShoppingListEvent extends CxEvent {
  static type = 'addToShoppingList';

  event? = AddToShoppingListEvent.type;
  shoppingListEntries: ShoppingListEntry[];
  itemListEntity?: ItemListEntity;
}
