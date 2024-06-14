import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryPageContentModule } from './content/category-page-content.module';
import { RelatedPagesModule } from '@features/shared-modules/related-pages/related-pages.module';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { CategoryPageComponent } from '@features/pages/category/page/category-page.component';
import { LayoutConfig, OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { I18nModule, provideConfig } from '@spartacus/core';
import { PageTitleModule } from '@features/shared-modules/components/page-title/page-title.module';
import { CategoryDescriptionComponent } from './content/category-description/category-description.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

const categoryPageLayoutConfig: LayoutConfig = {
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
};

@NgModule({
  imports: [
    CommonModule,
    PageSlotModule,
    PageLayoutModule,
    OutletModule,
    FontAwesomeModule,
    I18nModule,
    CategoryPageContentModule,
    BreadcrumbWrapperModule,
    RelatedPagesModule,
    PageTitleModule,
  ],
  exports: [RelatedPagesModule],
  declarations: [CategoryPageComponent, CategoryDescriptionComponent],
  providers: [provideConfig(categoryPageLayoutConfig)],
})
export class CategoryPageModule {}
