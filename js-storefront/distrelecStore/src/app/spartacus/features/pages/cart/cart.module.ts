import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartComponent } from './cart.component';
import { CategoryLayoutModule } from './layout-config/layout-category.module';

import { CartProductComponent } from './cart-product/cart-product.component';
import { CartTotalComponent } from './cart-total/cart-total.component';
import { CartToolbarComponent } from './cart-toolbar/cart-toolbar.component';

import { CartSearchComponent } from './cart-search/cart-search.component';
import { CartProductAvailabilityComponent } from './cart-product-availability/cart-product-availability.component';
import { CartVoucherComponent } from './cart-voucher/cart-voucher.component';

import { CartRecommenderComponent } from './cart-recommender/cart-recommender.component';
import { CmsConfig, ConfigModule, HttpErrorHandler, I18nModule } from '@spartacus/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IvyCarouselModule } from 'angular-responsive-carousel';
import { CartQuotationComponent } from './cart-quotation/cart-quotation.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { CartReevooCheckboxComponent } from './cart-reevoo-checkbox/cart-reevoo-checkbox.component';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { CartQuotationListComponent } from './cart-quotation-list/cart-quotation-list.component';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ProductImageFactFinderPipeModule } from '@pipes/product-image-factfinder-pipe.module';
import { SkeletonLoaderModule } from '@features/shared-modules/skeleton-loader/skeleton-loader.module';
import { CartHttpErrorHandler } from '@features/pages/cart/cart-http-error.handler';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { ReferenceInputModule } from '../../shared-modules/reference-input/reference-input.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';

@NgModule({
  imports: [
    ArticleNumberPipeModule,
    CommonModule,
    CategoryLayoutModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CartComponent: {
          component: CartComponent,
        },
      },
    } as CmsConfig),
    ReactiveFormsModule,
    RouterModule,
    I18nModule,
    FontAwesomeModule,
    IvyCarouselModule,
    SharedModule,
    ComponentLoadingSpinnerModule,
    StripHTMLTagsPipesModule,
    NumericStepperComponentModule,
    PricePipeModule,
    ProductImageFactFinderPipeModule,
    SkeletonLoaderModule,
    EnergyEfficiencyLabelModule,
    DistIconModule,
    ReferenceInputModule,
    AtcButtonModule,
    TooltipComponentModule,
  ],
  declarations: [
    CartComponent,
    CartSearchComponent,
    CartVoucherComponent,
    CartProductAvailabilityComponent,
    CartProductComponent,
    CartTotalComponent,
    CartToolbarComponent,
    CartRecommenderComponent,
    CartQuotationComponent,
    CartReevooCheckboxComponent,
    CartQuotationListComponent,
  ],
  exports: [CartComponent],
  providers: [
    {
      provide: HttpErrorHandler,
      useExisting: CartHttpErrorHandler,
      multi: true,
    },
  ],
})
export class DistrelecCartModule {}
