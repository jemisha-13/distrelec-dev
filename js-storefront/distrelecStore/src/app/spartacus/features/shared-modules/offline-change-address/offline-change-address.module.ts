import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OfflineChangeAddressComponent } from './offline-change-address.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  imports: [CommonModule, FormsModule, I18nModule, ReactiveFormsModule, FontAwesomeModule],
  declarations: [OfflineChangeAddressComponent],
  exports: [OfflineChangeAddressComponent],
})
export class OfflineChangeAddressModule {}
