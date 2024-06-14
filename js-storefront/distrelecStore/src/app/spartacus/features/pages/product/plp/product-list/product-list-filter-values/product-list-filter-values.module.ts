import { NgModule } from '@angular/core';
import { ProductListFilterValuesComponent } from '@features/pages/product/plp/product-list/product-list-filter-values/product-list-filter-values.component';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OptionFilterPipeModule } from '@pipes/option-filter-pipe.module';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    FontAwesomeModule,
    OptionFilterPipeModule,
    FormsModule,
    CheckboxModule,
    DistIconModule,
    DistScrollBarModule,
  ],
  exports: [ProductListFilterValuesComponent],
  declarations: [ProductListFilterValuesComponent],
})
export class ProductListFilterValuesModule {}
