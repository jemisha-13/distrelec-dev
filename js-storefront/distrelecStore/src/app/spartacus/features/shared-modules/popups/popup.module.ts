import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';

import { BackdropModuleComponent } from '@features/shared-modules/backdrop-module/backdrop-module.component';
import { MovPopupComponent } from '@features/shared-modules/popups/mov-popup/mov-popup.component';
import { SuccessPopupComponent } from '@features/shared-modules/popups/success-popup/success-popup.component';
import { ContentPopupComponent } from '@features/shared-modules/popups/content-popup/content-popup.component';
import { FormModalComponent } from '@features/shared-modules/popups/form-modal/form-modal.component';
import { CommunicationPrefPopupComponentModule } from '@features/shared-modules/popups/communication-pref-popup/communication-pref-popup.module';
import { ConfirmPopupComponentModule } from '@features/shared-modules/popups/confirm-popup/confirm-popup.module';
import { WarningPopupComponentModule } from '@features/shared-modules/popups/warning-popup/warning-popup.module';
import { MarketingConsentNotificationPopupComponentModule } from '@features/shared-modules/marketing-consent-notification-popup/marketing-consent-notification-popup.module';
import { LoadingLogoComponentModule } from '@features/shared-modules/loading-logo/loading-logo.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { DistIconModule } from '../icon/icon.module';
import { RouterModule } from '@angular/router';

/**
 * These are the components that are dynamically added to the DOM by append-component.service.ts
 *
 * We should reduce the number of dependencies in that service, and instead try to use the generic
 * appendComponent() and destroyComponent() methods and then create the popup/modal components in
 * the features that need them, instead of having them all in this module.
 */
@NgModule({
  imports: [I18nModule, CommonModule, ReactiveFormsModule, PricePipeModule, DistIconModule, RouterModule],
  declarations: [
    BackdropModuleComponent,
    MovPopupComponent,
    SuccessPopupComponent,
    ContentPopupComponent,
    FormModalComponent,
  ],
  exports: [
    BackdropModuleComponent,
    MovPopupComponent,
    SuccessPopupComponent,
    ContentPopupComponent,
    FormModalComponent,
    CommunicationPrefPopupComponentModule,
    ConfirmPopupComponentModule,
    WarningPopupComponentModule,
    MarketingConsentNotificationPopupComponentModule,
    LoadingLogoComponentModule,
  ],
})
export class PopupModule {}
