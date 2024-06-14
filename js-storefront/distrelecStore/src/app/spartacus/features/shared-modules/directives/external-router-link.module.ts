import { NgModule } from '@angular/core';
import { ExternalRouterLinkDirective } from '@features/shared-modules/directives/external-router-link.directive';

@NgModule({
  declarations: [ExternalRouterLinkDirective],
  exports: [ExternalRouterLinkDirective],
})
export class ExternalRouterLinkModule {}
