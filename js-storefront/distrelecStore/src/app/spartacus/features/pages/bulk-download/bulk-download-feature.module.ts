import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  providers: [
    provideConfig({
      featureModules: {
        bulkDownload: {
          module: () => import('./bulk-download.module').then((m) => m.BulkDownloadModule),
          cmsComponents: ['QualityAndLegalComponent'],
        },
      },
    } as CmsConfig),
  ],
})
export class BulkDownloadFeatureModule {}
