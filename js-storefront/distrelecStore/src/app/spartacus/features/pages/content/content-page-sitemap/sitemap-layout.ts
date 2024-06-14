import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { LayoutConfig } from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [
    provideConfig({
      layoutSlots: {
        SitemapTemplate: {
          slots: ['Content'],
        },
      },
    } as LayoutConfig),
  ],
})
export class SitemapLayoutModule {}
