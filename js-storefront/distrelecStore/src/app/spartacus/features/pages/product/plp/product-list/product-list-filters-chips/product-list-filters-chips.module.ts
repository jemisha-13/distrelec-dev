import { NgModule } from '@angular/core';
import { ProductListFiltersChipsComponent } from '@features/pages/product/plp/product-list/product-list-filters-chips/product-list-filters-chips.component';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ChipModule } from '@design-system/chip/chip.module';

@NgModule({
  declarations: [ProductListFiltersChipsComponent],
  imports: [CommonModule, I18nModule, FontAwesomeModule, ChipModule],
  exports: [ProductListFiltersChipsComponent],
})
export class ProductListFiltersChipsModule {}
