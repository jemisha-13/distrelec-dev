import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';

import { BomToolSavedEntriesComponent } from '@features/pages/bom-tool/bom-tool-saved-entries/bom-tool-saved-entries.component';
import { ActionMessageModule } from '@features/shared-modules/action-message/action-message.module';
import { SavedFileEntryComponent } from '@features/pages/bom-tool/bom-tool-saved-entries/saved-file-entry/saved-file-entry.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ButtonComponentModule } from '@features/shared-modules/components/button/button.module';
import { PageTitleModule } from '@features/shared-modules/components/page-title/page-title.module';
import { ConfirmPopupComponentModule } from '@features/shared-modules/popups/confirm-popup/confirm-popup.module';

@NgModule({
  declarations: [BomToolSavedEntriesComponent, SavedFileEntryComponent],
  imports: [
    ActionMessageModule,
    SharedModule,
    CommonModule,
    RouterModule,
    I18nModule,
    FormsModule,
    ButtonComponentModule,
    PageTitleModule,
    ConfirmPopupComponentModule,
  ],
  exports: [BomToolSavedEntriesComponent],
})
export class BomToolSavedEntriesModule {}
