import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToolbarItemComponent } from '@features/shared-modules/components/toolbar-item/toolbar-item.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';

@NgModule({
  imports: [FontAwesomeModule, I18nModule, CommonModule],
  declarations: [ToolbarItemComponent],
  exports: [ToolbarItemComponent],
})
export class ToolbarItemComponentModule {}
