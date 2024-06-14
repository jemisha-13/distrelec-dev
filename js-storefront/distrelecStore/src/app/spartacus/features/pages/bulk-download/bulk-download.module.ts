import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BulkDownloadComponent } from './bulk-download.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { QualityLegalGuard } from '@features/guards/quality-legal.guard';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';

@NgModule({
  declarations: [BulkDownloadComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    FormsModule,
    I18nModule,
    ReactiveFormsModule,
    ComponentLoadingSpinnerModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        QualityAndLegalComponent: {
          component: BulkDownloadComponent,
          guards: [QualityLegalGuard],
        },
      },
    } as CmsConfig),
  ],
})
export class BulkDownloadModule {}
