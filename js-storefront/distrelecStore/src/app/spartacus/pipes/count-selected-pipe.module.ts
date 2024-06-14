import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CountSelectedPipe } from '@pipes/count-selected.pipe';

@NgModule({
  declarations: [CountSelectedPipe],
  imports: [CommonModule],
  exports: [CountSelectedPipe],
})
export class CountSelectedPipeModule {}
