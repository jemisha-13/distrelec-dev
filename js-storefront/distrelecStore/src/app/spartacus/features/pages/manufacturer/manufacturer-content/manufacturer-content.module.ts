import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { ManufacturerDetailTopBarComponent } from './manufacturer-detail-top-bar/manufacturer-detail-top-bar.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { FeaturedProductsModule } from '@features/pages/homepage/featured-products/featured-products.module';
import { DistBannerModule } from '@features/shared-modules/banners/dist-banner/dist-banner.module';
import { PageComponentModule } from '@spartacus/storefront';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [ManufacturerDetailTopBarComponent],
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        ManufacturerDetailTopBarComponent: {
          component: ManufacturerDetailTopBarComponent,
        },
      },
    } as CmsConfig),
    ParseHtmlPipeModule,
    FeaturedProductsModule,
    DistBannerModule,
    PageComponentModule,
    FontAwesomeModule,
  ],
})
export class ManufacturerContentModule {}
