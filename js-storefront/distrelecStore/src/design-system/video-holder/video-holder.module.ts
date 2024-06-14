import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoHolderComponent } from './video-holder.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, DistIconModule],
  declarations: [VideoHolderComponent],
  exports: [VideoHolderComponent],
})
export class VideoHolderComponentModule {}
