import { NgModule } from '@angular/core';
import { ProductListSidebarComponent } from '@features/pages/product/plp/product-list/product-list-sidebar/product-list-sidebar.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ProductListFiltersChipsModule } from '../product-list-filters-chips/product-list-filters-chips.module';
import { SidebarFilterListComponent } from './sidebar-filter-list/sidebar-filter-list.component';
import { AbsoluteRouterLinkModule } from '@features/shared-modules/directives/absolute-router-link.module';
import { OptionFilterPipeModule } from '@pipes/option-filter-pipe.module';
import { ProductListFilterValuesModule } from '@features/pages/product/plp/product-list/product-list-filter-values/product-list-filter-values.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { ProductListFiltersSelectedModule } from '../product-list-filters-selected/product-list-filters-selected.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';

@NgModule({
  imports: [
    FormsModule,
    CommonModule,
    I18nModule,
    FontAwesomeModule,
    ProductListFiltersChipsModule,
    AbsoluteRouterLinkModule,
    OptionFilterPipeModule,
    ProductListFilterValuesModule,
    DistIconModule,
    DistButtonComponentModule,
    ProductListFiltersSelectedModule,
    DistScrollBarModule,
  ],
  exports: [ProductListSidebarComponent, DistIconModule],
  declarations: [ProductListSidebarComponent, SidebarFilterListComponent],
})
export class ProductListSidebarModule {}
