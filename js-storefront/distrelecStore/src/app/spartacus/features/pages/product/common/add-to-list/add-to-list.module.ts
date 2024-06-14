import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddToListComponent } from '@features/pages/product/common/add-to-list/add-to-list.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';

@NgModule({
  declarations: [AddToListComponent],
  imports: [CommonModule, FontAwesomeModule, I18nModule, CheckboxModule],
  exports: [AddToListComponent],
})
export class AddToListModule {}
