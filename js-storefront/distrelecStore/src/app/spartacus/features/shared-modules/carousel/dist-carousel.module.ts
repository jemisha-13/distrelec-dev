import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistCarouselComponent } from './dist-carousel.component';
import { I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';

@NgModule({
  declarations: [DistCarouselComponent],
  imports: [CommonModule, I18nModule, DistIconModule, DistScrollBarModule],
  exports: [DistCarouselComponent],
})
export class DistCarouselModule {}
