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
        AccountPageTemplate: {
          slots: [
            'main-account-information',
            'main-communication-preference',
            'main-account-addresses',
            'main-company-information',
            'main-payment-and-delivery',
            'main-order-history',
            'main-invoice-history',
            'main-quote-history',
            'main-quote-details',
            'main-order-approval',
            'main-user-management',
          ],
        },
      },
    } as LayoutConfig),
  ],
})
export class AccountInformationLayoutModule {}
