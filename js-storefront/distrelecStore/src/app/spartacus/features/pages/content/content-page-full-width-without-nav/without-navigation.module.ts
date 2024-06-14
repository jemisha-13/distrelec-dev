import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FullWidthWithoutNavigationComponent } from './without-navigation.component';
import { FullWidthWithoutNavLayoutModule } from './without-navigation-layout';
import { PageSlotModule } from '@spartacus/storefront';

@NgModule({
  declarations: [FullWidthWithoutNavigationComponent],
  imports: [CommonModule, FullWidthWithoutNavLayoutModule, PageSlotModule],
  exports: [FullWidthWithoutNavigationComponent, FullWidthWithoutNavLayoutModule],
})
export class FullWidthWithoutNavigationModule {}
