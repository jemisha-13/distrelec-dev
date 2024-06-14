import { Component, ModuleWithProviders, NgModule } from '@angular/core';
import { RouteReuseStrategy, RouterModule } from '@angular/router';
import { provideConfig, provideDefaultConfig, RoutingConfig, RoutingModule } from '@spartacus/core';
import { defaultRoutingConfig } from '@features/routing-config/default-routing-config';
import { DocumentRedirectGuard } from '@features/guards/document-redirect.guard';
import { CustomRouteReuseStrategy } from '@features/routing-config/custom-route-reuse-strategy';

@Component({ template: `` })
class BlankComponent {}

@NgModule({
  declarations: [BlankComponent],
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'compliance-document',
          pathMatch: 'prefix',
          children: [
            {
              path: '**',
              canActivate: [DocumentRedirectGuard],
              component: BlankComponent,
            },
          ],
        },
        {
          path: 'medias',
          pathMatch: 'prefix',
          children: [
            {
              path: '**',
              canActivate: [DocumentRedirectGuard],
              component: BlankComponent,
            },
          ],
        },
      ],
      {
        useHash: false,
        anchorScrolling: 'enabled',
        initialNavigation: 'enabledBlocking',
        scrollPositionRestoration: 'enabled',
      },
    ),
  ],
  providers: [{ provide: RouteReuseStrategy, useClass: CustomRouteReuseStrategy }],
})
export class AppRoutingModule {
  static forRoot(): ModuleWithProviders<RoutingModule> {
    return {
      ngModule: RoutingModule,
      providers: [
        provideDefaultConfig(defaultRoutingConfig),
        provideConfig({
          routing: {
            loadStrategy: 'once',
          },
        } as RoutingConfig),
      ],
    };
  }
}
