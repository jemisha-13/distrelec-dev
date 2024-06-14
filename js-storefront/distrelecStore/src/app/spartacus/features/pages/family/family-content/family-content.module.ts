import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { ProductFamilyDetailTopBarComponent } from './family-detail-top-bar/family-detail-top-bar.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { FeaturedProductsModule } from '@features/pages/homepage/featured-products/featured-products.module';
import { DistBannerModule } from '@features/shared-modules/banners/dist-banner/dist-banner.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { SanitizeUrlPipeModule } from '@pipes/sanitize-url-pipe.module';
import { PageTitleModule } from '@features/shared-modules/components/page-title/page-title.module';
import { GalleryPreviewComponentModule } from '@features/shared-modules/popups/gallery-preview/gallery-preview.module';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [ProductFamilyDetailTopBarComponent],
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        ProductFamilyDetailTopBarComponent: {
          component: ProductFamilyDetailTopBarComponent,
        },
      },
    } as CmsConfig),
    SanitizeUrlPipeModule,
    SharedModule,
    I18nModule,
    ParseHtmlPipeModule,
    FeaturedProductsModule,
    DistBannerModule,
    PageTitleModule,
    GalleryPreviewComponentModule,
    RouterModule,
  ],
})
export class ProductFamilyContentModule {}
