import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ParseJsonPipeModule } from 'src/app/spartacus/pipes/parse-json-pipe.module';
import { RouterModule } from '@angular/router';
import { AddToCartFFComponent } from './search-suggestion-add-to-cart.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { MinOrderQuantityPopupModule } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.module';
import { AtcButtonModule } from '@features/shared-modules/components/atc-button/atc-button.module';
import { LegacyQuantitySelectorComponentModule } from '@design-system/quantity-selector-legacy/quantity-selector-legacy.module';

@NgModule({
  declarations: [AddToCartFFComponent],
  imports: [
    CommonModule,
    FormsModule,
    FontAwesomeModule,
    I18nModule,
    ParseJsonPipeModule,
    RouterModule,
    I18nModule,
    SharedModule,
    MinOrderQuantityPopupModule,
    AtcButtonModule,
    LegacyQuantitySelectorComponentModule
  ],
  exports: [FontAwesomeModule, AddToCartFFComponent],
})
export class QtyAddToCartModule {}
