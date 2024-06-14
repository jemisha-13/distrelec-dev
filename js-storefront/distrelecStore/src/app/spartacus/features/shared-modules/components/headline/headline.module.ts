import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DistHeadlineComponent } from './headline.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { CmsConfig, provideDefaultConfig } from '@spartacus/core';

@NgModule({
  imports: [CommonModule, ParseHtmlPipeModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        DistHeadlineComponent: {
          component: DistHeadlineComponent,
        },
      },
    }),
  ],
  declarations: [DistHeadlineComponent],
  exports: [DistHeadlineComponent],
})
export class DistHeadlineModule {}
