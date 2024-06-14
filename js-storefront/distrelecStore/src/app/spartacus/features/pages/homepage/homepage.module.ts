import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedProductsModule } from './featured-products/featured-products.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { HomepageBannerModule } from '@features/pages/homepage/homepage-banner/homepage-banner.module';
import { SharedRxModule } from '@features/shared-modules/shared-rx.module';
import { WelcomeMatComponentModule } from '@features/pages/homepage/welcome-mat/welcome-mat.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FeaturedProductsModule,
    FontAwesomeModule,
    HomepageBannerModule,
    WelcomeMatComponentModule,
    SharedRxModule,
  ],
})
export class HomePageModule {}
