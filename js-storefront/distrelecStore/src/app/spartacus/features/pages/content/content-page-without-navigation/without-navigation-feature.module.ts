import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ContentPageWithoutNavigationTemplate: {
          module: () => import('./without-navigation.module').then((m) => m.WithoutNavigationModule),
          component: () => import('./without-navigation.component').then((m) => m.WithoutNavigationComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        ContentPageWithoutNavigationTemplate: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class WithoutNavigationFeatureModule {}
