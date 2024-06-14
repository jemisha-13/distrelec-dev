import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { CheckoutOrderConfirmationComponent } from './checkout-order-confirmation.component';
import { CheckoutOrderInfoComponent } from './checkout-order-info/checkout-order-info.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { CheckoutOrderConfirmationAuthGuard } from '@features/guards/checkout-order-confirmation-auth.guard';
import { ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { PasswordStrengthBarModule } from '@features/shared-modules/components/password-strength-bar/password-strength-bar.module';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { RouterModule } from '@angular/router';
import { CheckoutOrderConfirmationLayoutModule } from './layout-config/layout-checkout.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { CheckoutManageYourAccountComponent } from './checkout-manage-your-account/checkout-manage-your-account.component';
import { CheckoutProfileInformationComponent } from './checkout-profile-information/checkout-profile-information.component';
import { CheckoutConsentCaptureComponent } from './checkout-consent-capture/checkout-consent-capture.component';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { CheckoutDoubleOptInModalComponent } from './checkout-double-opt-in-modal/checkout-double-opt-in-modal.component';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { CheckoutGuestRegistrationComponent } from '@features/pages/checkout/checkout-order-confirmation/checkout-guest-registration/checkout-guest-registration.component';
import { CheckoutInvoiceRequestComponent } from '@features/pages/checkout/checkout-order-confirmation/checkout-invoice-request/checkout-invoice-request.component';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { CheckoutErpVoucherComponent } from '@features/pages/checkout/checkout-order-confirmation/checkout-erp-voucher/checkout-erp-voucher.component';
import { DistCardComponentModule } from '@design-system/card/card.module';

@NgModule({
  imports: [
    CommonModule,
    CheckoutOrderConfirmationLayoutModule,
    ConfigModule.forRoot({
      cmsComponents: {
        MainCheckoutOrderConfirmationComponent: {
          component: CheckoutOrderConfirmationComponent,
          guards: [CheckoutOrderConfirmationAuthGuard],
        },
        dependencies: [() => import('@spartacus/order').then((m) => m.OrderModule)],
      },
    } as CmsConfig),
    ReactiveFormsModule,
    I18nModule,
    FontAwesomeModule,
    ComponentLoadingSpinnerModule,
    PasswordStrengthBarModule,
    DistrelecRecaptchaModule,
    RouterModule,
    DistIconModule,
    DistButtonComponentModule,
    CheckboxModule,
    PricePipeModule,
    DistCardComponentModule,
  ],
  exports: [
    CheckoutOrderConfirmationComponent,
    CheckoutOrderInfoComponent,
    CheckoutManageYourAccountComponent,
    CheckoutProfileInformationComponent,
    CheckoutConsentCaptureComponent,
    CheckoutDoubleOptInModalComponent,
    CheckoutGuestRegistrationComponent,
    CheckoutInvoiceRequestComponent,
    CheckoutErpVoucherComponent,
  ],
  declarations: [
    CheckoutOrderConfirmationComponent,
    CheckoutOrderInfoComponent,
    CheckoutManageYourAccountComponent,
    CheckoutProfileInformationComponent,
    CheckoutConsentCaptureComponent,
    CheckoutDoubleOptInModalComponent,
    CheckoutGuestRegistrationComponent,
    CheckoutInvoiceRequestComponent,
    CheckoutErpVoucherComponent,
  ],
})
export class CheckoutOrderConfirmationModule {}
