import { NgModule } from '@angular/core';
import { MinOrderQuantityPopupComponent } from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.component';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [MinOrderQuantityPopupComponent],
  exports: [MinOrderQuantityPopupComponent],
  imports: [I18nModule],
})
export class MinOrderQuantityPopupModule {}
