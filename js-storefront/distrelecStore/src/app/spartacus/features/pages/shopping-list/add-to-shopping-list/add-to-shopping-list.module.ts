import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { DistTextFieldComponentModule } from '@design-system/text-field/text-field.module';
import { DistButtonModule } from 'src/app/shared-components/dist-button/dist-button.module';
import { ShoppingListAddToListComponent } from './add-to-shopping-list.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [ShoppingListAddToListComponent],
  imports: [
    FontAwesomeModule,
    I18nModule,
    CommonModule,
    DistIconModule,
    SlideDrawerModule,
    CheckboxModule,
    DistButtonModule,
    DistTextFieldComponentModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [ShoppingListAddToListComponent],
})
export class ShoppingListAddToListModule {}
