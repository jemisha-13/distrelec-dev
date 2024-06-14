import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageSlotModule } from '@spartacus/storefront';

import { ResponsiveWithNavLayoutModule } from './responsive-with-nav-layout.module';
import { ResponsiveWithNavComponent } from './responsive-with-nav.component';

@NgModule({
  imports: [CommonModule, ResponsiveWithNavLayoutModule, PageSlotModule],
  declarations: [ResponsiveWithNavComponent],
  exports: [ResponsiveWithNavComponent],
})
export class ResponsiveWithNavModule {}
