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
    }),
  ],
})
export class ProductFamilyLayoutModule {}
