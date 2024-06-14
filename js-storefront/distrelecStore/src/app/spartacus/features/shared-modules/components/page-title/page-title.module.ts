import { NgModule } from '@angular/core';
import { PageTitleComponent } from './page-title.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [CommonModule, I18nModule, FontAwesomeModule],
  declarations: [PageTitleComponent],
  exports: [PageTitleComponent],
})
export class PageTitleModule {}
