import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutSlots: {
        ContentPageWithoutNavigationTemplate: {
          slots: ['Content'],
        },
      },
    } as LayoutConfig),
  ],
})
export class WithoutNavigationLayoutModule {}
