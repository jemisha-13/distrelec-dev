import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistProductCardComponent } from './product-card.component';
import { RouterModule } from '@angular/router';
import { ProductNumberModule } from '@design-system/product-number/product-number.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { DistLabelComponentModule } from '@design-system/label/label.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    ProductNumberModule,
    DistButtonComponentModule,
    DistIconModule,
    SinglePriceComponentModule,
    TooltipComponentModule,
    DistLabelComponentModule,
    AtcButtonModule,
    NumericStepperComponentModule,
  ],
  declarations: [DistProductCardComponent],
  exports: [DistProductCardComponent],
})
export class DistProductCardModule {}
