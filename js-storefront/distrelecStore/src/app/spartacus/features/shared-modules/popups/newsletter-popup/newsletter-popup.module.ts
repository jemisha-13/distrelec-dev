import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsletterPopupComponent } from './newsletter-popup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { InputTooltipPopupComponent } from '@features/shared-modules/components/input-tooltip-popup/input-tooltip-popup.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { RouterModule } from '@angular/router';
import { ConfirmPopupComponentModule } from '@features/shared-modules/popups/confirm-popup/confirm-popup.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    I18nModule,
    SharedModule,
    RouterModule,
    ConfirmPopupComponentModule,
    DistIconModule,
  ],
  declarations: [NewsletterPopupComponent, InputTooltipPopupComponent],
  exports: [InputTooltipPopupComponent],
})
export class NewsletterPopupModule {}
