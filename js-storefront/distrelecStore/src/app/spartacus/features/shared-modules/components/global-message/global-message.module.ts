import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { GlobalMessageComponent } from './global-message.component';
import { ActionMessageModule } from '@features/shared-modules/action-message/action-message.module';

@NgModule({
  imports: [CommonModule, IconModule, I18nModule, ActionMessageModule],
  declarations: [GlobalMessageComponent],
  exports: [GlobalMessageComponent],
})
export class GlobalMessageComponentModule {}
