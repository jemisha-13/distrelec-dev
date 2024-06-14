import { NgModule } from '@angular/core';
import { ProductListPageComponent } from '@features/pages/product/plp/page/product-list-page.component';
import { OutletModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { CommonModule } from '@angular/common';
import { ProductListModule } from '@features/pages/product/plp/product-list/product-list.module';
import { SkeletonLoaderModule } from '@features/shared-modules/skeleton-loader/skeleton-loader.module';

@NgModule({
  declarations: [ProductListPageComponent],
  imports: [PageLayoutModule, OutletModule, PageSlotModule, CommonModule, ProductListModule, SkeletonLoaderModule],
  exports: [ProductListPageComponent],
})
export class ProductListPageTemplateModule {}
