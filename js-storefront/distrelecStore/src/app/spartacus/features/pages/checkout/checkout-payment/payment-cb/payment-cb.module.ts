import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentCbComponent } from './payment-cb.component';
import { RouterModule } from '@angular/router';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: 'checkout/payment/success',
        component: PaymentCbComponent,
        pathMatch: 'prefix',
      },
      {
        path: 'checkout/payment/failure',
        component: PaymentCbComponent,
        pathMatch: 'prefix',
      },
    ]),
    ComponentLoadingSpinnerModule,
  ],
  declarations: [PaymentCbComponent],
  exports: [PaymentCbComponent],
})
export class PaymentCbModule {}
