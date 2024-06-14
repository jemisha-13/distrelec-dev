import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    provideConfig(<LayoutConfig>{
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
    }),
  ],
})
export class ManufacturerLayoutModule {}
