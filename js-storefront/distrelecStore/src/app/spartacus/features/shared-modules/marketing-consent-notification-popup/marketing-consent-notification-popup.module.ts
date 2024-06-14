import { NgModule } from '@angular/core';
import { MarketingConsentNotificationPopupComponent } from '@features/shared-modules/marketing-consent-notification-popup/marketing-consent-notification-popup.component';
import { ConfirmPopupComponentModule } from '@features/shared-modules/popups/confirm-popup/confirm-popup.module';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [ConfirmPopupComponentModule, CommonModule, I18nModule],
  declarations: [MarketingConsentNotificationPopupComponent],
  exports: [MarketingConsentNotificationPopupComponent],
})
export class MarketingConsentNotificationPopupComponentModule {}
