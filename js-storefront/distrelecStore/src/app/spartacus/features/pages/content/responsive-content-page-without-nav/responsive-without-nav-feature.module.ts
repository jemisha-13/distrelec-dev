import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutConfig } from '@spartacus/storefront';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ResponsiveContentPageWithoutNavigation: {
          module: () => import('./responsive-without-nav.module').then((m) => m.ResponsiveWithoutNavModule),
          component: () => import('./responsive-without-nav.component').then((m) => m.ResponsiveWithoutNavComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        ResponsiveContentPageWithoutNavigation: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class ResponsiveWithoutNavFeatureModule {}
