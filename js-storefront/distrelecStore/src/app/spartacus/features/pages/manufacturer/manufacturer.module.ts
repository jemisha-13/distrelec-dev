import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManufacturerLayoutModule } from '@features/pages/manufacturer/layout-config/manufacturer-layout.module';
import { ManufacturerContentModule } from '@features/pages/manufacturer/manufacturer-content/manufacturer-content.module';
import { ManufacturerPageComponent } from './page/manufacturer-page-content.component';
import { LayoutConfig, OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { provideConfig } from '@spartacus/core';

const manufacturerPageLayoutConfig: LayoutConfig = {
  layoutSlots: {
    ManufacturerStoreDetailPageTemplate: {
      slots: [
        'ManufacturerDetailTopBar',
        'ManufacturerTopPosition',
        'ProductListTitle',
        'ProductListFilters',
        'ProductListPagination',
        'ProductListMain',
      ],
    },
  },
};

@NgModule({
  declarations: [ManufacturerPageComponent],
  imports: [
    CommonModule,
    ManufacturerLayoutModule,
    ManufacturerContentModule,
    PageLayoutModule,
    PageSlotModule,
    OutletModule,
  ],
  providers: [provideConfig(manufacturerPageLayoutConfig)],
})
export class ManufacturerModule {}
