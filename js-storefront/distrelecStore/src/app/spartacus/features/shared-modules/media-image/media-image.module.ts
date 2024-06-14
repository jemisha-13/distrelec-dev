import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { MediaImageComponent } from './media-image.component';

@NgModule({
  imports: [CommonModule, NgOptimizedImage],
  declarations: [MediaImageComponent],
  exports: [MediaImageComponent],
})
export class MediaImageModule {}
