import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShippingTrackingModalComponent } from './shipping-tracking-modal.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [ShippingTrackingModalComponent],
  imports: [CommonModule, FontAwesomeModule, FormsModule, ReactiveFormsModule, I18nModule, NgbAccordionModule],
  exports: [ShippingTrackingModalComponent, FontAwesomeModule],
})
export class ShippingTrackingModalModule {}
