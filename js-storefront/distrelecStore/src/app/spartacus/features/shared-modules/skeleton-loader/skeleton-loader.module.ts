import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkeletonLoaderComponent } from './skeleton-loader.component';
import { SkeletonLoaderCartComponent } from './skeleton-loader-cart/skeleton-loader-cart.component';
import { SkeletonLoaderCheckoutDeliveryComponent } from './skeleton-loader-checkout-delivery/skeleton-loader-checkout-delivery.component';
import { SkeletonLoaderCheckoutReviewPayComponent } from './skeleton-loader-checkout-review-pay/skeleton-loader-checkout-review-pay.component';
import { SkeletonLoaderPLPComponent } from './skeleton-loader-plp/skeleton-loader-plp.component';
import { SkeletonLoaderPDPComponent } from './skeleton-loader-pdp/skeleton-loader-pdp.component';

@NgModule({
  declarations: [
    SkeletonLoaderComponent,
    SkeletonLoaderCartComponent,
    SkeletonLoaderCheckoutDeliveryComponent,
    SkeletonLoaderCheckoutReviewPayComponent,
    SkeletonLoaderPLPComponent,
    SkeletonLoaderPDPComponent,
  ],
  imports: [CommonModule],
  exports: [SkeletonLoaderComponent],
})
export class SkeletonLoaderModule {}
