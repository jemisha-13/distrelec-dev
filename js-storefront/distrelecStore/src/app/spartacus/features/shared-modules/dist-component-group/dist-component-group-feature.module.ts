import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        DistComponentGroup: {
          module: () => import('./dist-component-group.module').then((m) => m.DistComponentGroupModule),
          cmsComponents: ['DistComponentGroup', 'DistRMAGuestReturnsFormComponent'],
        },
      },
    }),
  ],
})
export class DistComponentGroupFeatureModule {}
