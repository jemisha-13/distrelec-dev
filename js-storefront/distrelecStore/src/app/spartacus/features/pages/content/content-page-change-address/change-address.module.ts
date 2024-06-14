import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChangeAddressComponent } from './change-address.component';
import { ChangeAddressLayoutModule } from './change-address-layout';
import { PageSlotModule } from '@spartacus/storefront';
import { OfflineChangeAddressModule } from '@features/shared-modules/offline-change-address/offline-change-address.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, ChangeAddressLayoutModule, OfflineChangeAddressModule, PageSlotModule, I18nModule],
  exports: [ChangeAddressComponent, ChangeAddressLayoutModule],
  declarations: [ChangeAddressComponent],
})
export class ChangeAddressModule {}
