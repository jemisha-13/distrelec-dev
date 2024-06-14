import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ProductListFiltersSelectedComponent } from './product-list-filters-selected.component';
import { DistButtonComponentModule } from '@design-system/button/button.module';

@NgModule({
  declarations: [ProductListFiltersSelectedComponent],
  imports: [CommonModule, I18nModule, FontAwesomeModule, DistButtonComponentModule],
  exports: [ProductListFiltersSelectedComponent],
})
export class ProductListFiltersSelectedModule {}
