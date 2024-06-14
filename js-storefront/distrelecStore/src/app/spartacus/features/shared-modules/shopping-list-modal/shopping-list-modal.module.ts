import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShoppingListModalComponent } from './shopping-list-modal.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { ShoppingListDeleteModalModule } from './delete/delete-modal.module';

@NgModule({
  declarations: [ShoppingListModalComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    ShoppingListDeleteModalModule,
  ],
  exports: [ShoppingListModalComponent, FontAwesomeModule],
})
export class ShoppingListModalModule {}
