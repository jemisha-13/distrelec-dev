import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { WarningPopupComponent } from './warning-popup.component';

@NgModule({
  imports: [FontAwesomeModule, I18nModule, CommonModule],
  declarations: [WarningPopupComponent],
  exports: [WarningPopupComponent],
})
export class WarningPopupComponentModule {}
