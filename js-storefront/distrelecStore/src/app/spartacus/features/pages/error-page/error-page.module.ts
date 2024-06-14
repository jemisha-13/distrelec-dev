import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { I18nModule, provideConfig } from '@spartacus/core';
import { LayoutConfig, provideCmsStructure } from '@spartacus/storefront';

import { ErrorPageComponent } from './error-page.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [ErrorPageComponent],
  imports: [CommonModule, I18nModule, RouterModule, DistIconModule],
  providers: [
    provideConfig({
      layoutSlots: {
        ErrorPageTemplate: {
          slots: ['ErrorPageContentSlot'],
        },
      },
      cmsComponents: {
        ErrorPageContent: {
          component: ErrorPageComponent,
        },
      },
    } as LayoutConfig),
    provideCmsStructure({
      pageSlotPosition: 'ErrorPageContentSlot',
      componentId: 'ErrorPageContent',
    }),
  ],
})
export class ErrorPageModule {}
