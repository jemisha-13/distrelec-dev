import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';
import { ResponsiveContentPageLayoutModule } from '@features/pages/content/responsive-content-page-without-nav-full-width/responsive-without-nav-layout';

@NgModule({
  imports: [CommonModule, ResponsiveContentPageLayoutModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ResponsiveContentPageFullWidthWithoutNavigation: {
          module: () => import('./responsive-without-nav.module').then((m) => m.ResponsiveWithoutNavFullWidthModule),
          component: () => import('./responsive-without-nav.component').then((m) => m.ResponsiveContentPageComponent),
          lazy: true,
        },
      },
    } as TemplateConfig),
  ],
})
export class ResponsiveWithoutNavFullWidthFeatureModule {}
