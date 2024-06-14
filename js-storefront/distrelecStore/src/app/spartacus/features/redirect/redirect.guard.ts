import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { RedirectService } from '@features/redirect/redirect.service';
import { WindowRef } from '@spartacus/core';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class RedirectGuard  {
  constructor(
    private redirectService: RedirectService,
    private router: Router,
    private window: WindowRef,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.redirectService.getRedirectUrl(this.getFullUrl(route)).pipe(
      map((redirectUrl) => {
        if (redirectUrl) {
          if (this.redirectService.isInternalRedirect(redirectUrl)) {
            return this.router.createUrlTree([redirectUrl], {
              queryParams: route.queryParams,
              fragment: route.fragment,
            });
          }

          this.window.location.href = redirectUrl;
          return false;
        }

        return true;
      }),
    );
  }

  getFullUrl(routeSnapshot: ActivatedRouteSnapshot): string {
    return routeSnapshot.url.map((segment) => segment.path).join('/');
  }
}
