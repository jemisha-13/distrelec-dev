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
        OfflineAddressChangePageTemplate: {
          module: () => import('./change-address.module').then((m) => m.ChangeAddressModule),
          component: () => import('./change-address.component').then((m) => m.ChangeAddressComponent),
          lazy: true,
        },
      },
      layoutSlots: {
        OfflineAddressChangePageTemplate: {
          slots: [], // Empty slots to prevent CSR layout shifts while template is lazy-loading
        },
      },
    } as TemplateConfig & LayoutConfig),
  ],
})
export class ChangeAddressFeatureModule {}
