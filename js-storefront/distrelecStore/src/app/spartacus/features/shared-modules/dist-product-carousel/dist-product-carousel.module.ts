import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CmsConfig, provideConfig } from '@spartacus/core';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { DistProductCarouselComponent } from './dist-product-carousel.component';
import { SharedFeaturedProductsModule } from '../featured-products/featured-products.module';

@NgModule({
  declarations: [DistProductCarouselComponent],
  imports: [CommonModule, ParseHtmlPipeModule, SharedFeaturedProductsModule],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        DistProductCarouselComponent: {
          component: DistProductCarouselComponent,
        },
      },
    }),
  ],
})
export class DistrelecProductCarouselModule {}
