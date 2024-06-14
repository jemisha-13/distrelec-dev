import { NgModule } from '@angular/core';
import { TooltipComponent } from './tooltip.component';
import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, DistIconModule, I18nModule],
  declarations: [TooltipComponent],
  exports: [TooltipComponent],
})
export class TooltipComponentModule {}
