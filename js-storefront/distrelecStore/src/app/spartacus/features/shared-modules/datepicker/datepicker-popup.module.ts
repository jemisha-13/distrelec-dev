import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbDateParserFormatter, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { NgbDateCustomParserFormatter, NgbdDatepickerPopup } from './datepicker-popup';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [CommonModule, FormsModule, FontAwesomeModule, NgbDatepickerModule],
  declarations: [NgbdDatepickerPopup],
  exports: [NgbdDatepickerPopup, NgbDatepickerModule],
  bootstrap: [NgbdDatepickerPopup],
  providers: [{ provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter }],
})
export class NgbdDatepickerPopupModule {}
