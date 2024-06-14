import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedFeaturedProductsComponent } from './featured-products.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { I18nModule } from '@spartacus/core';
import { CarouselModule } from 'ngx-owl-carousel-o';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';

@NgModule({
  declarations: [SharedFeaturedProductsComponent],
  imports: [
    CommonModule,
    CarouselModule,
    FontAwesomeModule,
    RouterModule,
    I18nModule,
    PricePipeModule,
  ],
  exports: [SharedFeaturedProductsComponent],
})
export class SharedFeaturedProductsModule {}
