import { NgModule } from '@angular/core';
import { SinglePriceComponent } from './single-price.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';

@NgModule({
  imports: [FontAwesomeModule, CommonModule, I18nModule, DecimalPlacesPipeModule, VolumePricePipeModule],
  declarations: [SinglePriceComponent],
  exports: [SinglePriceComponent],
})
export class SinglePriceComponentModule {}
