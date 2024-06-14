import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OptionFilterPipe } from '@pipes/option-filter.pipe';

@NgModule({
  declarations: [OptionFilterPipe],
  imports: [CommonModule],
  exports: [OptionFilterPipe],
})
export class OptionFilterPipeModule {}
