import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Params, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthRedirectService, AuthService, BaseSiteService, SemanticPathService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { SiteIdEnum } from '@model/site-settings.model';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users.
 */
@Injectable({
  providedIn: 'root',
})
export class MyAccountAuthGuard  {
  constructor(
    protected authService: AuthService,
    protected authRedirectService: AuthRedirectService,
    protected baseSiteService: BaseSiteService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    private distrelecUserService: DistrelecUserService,
  ) {}

  redirectToPage(page: string, queryParams?: Params): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    const urlTree = this.router.parseUrl(this.semanticPathService.get(page));
    if (queryParams) {
      urlTree.queryParams = queryParams;
    }
    return urlTree;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().pipe(
      switchMap((isLoggedIn) => {
        if (!isLoggedIn) {
          return of(this.redirectToPage('login', route.queryParams));
        } else {
          return this.distrelecUserService.getUserInformation().pipe(
            map((data: any) => {
              if (data?.customerType === 'B2E') {
                return this.redirectToPage('account');
              } else if (
                (data?.customerType === 'B2B' || data?.customerType === 'B2B_KEY_ACCOUNT') &&
                data?.roles &&
                route.routeConfig?.path
              ) {
                if (
                  (this.isB2BAdminApprovalPath(route.routeConfig.path) && this.isB2BCustomerOnly(data.roles)) ||
                  (this.isB2BCustomerApprovalPath(route.routeConfig.path) && data.roles.includes('b2badmingroup'))
                ) {
                  return this.redirectToPage('account');
                } else if (
                  data?.customersBaseSite === SiteIdEnum.FR &&
                  (this.isB2BAdminApprovalPath(route.routeConfig.path) ||
                    this.isB2BCustomerApprovalPath(route.routeConfig.path))
                ) {
                  this.router.navigateByUrl('/not-found', route.queryParams);
                  return false;
                }
              }
              return true;
            }),
          );
        }
      }),
    );
  }

  isB2BAdminApprovalPath(path: string): boolean {
    return path === 'order-approval' || path === 'order-approval/order-details/:orderCode/workflow/:workFlowCode';
  }

  isB2BCustomerOnly(roles: string[]): boolean {
    return roles.includes('b2bcustomergroup') && !roles.includes('b2badmingroup');
  }

  isB2BCustomerApprovalPath(path: string): boolean {
    return (
      path === 'order-approval-requests' ||
      path === 'order-approval-requests/order-details/:orderCode/workflow/:workFlowCode'
    );
  }
}
