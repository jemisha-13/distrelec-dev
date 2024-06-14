import { NgModule } from '@angular/core';
import { PricePipe } from '@features/shared-modules/pipes/price.pipe';

@NgModule({
  imports: [],
  declarations: [PricePipe],
  exports: [PricePipe],
})
export class PricePipeModule {}
