import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageSlotModule } from '@spartacus/storefront';
import { WithoutNavigationComponent } from './without-navigation.component';
import { WithoutNavigationLayoutModule } from './without-navigation-layout';

@NgModule({
  declarations: [WithoutNavigationComponent],
  imports: [CommonModule, WithoutNavigationLayoutModule, PageSlotModule],
  exports: [WithoutNavigationComponent, WithoutNavigationLayoutModule],
})
export class WithoutNavigationModule {}
