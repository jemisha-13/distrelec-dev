import { NgModule } from '@angular/core';
import { ScaledPricesComponent } from './scaled-prices.component';
import { CommonModule } from '@angular/common';
import { PricePipeModule } from '@features/shared-modules/pipes/price-pipe.module';

@NgModule({
  imports: [CommonModule, PricePipeModule],
  declarations: [ScaledPricesComponent],
  exports: [ScaledPricesComponent],
})
export class ScaledPricesComponentModule {}
