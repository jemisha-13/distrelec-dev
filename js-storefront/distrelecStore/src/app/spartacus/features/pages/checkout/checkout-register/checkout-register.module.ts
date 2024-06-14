import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutRegisterComponent } from './checkout-register.component';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { RegisterModule } from '@features/pages/register/register.module';
import { NotLoggedInAuthGuard } from '@features/guards/not-logged-in-auth.guard';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CheckoutRegistrationComponent: {
          component: CheckoutRegisterComponent,
          guards: [NotLoggedInAuthGuard],
        },
      },
    } as CmsConfig),
    RegisterModule,
  ],
  declarations: [CheckoutRegisterComponent],
})
export class CheckoutRegisterModule {}
