import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageCms3LayoutModule } from './page-cms3-layout';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@NgModule({
  imports: [CommonModule, PageCms3LayoutModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        CMS3PageTemplate: {
          module: () => import('./page-cms3.module').then((m) => m.PageCms3Module),
          component: () => import('./page-cms3.component').then((m) => m.PageCms3Component),
          lazy: true,
        },
      },
    } as TemplateConfig),
  ],
})
export class PageCms3FeatureModule {}
