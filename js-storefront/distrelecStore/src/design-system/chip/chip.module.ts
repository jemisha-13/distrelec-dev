import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { ChipComponent } from './chip.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, I18nModule, DistIconModule],
  declarations: [ChipComponent],
  exports: [ChipComponent],
})
export class ChipModule {}
