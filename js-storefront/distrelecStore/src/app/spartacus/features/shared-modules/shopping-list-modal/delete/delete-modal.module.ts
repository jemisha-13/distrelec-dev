import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { ShoppingListDeleteModalComponent } from './delete-modal.component';

@NgModule({
  declarations: [ShoppingListDeleteModalComponent],
  imports: [CommonModule, FontAwesomeModule, I18nModule],
  exports: [ShoppingListDeleteModalComponent, FontAwesomeModule],
})
export class ShoppingListDeleteModalModule {}
