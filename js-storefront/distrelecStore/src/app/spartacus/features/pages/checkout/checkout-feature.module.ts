import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { CheckoutRootModule } from '@features/pages/checkout/checkout-root.module';
import { OrderRootModule } from '@spartacus/order/root';
import { ORDER_FEATURE } from '@spartacus/order/core';

@NgModule({
  imports: [CheckoutRootModule, OrderRootModule],
  providers: [
    provideConfig({
      featureModules: {
        checkout: {
          module: () => import('@features/pages/checkout/checkout.module').then((m) => m.CheckoutModule),
          cmsComponents: [
            'CheckoutLoginComponent',
            'CheckoutRegistrationComponent',
            'CheckoutDeliveryComponent',
            'CheckoutReviewAndPayComponent',
            'MainCheckoutOrderConfirmationComponent',
          ],
        },
        [ORDER_FEATURE]: {
          module: () => import('@spartacus/order').then((m) => m.OrderModule),
          cmsComponents: ['MainCheckoutOrderConfirmationComponent'],
        },
      },
    }),
  ],
})
export class CheckoutFeatureModule {}
