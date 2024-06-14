import { NgModule } from '@angular/core';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { CommunicationPrefPopupComponent } from '@features/shared-modules/popups/communication-pref-popup/communication-pref-popup.component';
import { I18nModule } from '@spartacus/core';
import { DistButtonComponentModule } from '@design-system/button/button.module';

@NgModule({
  declarations: [CommunicationPrefPopupComponent],
  exports: [CommunicationPrefPopupComponent],
  imports: [I18nModule, CheckboxModule, DistButtonComponentModule],
})
export class CommunicationPrefPopupComponentModule {}
