import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { ParseJsonPipeModule } from '@pipes/parse-json-pipe.module';

import { HomepageBannerComponent } from './homepage-banner.component';
import { QuickOrderComponent } from '@features/pages/homepage/homepage-banner/quick-order/quick-order.component';
import { QuickOrderRowComponent } from '@features/pages/homepage/homepage-banner/quick-order/quick-order-row/quick-order-row.component';
import { StripHTMLTagsPipesModule } from '@features/shared-modules/pipes/strip-sup-pipes.module';
import { HomeBannerLoginComponent } from '@features/pages/homepage/homepage-banner/login/home-banner-login.component';
import { DistBannerModule } from '@features/shared-modules/banners/dist-banner/dist-banner.module';
import { ExternalRouterLinkModule } from '@features/shared-modules/directives/external-router-link.module';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { SharedRxModule } from '@features/shared-modules/shared-rx.module';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';
import { ProductImageFactFinderPipeModule } from '@pipes/product-image-factfinder-pipe.module';
import { ArticleNumberPipeModule } from '@pipes/article-number-pipe.module';
import { EnergyEfficiencyLabelModule } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.module';

@NgModule({
  declarations: [HomepageBannerComponent, HomeBannerLoginComponent, QuickOrderComponent, QuickOrderRowComponent],
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        Homepage2018BannerWrapperComponent: {
          component: HomepageBannerComponent,
        },
      },
    } as CmsConfig),
    DistBannerModule,
    CommonModule,
    FontAwesomeModule,
    ParseJsonPipeModule,
    RouterModule,
    I18nModule,
    StripHTMLTagsPipesModule,
    ExternalRouterLinkModule,
    MinOrderQuantityPopupModule,
    SharedRxModule,
    PricePipeModule,
    ProductImageFactFinderPipeModule,
    ArticleNumberPipeModule,
    EnergyEfficiencyLabelModule,
    NgOptimizedImage,
  ],
  exports: [HomepageBannerComponent],
})
export class HomepageBannerModule {}
