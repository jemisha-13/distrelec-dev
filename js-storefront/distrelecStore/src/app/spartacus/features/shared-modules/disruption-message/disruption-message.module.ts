import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { DisruptionMessageComponent } from './disruption-message.component';
import { ParseHtmlPipeModule } from 'src/app/spartacus/pipes/parse-html-pipe.module';

@NgModule({
  declarations: [DisruptionMessageComponent],
  imports: [
    CommonModule,
    ConfigModule.withConfig({
      cmsComponents: {
        DistWarningComponent: {
          component: DisruptionMessageComponent,
        },
      },
    } as CmsConfig),
    ParseHtmlPipeModule,
  ],
  exports: [DisruptionMessageComponent],
})
export class DisruptionMessageModule {}
