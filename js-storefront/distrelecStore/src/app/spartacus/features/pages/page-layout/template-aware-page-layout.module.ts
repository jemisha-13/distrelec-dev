import { APP_INITIALIZER, NgModule } from '@angular/core';
import { TemplateAwarePageLayoutComponent } from './template-aware-page-layout.component';
import { Router } from '@angular/router';
import { OutletModule, PageLayoutComponent, PageLayoutModule, PageSlotModule } from '@spartacus/storefront';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    CommonModule,
    OutletModule,
    PageLayoutModule,
    PageSlotModule
  ],
  declarations: [
    TemplateAwarePageLayoutComponent
  ],
  exports: [
    TemplateAwarePageLayoutComponent
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: (router: Router) => () => {
        router.resetConfig(router.config.map((route) => {
          if (route.component === PageLayoutComponent) {
            return {
              ...route,
              component: TemplateAwarePageLayoutComponent,
            };
          }
          return route;
        }))
      },
      deps: [Router],
      multi: true,
    }
  ]
})
export class TemplateAwarePageLayoutModule {}
