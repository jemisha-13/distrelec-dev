import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        SitemapTemplate: {
          module: () => import('./sitemap.module').then((m) => m.SitemapModule),
          component: () => import('./sitemap.component').then((m) => m.SitemapComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        SitemapTemplate: {
          slots: [],
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class SitemapFeatureModule {}
