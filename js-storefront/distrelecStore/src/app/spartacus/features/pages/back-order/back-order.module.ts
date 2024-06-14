import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackOrderComponent } from './back-order.component';
import { BackOrderItemComponent } from './back-order-item/back-order-item.component';
import { BackOrderSaveComponent } from './back-order-save/back-order-save.component';
import { BackOrderAlternativeComponent } from './back-order-alternative/back-order-alternative.component';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReactiveFormsModule } from '@angular/forms';
import { DistComponentGroupModule } from '@features/shared-modules/dist-component-group/dist-component-group.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { BackOrderAlternativeProductDetailsModalComponent } from './back-order-alternative/back-order-alternative-product-details-modal/back-order-alternative-product-details-modal.component';
import { AddToQueueComponent } from './back-order-alternative/add-to-queue/add-to-queue.component';
import { CmsPageGuard } from '@spartacus/storefront';
import { BackorderAuthGuard } from '@features/guards/backorder-auth.guard';
import { ButtonComponentModule } from '@features/shared-modules/components/button/button.module';
import { ModalComponentModule } from '@features/shared-modules/popups/modal/modal.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    RouterModule.forChild([
      {
        path: '',
        component: BackOrderComponent,
        canActivate: [CmsPageGuard, BackorderAuthGuard],
        pathMatch: 'prefix',
        data: {
          pageLabel: 'checkout/backorderDetails',
        },
      },
    ]),
    DistComponentGroupModule,
    ArticleNumberPipeModule,
    RouterModule,
    SharedModule,
    ComponentLoadingSpinnerModule,
    ButtonComponentModule,
    ModalComponentModule,
    PricePipeModule,
    VolumePricePipeModule,
    DecimalPlacesPipeModule,
  ],
  declarations: [
    AddToQueueComponent,
    BackOrderComponent,
    BackOrderItemComponent,
    BackOrderSaveComponent,
    BackOrderAlternativeComponent,
    BackOrderAlternativeProductDetailsModalComponent,
  ],
  exports: [BackOrderComponent],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutBackorderDetailsComponent: {
          component: BackOrderComponent,
        },
      },
    } as CmsConfig),
  ],
})
export class BackOrderModule {}
