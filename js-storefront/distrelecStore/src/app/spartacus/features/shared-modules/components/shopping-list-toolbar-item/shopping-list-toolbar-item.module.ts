import { NgModule } from '@angular/core';
import { ShoppingListToolbarItemComponent } from './shopping-list-toolbar-item.component';
import { ToolbarItemComponentModule } from '@features/shared-modules/components/toolbar-item/toolbar-item.module';

@NgModule({
  imports: [ToolbarItemComponentModule],
  declarations: [ShoppingListToolbarItemComponent],
  exports: [ShoppingListToolbarItemComponent],
})
export class ShoppingListToolbarItemComponentModule {}
