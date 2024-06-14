import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { ImageGalleryComponent } from './image-gallery.component';
import { CarouselModule } from 'ngx-owl-carousel-o';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { I18nModule } from '@spartacus/core';
import { VideoHolderComponentModule } from '@design-system/video-holder/video-holder.module';

@NgModule({
  imports: [
    CommonModule,
    CarouselModule,
    FontAwesomeModule,
    IfModule,
    ForModule,
    I18nModule,
    NgOptimizedImage,
    VideoHolderComponentModule,
  ],
  declarations: [ImageGalleryComponent],
  exports: [ImageGalleryComponent],
})
export class ImageGalleryModule {}
