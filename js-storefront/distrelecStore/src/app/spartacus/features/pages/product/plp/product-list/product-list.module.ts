import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { IvyCarouselModule } from 'angular-responsive-carousel';
import { ProductListMainComponent } from '@features/pages/product/plp/product-list/product-list-main/product-list-main.component';
import { ProductListFiltersComponent } from '@features/pages/product/plp/product-list/product-list-filters/product-list-filters.component';
import { ProductListTitleComponent } from '@features/pages/product/plp/product-list/product-list-title/product-list-title.component';
import { ProductListPaginationComponent } from '@features/pages/product/plp/product-list/product-list-pagination/product-list-pagination.component';
import { PriceAndStockModule } from '@features/pages/product/pdp/price-and-stock/price-and-stock.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CountSelectedPipeModule } from '@pipes/count-selected-pipe.module';
import { OptionFilterPipeModule } from '@pipes/option-filter-pipe.module';
import { ProductListFiltersMobileComponent } from './product-list-filters/product-list-filters-mobile/product-list-filters-mobile.component';
import { ProductListSortMobileComponent } from './product-list-sort-mobile/product-list-sort-mobile.component';
import { FloatingToolbarComponent } from '@features/pages/product/plp/product-list/product-list-main/floating-toolbar/floating-toolbar.component';
import { ProductListFiltersScrollListComponent } from '@features/pages/product/plp/product-list/product-list-filters/product-list-filters-scroll-list/product-list-filters-scroll-list.component';
import { ProductListEmptyComponent } from '@features/pages/product/plp/product-list/product-list-empty/product-list-empty.component';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { ProductListAvailabilityComponent } from './product-list-main/product-list-availability/product-list-availability.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { FeedbackCampaignComponent } from './product-list-main/feedback-campaign/feedback-campaign.component';
import { FeedbackCampaignTextComponent } from '@features/pages/product/plp/product-list/product-list-main/feedback-campaign/feedback-campaign-text/feedback-campaign-text.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { ProductListMainItemComponent } from './product-list-main/product-list-main-item/product-list-main-item.component';
import { PaginationLabelComponent } from './pagination-label/pagination-label.component';
import { ProductImageFallbackPipeModule } from '@pipes/product-image-fallback-pipe.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { SkeletonLoaderModule } from '@features/shared-modules/skeleton-loader/skeleton-loader.module';
import { DistrelecLocaleFormatNumberPipeModule } from '@features/shared-modules/pipes/distrelec-locale-format-number.pipe.module';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { ProductListEmptyProductsComponent } from './product-list-empty/product-list-empty-products/product-list-empty-products.component';
import { ProductListToolbarComponent } from './product-list-toolbar/product-list-toolbar.component';
import { ProductListHeaderComponent } from './product-list-header/product-list-header.component';
import { ProductListSidebarModule } from '@features/pages/product/plp/product-list/product-list-sidebar/product-list-sidebar.module';
import { ProductListFiltersChipsModule } from '@features/pages/product/plp/product-list/product-list-filters-chips/product-list-filters-chips.module';
import { ProductListFilterValuesModule } from '@features/pages/product/plp/product-list/product-list-filter-values/product-list-filter-values.module';
import { DistLabelComponentModule } from '@design-system/label/label.module';
import { AddToListModule } from '@features/pages/product/common/add-to-list/add-to-list.module';
import { ProductDescriptionAttributesModule } from '@features/pages/product/plp/product-list/product-list-main/product-description-attributes/product-description-attributes.module';
import { AddToCartModule } from '@features/pages/product/common/add-to-cart/add-to-cart.module';
import { ProductListFiltersSelectedModule } from './product-list-filters-selected/product-list-filters-selected.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';
import { DistProductCardModule } from '@design-system/product-card/product-card.module';
import { DistCarouselModule } from '@features/shared-modules/carousel/dist-carousel.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { BetterWorldLabelModule } from '../../pdp/summary/better-world-label/better-world-label.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [
    ProductListMainComponent,
    ProductListFiltersComponent,
    ProductListTitleComponent,
    ProductListPaginationComponent,
    ProductListFiltersMobileComponent,
    ProductListSortMobileComponent,
    FloatingToolbarComponent,
    FeedbackCampaignComponent,
    ProductListFiltersScrollListComponent,
    ProductListEmptyComponent,
    ProductListAvailabilityComponent,
    FeedbackCampaignTextComponent,
    ProductListMainItemComponent,
    PaginationLabelComponent,
    ProductListEmptyProductsComponent,
    ProductListToolbarComponent,
    ProductListHeaderComponent,
  ],
  imports: [
    CommonModule,
    PriceAndStockModule,
    FontAwesomeModule,
    FormsModule,
    RouterModule,
    CountSelectedPipeModule,
    OptionFilterPipeModule,
    I18nModule,
    ReactiveFormsModule,
    ReevooModule,
    EnergyEfficiencyLabelModule,
    SharedModule,
    IvyCarouselModule,
    ParseHtmlPipeModule,
    ProductImageFallbackPipeModule,
    PricePipeModule,
    ArticleNumberPipeModule,
    SkeletonLoaderModule,
    DistrelecLocaleFormatNumberPipeModule,
    StripHTMLTagsPipesModule,
    ProductListSidebarModule,
    ProductListFiltersChipsModule,
    ProductListFilterValuesModule,
    DistLabelComponentModule,
    AddToListModule,
    ProductDescriptionAttributesModule,
    AddToCartModule,
    ProductListFiltersSelectedModule,
    AtcButtonModule,
    DistJsonLdModule,
    DistProductCardModule,
    DistCarouselModule,
    NgSelectModule,
    BetterWorldLabelModule,
    DistIconModule,
  ],
  exports: [
    ProductListMainComponent,
    ProductListFiltersComponent,
    ProductListTitleComponent,
    ProductListPaginationComponent,
    ProductListEmptyComponent,
    ProductListEmptyProductsComponent,
  ],
})
export class ProductListModule {}
