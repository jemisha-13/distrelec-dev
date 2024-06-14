import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DistrelecSummaryModule } from './summary/summary.module';
import { ProductsCarouselModule } from './products-carousel/products-carousel.module';
import { RelatedPagesModule } from '@features/shared-modules/related-pages/related-pages.module';
import { RouterModule } from '@angular/router';
import { ReevooModule } from '@features/shared-modules/reevoo/reevoo.module';
import { NotifyMeModule } from '@features/pages/product/pdp/notify-me/notify-me.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    DistrelecSummaryModule,
    ProductsCarouselModule,
    RelatedPagesModule,
    FontAwesomeModule,
    RouterModule,
    ReevooModule,
    NotifyMeModule,
    DistIconModule,
  ],
})
export class PDPPageModule {}
