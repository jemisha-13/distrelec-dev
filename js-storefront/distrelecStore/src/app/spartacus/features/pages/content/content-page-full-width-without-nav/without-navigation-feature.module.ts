import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { TemplateConfig } from '@features/pages/page-layout/template-config';
import { FullWidthWithoutNavLayoutModule } from './without-navigation-layout';

@NgModule({
  imports: [CommonModule, FullWidthWithoutNavLayoutModule],
  providers: [
    provideConfig({
      layoutTemplates: {
        ContentPageFullWidthWithoutNavigationTemplate: {
          module: () => import('./without-navigation.module').then((m) => m.FullWidthWithoutNavigationModule),
          component: () => import('./without-navigation.component').then((m) => m.FullWidthWithoutNavigationComponent),
          lazy: true,
        },
      },
    } as TemplateConfig),
  ],
})
export class FullWidthWithoutNavigationFeatureModule {}
