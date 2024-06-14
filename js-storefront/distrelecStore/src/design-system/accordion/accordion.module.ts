import { NgModule } from '@angular/core';
import { AccordionComponent } from './accordion.component';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, I18nModule, DistIconModule],
  declarations: [AccordionComponent],
  exports: [AccordionComponent],
})
export class AccordionComponentModule {}
