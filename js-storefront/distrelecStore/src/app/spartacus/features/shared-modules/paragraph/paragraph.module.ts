import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { SupplementHashAnchorsModule } from '@spartacus/storefront';

import { ParagraphComponent } from './paragraph.component';
import { ParseHtmlPipeModule } from '@pipes/parse-html-pipe.module';
import { UrlHrefPipeModule } from '@pipes/url-href-pipe.module';

@NgModule({
  imports: [CommonModule, ParseHtmlPipeModule, SupplementHashAnchorsModule, UrlHrefPipeModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CMSParagraphComponent: {
          component: ParagraphComponent,
        },
        CMSTabParagraphComponent: {
          component: ParagraphComponent,
        },
      },
    }),
  ],
  declarations: [ParagraphComponent],
  exports: [ParagraphComponent],
})
export class DistrelecCmsParagraphModule {}
