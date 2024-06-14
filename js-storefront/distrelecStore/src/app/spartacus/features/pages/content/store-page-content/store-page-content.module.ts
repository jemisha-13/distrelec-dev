import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParseHtmlPipeModule } from 'src/app/spartacus/pipes/parse-html-pipe.module';
import { StorePageContentComponent } from './store-page-content.component';
import { StorePageContentLayoutModule } from './store-page-content-layout';
import { OutletModule, PageComponentModule, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { DisruptionMessageModule } from '@features/shared-modules/disruption-message/disruption-message.module';
import { PageTitleModule } from '@features/shared-modules/components/page-title/page-title.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [StorePageContentComponent],
  imports: [
    CommonModule,
    StorePageContentLayoutModule,
    ParseHtmlPipeModule,
    PageComponentModule,
    DisruptionMessageModule,
    PageLayoutModule,
    OutletModule,
    PageSlotModule,
    PageTitleModule,
    I18nModule,
  ],
  exports: [StorePageContentComponent, StorePageContentLayoutModule],
})
export class StorePageContentModule {}
