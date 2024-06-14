import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { SMART_EDIT_FEATURE, SmartEditRootModule } from '@spartacus/smartedit/root';

@NgModule({
  declarations: [],
  imports: [SmartEditRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [SMART_EDIT_FEATURE]: {
          module: () => import('@spartacus/smartedit').then((m) => m.SmartEditModule),
        },
      },
    }),
  ],
})
export class SmartEditFeatureModule {}
