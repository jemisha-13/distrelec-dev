import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PageSlotModule } from '@spartacus/storefront';
import { PageManufacturerComponent } from './page-manufacturer.component';
import { ContentManufacturerLayoutModule } from './page-manufacturer-layout';

@NgModule({
  declarations: [PageManufacturerComponent],
  imports: [CommonModule, RouterModule, PageSlotModule, ContentManufacturerLayoutModule],
  exports: [PageManufacturerComponent],
})
export class ContentPageManufacturerModule {}
