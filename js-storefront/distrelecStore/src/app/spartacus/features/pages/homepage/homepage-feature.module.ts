import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { HomePageLayoutModule } from '@features/pages/homepage/layout-config/homepage-layout.module';

@NgModule({
  imports: [HomePageLayoutModule],
  providers: [
    provideConfig({
      featureModules: {
        homepage: {
          module: () => import('@features/pages/homepage/homepage.module').then((m) => m.HomePageModule),
          cmsComponents: [
            'Homepage2018BannerWrapperComponent',
            'DistFeaturedProductsComponent',
            'DistWelcomeMatComponent',
          ],
        },
      },
    }),
  ],
})
export class HomePageFeatureModule {}
