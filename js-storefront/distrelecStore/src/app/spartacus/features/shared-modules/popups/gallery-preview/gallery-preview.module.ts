import { NgModule } from '@angular/core';
import { GalleryPreviewComponent } from '@features/shared-modules/popups/gallery-preview/gallery-preview.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SharedRxModule } from '@features/shared-modules/shared-rx.module';
import { CommonModule } from '@angular/common';
import { VideoHolderComponentModule } from '@design-system/video-holder/video-holder.module';

@NgModule({
  imports: [FontAwesomeModule, SharedRxModule, CommonModule, VideoHolderComponentModule],
  declarations: [GalleryPreviewComponent],
  exports: [GalleryPreviewComponent],
})
export class GalleryPreviewComponentModule {}
