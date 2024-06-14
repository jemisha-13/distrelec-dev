import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateWithDefaultPipe } from './translate-with-default.pipe';

@NgModule({
  declarations: [TranslateWithDefaultPipe],
  imports: [CommonModule],
  exports: [TranslateWithDefaultPipe],
})
export class TranslateWithDefaultPipeModule {}
