import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { PDPLayoutModule } from '@features/pages/product/pdp/layout-config/pdp-layout.module';

@NgModule({
  imports: [PDPLayoutModule],
  providers: [
    provideConfig({
      featureModules: {
        pdp: {
          module: () => import('@features/pages/product/pdp/pdp.module').then((m) => m.PDPPageModule),
          cmsComponents: ['ProductSummaryComponent', 'DistProductFFCarouselComponent'],
        },
      },
    }),
  ],
})
export class PdpFeatureModule {}
