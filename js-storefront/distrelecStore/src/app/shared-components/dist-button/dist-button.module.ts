import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistButtonComponent } from './dist-button.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [DistButtonComponent],
  imports: [CommonModule, FontAwesomeModule],
  exports: [DistButtonComponent],
})
export class DistButtonModule {}
