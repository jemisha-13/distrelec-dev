import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResponsiveWithoutNavComponent } from './responsive-without-nav.component';
import { ResponsiveWithoutNavLayoutModule } from './responsive-without-nav-layout';
import { SharedFeaturedProductsModule } from '@features/shared-modules/featured-products/featured-products.module';
import { PageSlotModule } from '@spartacus/storefront';

@NgModule({
  declarations: [ResponsiveWithoutNavComponent],
  imports: [CommonModule, ResponsiveWithoutNavLayoutModule, SharedFeaturedProductsModule, PageSlotModule],
  exports: [ResponsiveWithoutNavComponent, ResponsiveWithoutNavLayoutModule],
})
export class ResponsiveWithoutNavModule {}
