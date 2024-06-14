import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ConfigModule.withConfig({
      layoutSlots: {
        CheckoutPageTemplate: {
          slots: ['main-checkoutLogin', 'main-checkout-register', 'main-checkout', 'MainSlot-registration'],
        },
      },
    } as LayoutConfig),
  ],
})
export class CheckoutLayoutModule {}
