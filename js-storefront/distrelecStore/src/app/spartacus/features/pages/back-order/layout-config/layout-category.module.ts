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
        'checkoutbackorder-Template': {
          slots: ['main-checkout-backorder-details'],
        },
      },
    } as LayoutConfig),
  ],
})
export class BackorderLayoutModule {}
