import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutSlots: {
        ContentPageFullWidthWithoutNavigationTemplate: {
          slots: ['Content'],
        },
      },
    } as LayoutConfig),
  ],
})
export class FullWidthWithoutNavLayoutModule {}
