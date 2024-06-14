import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductListModule } from '@features/pages/product/plp/product-list/product-list.module';
import { PlpLayoutModule } from '@features/pages/product/plp/layout-config/plp-layout.module';

@NgModule({
  imports: [CommonModule, PlpLayoutModule, ProductListModule],
})
export class PlpModule {}
