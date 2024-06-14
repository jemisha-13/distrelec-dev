import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { StandaloneWarningComponent } from './standalone-warning.component';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        RSIntegrationMessageComponent: {
          component: StandaloneWarningComponent,
        },
      },
    } as CmsConfig),
  ],
  declarations: [StandaloneWarningComponent],
})
export class StandAloneWarningModule {}
