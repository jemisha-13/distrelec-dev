import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { CheckoutLoginModule } from './checkout-login/checkout-login.module';
import { CheckoutRegisterModule } from './checkout-register/checkout-register.module';
import { CheckoutAddressModule } from './checkout-address/checkout-address.module';
import { CheckoutPaymentModule } from './checkout-payment/checkout-payment.module';
import { CheckoutOrderConfirmationModule } from './checkout-order-confirmation/checkout-order-confirmation.module';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { LazyLoadStylesService } from '@services/lazyload-styles.service';

@NgModule({
  imports: [
    CommonModule,
    CheckoutLoginModule,
    CheckoutRegisterModule,
    CheckoutAddressModule,
    CheckoutPaymentModule,
    CheckoutOrderConfirmationModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    I18nModule,
    RouterModule,
    DistrelecRecaptchaModule,
  ],
})
export class CheckoutModule {
  constructor(private lazyLoadStyles: LazyLoadStylesService) {
    this.lazyLoadStyles.injectStyles('checkout-styles.css');
  }
}
