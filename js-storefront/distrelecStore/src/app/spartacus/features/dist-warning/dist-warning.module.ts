import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistWarningComponent } from './dist-warning.component';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

@NgModule({
  imports: [
    CommonModule,
    DistIconModule,
    I18nModule,
    ConfigModule.forRoot({
      cmsComponents: {
        DistWarningComponent: {
          component: DistWarningComponent,
        },
      },
    } as CmsConfig),
  ],
  declarations: [DistWarningComponent],
})
export class DistWarningModule {}
