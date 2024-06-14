import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WithNavigationComponent } from './with-navigation.component';
import { WithNavigationLayoutModule } from './with-navigation-layout';
import { ParseHtmlPipeModule } from 'src/app/spartacus/pipes/parse-html-pipe.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { RouterModule } from '@angular/router';
import { PageComponentModule, PageSlotModule } from '@spartacus/storefront';
import { DisruptionMessageModule } from '@features/shared-modules/disruption-message/disruption-message.module';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [WithNavigationComponent],
  imports: [
    CommonModule,
    WithNavigationLayoutModule,
    ParseHtmlPipeModule,
    FontAwesomeModule,
    BreadcrumbWrapperModule,
    RouterModule,
    PageComponentModule,
    DisruptionMessageModule,
    I18nModule,
    PageSlotModule,
  ],
  exports: [WithNavigationComponent, WithNavigationLayoutModule],
})
export class WithNavigationModule {}
