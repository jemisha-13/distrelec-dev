import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalibratedBannerComponent } from './calibrated-banner.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { I18nModule } from '@spartacus/core';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';

@NgModule({
  imports: [CommonModule, FontAwesomeModule, RouterModule, I18nModule, AlertBannerComponentModule],
  declarations: [CalibratedBannerComponent],
  exports: [CalibratedBannerComponent],
})
export class CalibratedBannerModule {}
