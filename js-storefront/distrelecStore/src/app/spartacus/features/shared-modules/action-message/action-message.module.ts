import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule } from '@spartacus/core';
import { ActionMessageComponent } from './action-message.component';
import { ParseHtmlPipeModule } from 'src/app/spartacus/pipes/parse-html-pipe.module';

@NgModule({
  declarations: [ActionMessageComponent],
  imports: [
    CommonModule,
    ConfigModule.withConfig({
      cmsComponents: {
        DistWarningComponent: {
          component: ActionMessageComponent,
        },
      },
    } as CmsConfig),
    ParseHtmlPipeModule,
  ],
  exports: [ActionMessageComponent],
})
export class ActionMessageModule {}
