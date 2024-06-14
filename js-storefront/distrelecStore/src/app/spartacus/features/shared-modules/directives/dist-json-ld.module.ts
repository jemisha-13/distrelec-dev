import { NgModule } from '@angular/core';
import { DistJsonLdDirective } from '@features/shared-modules/directives/dist-json-ld.directive';
import { StructuredDataModule } from '@spartacus/storefront';

@NgModule({
  imports: [StructuredDataModule],
  declarations: [DistJsonLdDirective],
  exports: [DistJsonLdDirective, StructuredDataModule],
})
export class DistJsonLdModule {}
