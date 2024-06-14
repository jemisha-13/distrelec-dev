import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentManufacturerLayoutModule } from './page-manufacturer-layout';
import { RouterModule } from '@angular/router';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';

@NgModule({
  imports: [CommonModule, RouterModule, ContentManufacturerLayoutModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ManufacturerStoresPageTemplate: {
          module: () => import('./page-manufacturer.module').then((m) => m.ContentPageManufacturerModule),
          component: () => import('./page-manufacturer.component').then((m) => m.PageManufacturerComponent),
          lazy: true,
        },
      },
    } as TemplateConfig),
  ],
})
export class ContentPageManufacturerFeatureModule {}
