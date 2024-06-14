import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StorePageContentLayoutModule } from './store-page-content-layout';
import { LayoutConfig } from '@spartacus/storefront';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@NgModule({
  imports: [CommonModule, StorePageContentLayoutModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        StorePageTemplate: {
          module: () => import('./store-page-content.module').then((m) => m.StorePageContentModule),
          component: () => import('./store-page-content.component').then((m) => m.StorePageContentComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        StorePageTemplate: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class StorePageContentFeatureModule {}
