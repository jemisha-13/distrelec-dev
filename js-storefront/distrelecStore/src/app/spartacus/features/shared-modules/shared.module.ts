import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';

import { ActionMessageModule } from '@features/shared-modules/action-message/action-message.module';
import { ComponentLoadingSpinnerModule } from '@features/shared-modules/component-loading-spinner/component-loading-spinner.module';
import { LoginModalModule } from '@features/shared-modules/popups/login-modal/login-modal.module';

import { GlobalMessageComponentModule } from '@features/shared-modules/components/global-message/global-message.module';
import { AbsoluteRouterLinkModule } from '@features/shared-modules/directives/absolute-router-link.module';
import { ExternalRouterLinkModule } from '@features/shared-modules/directives/external-router-link.module';
import { SharedRxModule } from '@features/shared-modules/shared-rx.module';

@NgModule({
  imports: [LoginModalModule],
  exports: [
    // Modules
    I18nModule,
    FontAwesomeModule,
    ActionMessageModule,
    SharedRxModule,

    // Directives
    AbsoluteRouterLinkModule,
    ExternalRouterLinkModule,

    // Components
    GlobalMessageComponentModule,
    ComponentLoadingSpinnerModule,
  ],
})
export class SharedModule {}
