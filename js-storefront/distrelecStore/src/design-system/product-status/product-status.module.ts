import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductStatusComponent } from './product-status.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, DistIconModule],
  declarations: [ProductStatusComponent],
  exports: [ProductStatusComponent],
})
export class DistProductStatusComponentModule {}
