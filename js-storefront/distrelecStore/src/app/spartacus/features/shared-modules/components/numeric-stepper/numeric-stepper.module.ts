import { NgModule } from '@angular/core';
import { NumericStepperComponent } from './numeric-stepper.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { CommonModule } from '@angular/common';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { I18nModule } from '@spartacus/core';
import { QuantitySelectorComponentModule } from '@design-system/quantity-selector/quantity-selector.module';

@NgModule({
  imports: [
    FontAwesomeModule,
    I18nModule,
    MinOrderQuantityPopupModule,
    CommonModule,
    QuantitySelectorComponentModule,
    TooltipComponentModule,
  ],
  declarations: [NumericStepperComponent],
  exports: [NumericStepperComponent],
})
export class NumericStepperComponentModule {}
