import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { DistBannerModule } from './dist-banner/dist-banner.module';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ParseJsonPipeModule } from '@pipes/parse-json-pipe.module';
import { RouterModule } from '@angular/router';
import { StripHTMLTagsPipesModule } from '../pipes/strip-sup-pipes.module';
import { AbsoluteRouterLinkModule } from '@features/shared-modules/directives/absolute-router-link.module';
import { SimpleBannerModule } from '@features/shared-modules/banners/simple-banner/simple-banner.module';

@NgModule({
  imports: [
    CommonModule,
    DistBannerModule,
    CommonModule,
    FontAwesomeModule,
    ParseJsonPipeModule,
    RouterModule,
    I18nModule,
    StripHTMLTagsPipesModule,
    AbsoluteRouterLinkModule,
    SimpleBannerModule,
  ],
  exports: [DistBannerModule, SimpleBannerModule],
})
export class DistrelecBannerModule {}
