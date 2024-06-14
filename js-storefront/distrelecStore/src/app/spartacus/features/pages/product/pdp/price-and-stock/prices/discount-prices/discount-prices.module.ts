import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DiscountPricesComponent } from './discount-prices.component';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [DiscountPricesComponent],
  imports: [CommonModule, DecimalPlacesPipeModule, VolumePricePipeModule, I18nModule],
  exports: [DiscountPricesComponent],
})
export class DiscountPricesModule {}
