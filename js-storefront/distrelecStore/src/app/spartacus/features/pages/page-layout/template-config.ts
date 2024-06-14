import { Injectable } from '@angular/core';
import { Config } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
  useExisting: Config,
})
export abstract class TemplateConfig extends Config {
  /**
   * To lazy-load the template module and component, provide functions that return a promise and set lazy to true.
   *
   * e.g.
   *
   * layoutTemplates: {
   *   ResponsiveContentPageWithNavigation: {
   *     module: () => import('./responsive-with-nav.module').then((m) => m.ResponsiveWithNavModule),
   *     component: () => import('./responsive-with-nav.component').then((m) => m.ResponsiveWithNavComponent),
   *     lazy: true,
   *   }
   * }
   *
   * To provide a static module and component, provide just the reference to the component directly.
   * e.g.
   *
   * layoutTemplates: {
   *   ResponsiveContentPageWithNavigation: {
   *     component: ResponsiveWithNavComponent,
   *   }
   * }
   */
  layoutTemplates?: {
    [templateName: string]: {
      module?: () => Promise<any>;
      component?: any | (() => Promise<any>);
      lazy?: boolean;
    }
  }
}
