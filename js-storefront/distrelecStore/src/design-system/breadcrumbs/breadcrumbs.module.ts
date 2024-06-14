import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BreadcrumbsComponent } from './breadcrumbs.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';

@NgModule({
  imports: [CommonModule, DistIconModule, RouterModule, SharedModule],
  declarations: [BreadcrumbsComponent],
  exports: [BreadcrumbsComponent],
})
export class BreadcrumbsModule {}
