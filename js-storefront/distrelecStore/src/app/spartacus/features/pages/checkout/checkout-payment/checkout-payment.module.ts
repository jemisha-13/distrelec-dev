import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutPaymentComponent } from './checkout-payment.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { CheckoutPaymentLayoutModule } from './layout-config/layout-checkout.module';
import { OrderSummaryComponent } from './order-summary/order-summary.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { PaymentDetailsComponent } from './payment-details/payment-details.component';
import { AddressDeliverySummaryComponent } from './address-delivery-summary/address-delivery-summary.component';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { RouterModule } from '@angular/router';
import { CheckoutPaymentAuthGuard } from '@features/guards/checkout-auth-payment.guard';
import { PaymentFormComponent } from './payment-form/payment-form.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { TermsAndConditionsComponent } from './order-summary/terms-and-conditions/terms-and-conditions.component';
import { ItalyCodiceComponent } from './italy-codice/italy-codice.component';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { ProductImageFallbackPipeModule } from '@pipes/product-image-fallback-pipe.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { SkeletonLoaderModule } from '@features/shared-modules/skeleton-loader/skeleton-loader.module';
import { TranslateWithDefaultPipeModule } from '@pipes/translate-with-default-pipe.module';
import { CardHolderComponent } from './payment-details/card-holder/card-holder.component';
import { TermsAndConditionsAcceptanceComponent } from './order-summary/terms-and-conditions-acceptance/terms-and-conditions-acceptance.component';
import { ReferenceInputModule } from '@features/shared-modules/reference-input/reference-input.module';

@NgModule({
  imports: [
    CommonModule,
    CheckoutPaymentLayoutModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CheckoutReviewAndPayComponent: {
          component: CheckoutPaymentComponent,
          guards: [CheckoutPaymentAuthGuard],
        },
      },
    } as CmsConfig),
    ReactiveFormsModule,
    FontAwesomeModule,
    ComponentLoadingSpinnerModule,
    I18nModule,
    SharedModule,
    RouterModule,
    StripHTMLTagsPipesModule,
    ProductImageFallbackPipeModule,
    PricePipeModule,
    ArticleNumberPipeModule,
    SkeletonLoaderModule,
    TranslateWithDefaultPipeModule,
    ReferenceInputModule,
  ],
  exports: [
    CheckoutPaymentComponent,
    OrderSummaryComponent,
    TermsAndConditionsComponent,
    PaymentDetailsComponent,
    AddressDeliverySummaryComponent,
    PaymentFormComponent,
    ItalyCodiceComponent,
    TermsAndConditionsAcceptanceComponent,
  ],
  declarations: [
    CheckoutPaymentComponent,
    OrderSummaryComponent,
    TermsAndConditionsComponent,
    PaymentDetailsComponent,
    AddressDeliverySummaryComponent,
    PaymentFormComponent,
    ItalyCodiceComponent,
    CardHolderComponent,
    TermsAndConditionsAcceptanceComponent,
  ],
})
export class CheckoutPaymentModule {}
