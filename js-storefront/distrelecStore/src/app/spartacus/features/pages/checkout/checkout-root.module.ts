import { NgModule } from '@angular/core';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { HttpErrorHandler } from '@spartacus/core';
import { CheckoutLayoutModule } from '@features/pages/checkout/layout-config/layout-checkout.module';
import { PaymentCbModule } from '@features/pages/checkout/checkout-payment/payment-cb/payment-cb.module';
import { CmsPageGuard, PageLayoutComponent } from '@spartacus/storefront';
import { CheckoutOrderConfirmationAuthGuard } from '@features/guards/checkout-order-confirmation-auth.guard';
import { DistCheckoutForbiddenHandler } from '@handlers/checkout-forbidden-handler';
import { DistCheckoutHttpErrorHandler } from '@handlers/checkout-error-handler';
import { OrderModule } from '@spartacus/order';

@NgModule({
  imports: [
    PaymentCbModule,
    CheckoutLayoutModule,
    RouterModule.forChild([
      {
        path: 'checkout/orderConfirmation/:orderCode',
        data: {
          pageLabel: '/checkout/orderConfirmation',
        },
        component: PageLayoutComponent,
        canActivate: mapToCanActivate([CmsPageGuard, CheckoutOrderConfirmationAuthGuard]),
        pathMatch: 'prefix',
      },
    ]),
    OrderModule,
  ],
  providers: [
    {
      provide: HttpErrorHandler,
      useExisting: DistCheckoutForbiddenHandler,
      multi: true,
    },
    {
      provide: HttpErrorHandler,
      useExisting: DistCheckoutHttpErrorHandler,
      multi: true,
    },
  ],
})
export class CheckoutRootModule {}
