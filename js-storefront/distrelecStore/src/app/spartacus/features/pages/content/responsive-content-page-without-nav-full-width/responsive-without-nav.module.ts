import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResponsiveContentPageComponent } from './responsive-without-nav.component';
import { ResponsiveContentPageLayoutModule } from './responsive-without-nav-layout';
import { PageSlotModule } from '@spartacus/storefront';

@NgModule({
  declarations: [ResponsiveContentPageComponent],
  imports: [CommonModule, ResponsiveContentPageLayoutModule, PageSlotModule],
  exports: [ResponsiveContentPageComponent, ResponsiveContentPageLayoutModule],
})
export class ResponsiveWithoutNavFullWidthModule {}
