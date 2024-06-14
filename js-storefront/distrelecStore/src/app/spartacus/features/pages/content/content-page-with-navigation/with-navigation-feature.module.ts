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
        ContentPageWithNavigationTemplate: {
          module: () => import('./with-navigation.module').then((m) => m.WithNavigationModule),
          component: () => import('./with-navigation.component').then((m) => m.WithNavigationComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        ContentPageWithNavigationTemplate: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class WithNavigationFeatureModule {}
