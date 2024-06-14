import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductStatusBadgeComponent } from './product-status-badge.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [CommonModule, DistIconModule],
  declarations: [ProductStatusBadgeComponent],
  exports: [ProductStatusBadgeComponent],
})
export class DistProductStatusBadgeComponentModule {}
