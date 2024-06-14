import { NgModule } from '@angular/core';
import { BrToNewlinePipe } from '@features/shared-modules/pipes/br-to-newline.pipe';

@NgModule({
  declarations: [BrToNewlinePipe],
  exports: [BrToNewlinePipe],
})
export class BrToNewlinePipeModule {}
