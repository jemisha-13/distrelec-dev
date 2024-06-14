import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';
import { ProductListMainComponent } from '@features/pages/product/plp/product-list/product-list-main/product-list-main.component';
import { ProductListFiltersComponent } from '@features/pages/product/plp/product-list/product-list-filters/product-list-filters.component';
import { ProductListTitleComponent } from '@features/pages/product/plp/product-list/product-list-title/product-list-title.component';
import { ProductListPaginationComponent } from '@features/pages/product/plp/product-list/product-list-pagination/product-list-pagination.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ConfigModule.withConfig({
      layoutSlots: {
        SearchResultsListPageTemplate: {
          slots: ['ProductListTitle', 'ProductListFilters', 'ProductListPagination', 'ProductListMain'],
        },
        SearchResultsEmptyPageTemplate: {
          slots: ['ProductListMain'],
        },
        SearchFeedbackSentPageTemplate: {
          slots: ['breadcrumb', 'Content'],
        },
      },
    } as LayoutConfig),
    ConfigModule.forRoot({
      cmsComponents: {
        ProductListMainComponent: {
          component: ProductListMainComponent,
        },
        ProductListFiltersComponent: {
          component: ProductListFiltersComponent,
        },
        ProductListTitleComponent: {
          component: ProductListTitleComponent,
        },
        ProductListPaginationComponent: {
          component: ProductListPaginationComponent,
        },
      },
    } as CmsConfig),
  ],
})
export class PlpLayoutModule {}
