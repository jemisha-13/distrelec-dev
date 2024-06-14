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
        CMS3PageTemplate: {
          slots: [''],
        },
      },
    } as LayoutConfig),
  ],
})
export class PageCms3LayoutModule {}
