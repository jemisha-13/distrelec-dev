import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { ConfigModule, CmsConfig, I18nModule } from '@spartacus/core';
import { SharedModule } from '@features/shared-modules/shared.module';
import { ProgressBarComponent } from './progress-bar.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  declarations: [ProgressBarComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    RouterModule,
    I18nModule,
    SharedModule,
    ConfigModule.forRoot({
      cmsComponents: {
        HeaderCheckoutPageComponent: {
          component: ProgressBarComponent,
        },
      },
    } as CmsConfig),
    DistIconModule,
  ],
  exports: [ProgressBarComponent],
})
export class ProgressBarModule {}
