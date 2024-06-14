import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BetterWorldLabelComponent } from './better-world-label.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [BetterWorldLabelComponent],
  imports: [CommonModule, DistIconModule],
  exports: [BetterWorldLabelComponent],
})
export class BetterWorldLabelModule {}
