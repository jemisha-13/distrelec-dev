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
        CategoryPageTemplate: {
          slots: [
            'Content',
            'category-relatedData',
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
export class CategoryLayoutModule {}
