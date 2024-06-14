import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { AuthRedirectService, AuthService, SemanticPathService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users.
 * Passes the query parameters that were used in the targeted url
 */
@Injectable({
  providedIn: 'root',
})
export class AuthGuardWithParams  {
  constructor(
    protected authService: AuthService,
    protected authRedirectService: AuthRedirectService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().pipe(
      map((isLoggedIn) => {
        if (!isLoggedIn) {
          this.authRedirectService.saveCurrentNavigationUrl();
          const urlTree = this.router.parseUrl(this.semanticPathService.get('login'));
          if (route.queryParams) {
            urlTree.queryParams = route.queryParams;
          }
          return urlTree;
        }
        return isLoggedIn;
      }),
    );
  }
}
