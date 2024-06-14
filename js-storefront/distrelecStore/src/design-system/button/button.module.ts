import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistButtonComponent } from './button.component';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, I18nModule],
  declarations: [DistButtonComponent],
  exports: [DistButtonComponent],
})
export class DistButtonComponentModule {}
