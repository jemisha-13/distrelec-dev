import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LabelComponent } from './label.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, DistIconModule],
  declarations: [LabelComponent],
  exports: [LabelComponent],
})
export class DistLabelComponentModule {}
