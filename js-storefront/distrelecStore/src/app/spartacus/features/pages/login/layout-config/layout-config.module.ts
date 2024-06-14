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
        LoginPageTemplate: {
          slots: ['main-login', 'main-resetPassword', 'main-updatePassword'],
        },
      },
    } as LayoutConfig),
  ],
})
export class LoginLayoutModule {}
