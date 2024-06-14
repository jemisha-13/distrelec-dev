import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        DistProductCarouselComponent: {
          module: () => import('./dist-product-carousel.module').then((m) => m.DistrelecProductCarouselModule),
          cmsComponents: ['DistProductCarouselComponent'],
        },
      },
    }),
  ],
})
export class DistrelecProductCarouselFeatureModule {}
