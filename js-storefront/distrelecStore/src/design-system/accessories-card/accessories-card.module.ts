import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { DistAccessoriesCardComponent } from './accessories-card.component';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { LetModule } from '@rx-angular/template/let';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    DistButtonComponentModule,
    SinglePriceComponentModule,
    DistIconModule,
    RouterModule,
    LetModule,
    NumericStepperComponentModule,
    AtcButtonModule,
  ],
  declarations: [DistAccessoriesCardComponent],
  exports: [DistAccessoriesCardComponent],
})
export class DistAccessoriesCardModule {}
