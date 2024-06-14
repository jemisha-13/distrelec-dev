import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        DistRestrictionComponentGroup: {
          module: () =>
            import('./dist-restriction-component-group.module').then((m) => m.DistRestrictionComponentGroupModule),
          cmsComponents: ['DistRestrictionComponentGroup'],
        },
      },
    }),
  ],
})
export class DistRestrictionComponentGroupFeatureModule {}
