import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutAddressComponent } from './checkout-address.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CheckoutDeliveryComponent } from './delivery-modes/checkout-delivery.component';
import { DeliveryDetailsComponent } from './delivery-details/delivery-details.component';
import { CheckoutCalendarComponent } from './checkout-calendar/checkout-calendar.component';
import { BillingDetailsComponent } from './billing-details/billing-details.component';
import { OrderSummaryComponent } from './order-summary/order-summary.component';
import { DeliveryAddressSingleComponent } from './delivery-details/delivery-address-single/delivery-address-single.component';
import { CheckoutDeliveryAuthGuard } from '@features/guards/checkout-auth-delivery.guard';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { BillingAddressFormComponent } from './billing-details/address-form/address-form.component';
import { DeliveryModeComponent } from '@features/pages/checkout/checkout-address/delivery-modes/delivery-mode/delivery-mode.component';
import { AddressFormDeliveryComponent } from './delivery-details/address-form/address-form.component';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { ProductImageFallbackPipeModule } from '@pipes/product-image-fallback-pipe.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { SkeletonLoaderModule } from '@features/shared-modules/skeleton-loader/skeleton-loader.module';
import { TranslateWithDefaultPipeModule } from '@pipes/translate-with-default-pipe.module';
import { CalendarService } from '@services/calendar.service';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CheckoutDeliveryComponent: {
          component: CheckoutAddressComponent,
          guards: [CheckoutDeliveryAuthGuard],
        },
      },
    } as CmsConfig),
    FontAwesomeModule,
    ReactiveFormsModule,
    I18nModule,
    RouterModule,
    ComponentLoadingSpinnerModule,
    SharedModule,
    StripHTMLTagsPipesModule,
    ArticleNumberPipeModule,
    ProductImageFallbackPipeModule,
    PricePipeModule,
    SkeletonLoaderModule,
    TranslateWithDefaultPipeModule,
  ],
  exports: [
    CheckoutDeliveryComponent,
    DeliveryDetailsComponent,
    BillingDetailsComponent,
    CheckoutCalendarComponent,
    OrderSummaryComponent,
    DeliveryAddressSingleComponent,
    BillingAddressFormComponent,
    DeliveryModeComponent,
    AddressFormDeliveryComponent,
    CheckoutAddressComponent,
  ],
  declarations: [
    CheckoutAddressComponent,
    CheckoutDeliveryComponent,
    DeliveryDetailsComponent,
    BillingDetailsComponent,
    CheckoutCalendarComponent,
    OrderSummaryComponent,
    DeliveryAddressSingleComponent,
    BillingAddressFormComponent,
    DeliveryModeComponent,
    AddressFormDeliveryComponent,
  ],
})
export class CheckoutAddressModule {}
