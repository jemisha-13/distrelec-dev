import { NgModule } from '@angular/core';
import { ConfigModule } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  imports: [
    ConfigModule.withConfig({
      layoutSlots: {
        RegisterPageTemplate: {
          slots: ['main-register'],
        },
      },
    } as LayoutConfig),
  ],
})
export class RegisterLayoutModule {}
