import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { BannerModule } from '@spartacus/storefront';

import { SimpleBannerComponent } from './simple-banner/simple-banner.component';

@NgModule({
  imports: [BannerModule],
  exports: [BannerModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        banners: {
          module: () => import('@features/shared-modules/banners/banner.module').then((m) => m.DistrelecBannerModule),
          cmsComponents: ['SimpleBannerComponent', 'DistBannerComponent'],
        },
      },
    }),
  ],
})
export class DistrelecBannerFeatureModule {}
