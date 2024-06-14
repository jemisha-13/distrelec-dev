import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalibratedLabelComponent } from './calibrated-label.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [CommonModule, FontAwesomeModule, RouterModule],
  declarations: [CalibratedLabelComponent],
  exports: [CalibratedLabelComponent],
})
export class CalibratedLabelModule {}
