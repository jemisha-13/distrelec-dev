import { NgModule } from '@angular/core';
import { ConfirmPopupComponent } from './confirm-popup.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [FontAwesomeModule, I18nModule, CommonModule],
  declarations: [ConfirmPopupComponent],
  exports: [ConfirmPopupComponent],
})
export class ConfirmPopupComponentModule {}
