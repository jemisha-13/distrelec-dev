import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutLoginComponent } from './checkout-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { CheckoutLoginAuthGuard } from '@features/guards/checkout-login-auth.guard';
import { LoginFormComponent } from './login-form/login-form.component';
import { ForgottenPwdFormComponent } from './forgotten-pwd-form/forgotten-pwd-form.component';
import { ResetPwdFormComponent } from './reset-pwd-form/reset-pwd-form.component';
import { CheckoutGuestFormComponent } from './checkout-guest-form/checkout-guest-form.component';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        CheckoutLoginComponent: {
          component: CheckoutLoginComponent,
          guards: [CheckoutLoginAuthGuard],
        },
      },
    } as CmsConfig),
    ReactiveFormsModule,
    FontAwesomeModule,
    I18nModule,
    RouterModule,
    DistrelecRecaptchaModule,
    ComponentLoadingSpinnerModule,
  ],
  exports: [CheckoutLoginComponent],
  declarations: [
    CheckoutLoginComponent,
    LoginFormComponent,
    ForgottenPwdFormComponent,
    ResetPwdFormComponent,
    CheckoutGuestFormComponent,
  ],
})
export class CheckoutLoginModule {}
