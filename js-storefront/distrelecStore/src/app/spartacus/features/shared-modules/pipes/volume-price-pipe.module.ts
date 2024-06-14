import { NgModule } from '@angular/core';
import { VolumePricePipe } from '@features/shared-modules/pipes/volume-price.pipe';

@NgModule({
  declarations: [VolumePricePipe],
  exports: [VolumePricePipe],
})
export class VolumePricePipeModule {}
