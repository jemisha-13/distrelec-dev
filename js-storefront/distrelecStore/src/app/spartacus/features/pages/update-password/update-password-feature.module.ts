import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        updatePassword: {
          module: () =>
            import('@features/pages/update-password/update-password.module').then((m) => m.UpdatePasswordModule),
          cmsComponents: ['UpdatePasswordComponent'],
        },
      },
    }),
  ],
})
export class UpdatePasswordFeatureModule {}
