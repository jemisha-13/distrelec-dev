import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SlideDrawerComponent } from './slide-drawer.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { DistButtonModule } from '../../app/shared-components/dist-button/dist-button.module';
import { I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistScrollBarModule } from '@design-system/scroll-bar/scroll-bar.module';

@NgModule({
  declarations: [SlideDrawerComponent],
  imports: [CommonModule, FontAwesomeModule, DistButtonModule, I18nModule, DistIconModule, DistScrollBarModule],
  exports: [SlideDrawerComponent],
})
export class SlideDrawerModule {}
