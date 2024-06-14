import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScrollBarComponent } from './scroll-bar.component';

@NgModule({
  imports: [CommonModule],
  declarations: [ScrollBarComponent],
  exports: [ScrollBarComponent],
})
export class DistScrollBarModule {}
