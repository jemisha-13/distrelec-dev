import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AddToCartComponent } from '../../common/add-to-cart/add-to-cart.component';

import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, FontAwesomeModule, AtcButtonModule, NumericStepperComponentModule, I18nModule],
  declarations: [AddToCartComponent],
  exports: [AddToCartComponent],
})
export class AddToCartModule {}
