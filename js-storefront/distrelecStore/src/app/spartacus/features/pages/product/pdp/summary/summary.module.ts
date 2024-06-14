import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';

import { DownloadsComponent } from '../downloads/downloads.component';
import { ProductDetailsComponent } from '../product-details/product-details.component';
import { ImageHolderComponent } from './image-holder/image-holder.component';
import { ProductIntroComponent } from './product-intro/product-intro.component';
import { SummaryComponent } from './summary.component';

import { NotifyMeModule } from '../notify-me/notify-me.module';
import { PriceAndStockModule } from '../price-and-stock/price-and-stock.module';
import { ProductSpecificationsModule } from '../product-specifications/product-specifications.module';

import { FilterLabelsPipeModule } from '@pipes/filter-labels.pipe.module';

import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AccordionComponentModule } from '@design-system/accordion/accordion.module';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { StickyAddToCartComponentModule } from '@design-system/sticky-add-to-cart/sticky-add-to-cart.module';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { AddToListModule } from '@features/pages/product/common/add-to-list/add-to-list.module';
import { PunchOutProductGuard } from '@features/pages/product/pdp/guards/punch-out-product.guard';
import { ArticleTooltipModule } from '@features/shared-modules/components/article-tooltip/article-tooltip.module';
import { MagnifyImageDirectiveModule } from '@features/shared-modules/directives/magnify-image-directive.module';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { GalleryPreviewComponentModule } from '@features/shared-modules/popups/gallery-preview/gallery-preview.module';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { SnapedaModule } from '@features/shared-modules/snapeda/snapeda.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ClipboardModule } from 'ngx-clipboard';
import { LearnMorePopupComponent } from '../product-details/learn-more-popup/learn-more-popup.component';
import { BackToCategoryComponent } from './back-to-category-cta/back-to-category-cta.component';
import { CalibratedBannerModule } from './calibrated-banner/calibrated-banner.module';
import { CalibratedLabelModule } from './calibrated-label/calibrated-label.module';
import { ImageGalleryModule } from './image-gallery/image-gallery.module';
import { ProductLabelComponent } from './product-label/product-label.component';
import { ReportErrorFormComponent } from './report-error-form/report-error-form.component';
import { DividerLineModule } from '@design-system/divider-line/divider-line.module';
import { DistLabelComponentModule } from '@design-system/label/label.module';
import { ProductAvailabilityGuard } from '@features/pages/product/pdp/guards/product-availability.guard';
import { BetterWorldLabelModule } from './better-world-label/better-world-label.module';
import { FirstWordPipe } from './back-to-category-cta/pipe/first-word.pipe';
import { AudioPlayComponent } from './image-holder/audio-play/audio-play.component';
import { ProductSeoRedirectGuard } from '@features/pages/product/pdp/guards/product-seo-redirect.guard';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        ProductSummaryComponent: {
          component: SummaryComponent,
          guards: [PunchOutProductGuard, ProductSeoRedirectGuard, ProductAvailabilityGuard],
        },
      },
    } as CmsConfig),
    NotifyMeModule,
    PriceAndStockModule,
    ProductSpecificationsModule,
    EnergyEfficiencyLabelModule,
    FontAwesomeModule,
    ClipboardModule,
    I18nModule,
    RouterModule,
    ReevooModule,
    ImageGalleryModule,
    CalibratedBannerModule,
    CalibratedLabelModule,
    SnapedaModule,
    SharedModule,
    FilterLabelsPipeModule,
    ArticleNumberPipeModule,
    MagnifyImageDirectiveModule,
    GalleryPreviewComponentModule,
    ArticleTooltipModule,
    NgOptimizedImage,
    AccordionComponentModule,
    StickyAddToCartComponentModule,
    DistIconModule,
    ReactiveFormsModule,
    DistButtonComponentModule,
    AlertBannerComponentModule,
    TooltipComponentModule,
    AddToListModule,
    DividerLineModule,
    DistLabelComponentModule,
    BetterWorldLabelModule,
  ],
  declarations: [
    SummaryComponent,
    ProductIntroComponent,
    ImageHolderComponent,
    DownloadsComponent,
    ProductDetailsComponent,
    ProductLabelComponent,
    LearnMorePopupComponent,
    ReportErrorFormComponent,
    BackToCategoryComponent,
    FirstWordPipe,
    AudioPlayComponent,
  ],
})
export class DistrelecSummaryModule {}
