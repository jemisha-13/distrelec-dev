import { NgModule } from '@angular/core';
import { PricesComponent } from './prices.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { SinglePriceComponentModule } from '@design-system/single-price/single-price.module';

@NgModule({
  imports: [FontAwesomeModule, CommonModule, I18nModule, SinglePriceComponentModule],
  declarations: [PricesComponent],
  exports: [PricesComponent],
})
export class PricesComponentModule {}
