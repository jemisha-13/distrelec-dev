import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpErrorHandler, provideConfig } from '@spartacus/core';

import { BomToolErrorHandler } from '@features/pages/bom-tool/bom-tool-error.handler';
import { LayoutConfig } from '@spartacus/storefront';

const bomToolPageLayoutConfig: LayoutConfig = {
  layoutSlots: {
    ImportToolPageTemplate: {
      slots: ['BomHelp'],
    },
  },
};

const bomToolLazyLoadRouteConfig: Routes = [
  {
    path: 'import-tool',
    redirectTo: 'bom-tool',
  },
  {
    path: 'bom-tool',
    loadChildren: () => import('@features/pages/bom-tool/bom-tool.module').then((m) => m.BomToolModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(bomToolLazyLoadRouteConfig)],
  providers: [
    provideConfig(bomToolPageLayoutConfig),
    {
      provide: HttpErrorHandler,
      useExisting: BomToolErrorHandler,
      multi: true,
    },
  ],
})
export class BomToolFeatureModule {}
