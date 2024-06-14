import { ActivatedRouteSnapshot, BaseRouteReuseStrategy, RouteReuseStrategy } from '@angular/router';

export class CustomRouteReuseStrategy extends BaseRouteReuseStrategy implements RouteReuseStrategy {
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    // There are issues with components not re-rendering when the route is reused.
    // default implementation: return future.routeConfig === curr.routeConfig;
    return false;
  }
}
