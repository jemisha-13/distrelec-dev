import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductFamilyLayoutModule } from './layout-config/layout-family.module';
import { ProductFamilyContentModule } from './family-content/family-content.module';
import { LayoutConfig, OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { provideConfig } from '@spartacus/core';
import { ProductFamilyComponent } from './page/product-family-content.component';

const productFamilyLayoutConfig: LayoutConfig = {
  layoutSlots: {
    ProductFamilyPageTemplate: {
      slots: [
        'Content',
        'ProductFamilyDetailTopBar',
        'ProductListTitle',
        'ProductListFilters',
        'ProductListPagination',
        'ProductListMain',
      ],
    },
  },
};

@NgModule({
  declarations: [ProductFamilyComponent],
  imports: [
    CommonModule,
    ProductFamilyLayoutModule,
    ProductFamilyContentModule,
    PageLayoutModule,
    PageSlotModule,
    OutletModule,
  ],
  providers: [provideConfig(productFamilyLayoutConfig)],
})
export class ProductFamilyModule {}
