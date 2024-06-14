import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { RouterModule } from '@angular/router';
import { AtcButtonComponent } from './atc-button.component';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { I18nModule } from '@spartacus/core';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';

@NgModule({
  imports: [CommonModule, DistIconModule, RouterModule, DistButtonComponentModule, I18nModule, TooltipComponentModule],
  declarations: [AtcButtonComponent],
  exports: [AtcButtonComponent],
})
export class AtcButtonModule {}
