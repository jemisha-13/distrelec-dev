import { NgModule } from '@angular/core';
import { MagnifyImageDirective } from '@features/shared-modules/directives/magnify-image.directive';

@NgModule({
  declarations: [MagnifyImageDirective],
  exports: [MagnifyImageDirective],
})
export class MagnifyImageDirectiveModule {}
