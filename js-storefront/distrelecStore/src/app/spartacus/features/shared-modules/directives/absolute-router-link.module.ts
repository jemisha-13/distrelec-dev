import { NgModule } from '@angular/core';
import { AbsoluteRouterLinkDirective } from '@features/shared-modules/directives/absolute-router-link.directive';

@NgModule({
  declarations: [AbsoluteRouterLinkDirective],
  exports: [AbsoluteRouterLinkDirective],
})
export class AbsoluteRouterLinkModule {}
