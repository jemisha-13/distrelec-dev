import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistCardComponent } from './card.component';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, I18nModule],
  declarations: [DistCardComponent],
  exports: [DistCardComponent],
})
export class DistCardComponentModule {}
