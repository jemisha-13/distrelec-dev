import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ResponsiveContentPageWithNavigation: {
          module: () => import('./responsive-with-nav.module').then((m) => m.ResponsiveWithNavModule),
          component: () => import('./responsive-with-nav.component').then((m) => m.ResponsiveWithNavComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        ResponsiveContentPageWithNavigation: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class ResponsiveWithNavFeatureModule {}
