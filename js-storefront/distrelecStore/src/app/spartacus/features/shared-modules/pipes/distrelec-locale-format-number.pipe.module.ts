import { NgModule } from '@angular/core';
import { DistrelecLocaleFormatNumberPipe } from '@features/shared-modules/pipes/distrelec-locale-format-number.pipe';

@NgModule({
  declarations: [DistrelecLocaleFormatNumberPipe],
  exports: [DistrelecLocaleFormatNumberPipe],
})
export class DistrelecLocaleFormatNumberPipeModule {}
