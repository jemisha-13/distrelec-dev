import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        guestReturnsForm: {
          module: () => import('./guest-returns-form-feature.module').then((m) => m.GuestReturnsFormFeatureModule),
          cmsComponents: ['DistRMAGuestReturnsFormComponent'],
        },
      },
    }),
  ],
})
export class GuestReturnsFormFeatureModule {}
