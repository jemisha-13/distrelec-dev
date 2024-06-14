import { NgModule } from '@angular/core';

import { CompareComponent } from './compare.component';
import { provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig({
      featureModules: {
        compare: {
          module: () => import('./compare.module').then((m) => m.CompareModule),
          cmsComponents: ['CompareComponent'],
        },
      },
    }),
  ],
})
export class CompareFeatureModule {}
