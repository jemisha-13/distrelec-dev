import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterComponent } from './newsletter.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { NewsletterPopupModule } from '@features/shared-modules/popups/newsletter-popup/newsletter-popup.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { MarketingConsentNotificationPopupComponentModule } from '@features/shared-modules/marketing-consent-notification-popup/marketing-consent-notification-popup.module';

@NgModule({
  declarations: [NewsletterComponent],
  imports: [
    CommonModule,
    I18nModule,
    FontAwesomeModule,
    FormsModule,
    ReactiveFormsModule,
    DistrelecRecaptchaModule,
    NewsletterPopupModule,
    MarketingConsentNotificationPopupComponentModule,
    SharedModule,
  ],
  exports: [NewsletterComponent],
})
export class NewsletterModule {}
