import { NgModule } from '@angular/core';
import { StickyAddToCartComponent } from './sticky-add-to-cart.component';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { PricesComponentModule } from '@design-system/prices/prices.module';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    DistButtonComponentModule,
    DistIconModule,
    PricesComponentModule,
    VolumePricePipeModule,
    DecimalPlacesPipeModule,
    NumericStepperComponentModule,
    AtcButtonModule,
  ],
  declarations: [StickyAddToCartComponent],
  exports: [StickyAddToCartComponent],
})
export class StickyAddToCartComponentModule {}
