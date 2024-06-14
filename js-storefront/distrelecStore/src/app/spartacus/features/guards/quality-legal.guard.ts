import { Injectable } from '@angular/core';
import { Params, Router, UrlTree } from '@angular/router';
import { AuthRedirectService, AuthService, BaseSiteService, GlobalMessageService, GlobalMessageType, SemanticPathService, User } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { CountryService } from '../../site-context/services/country.service';
import { SalesOrgEmailsEnum } from '../../enums/salesOrgEmailsEnum';

/**
 * Checks if there is currently logged in user.
 * Use to protect pages dedicated only for logged in users with the quality and legal role.
 */
@Injectable({
  providedIn: 'root',
})
export class QualityLegalGuard  {
  constructor(
    protected authService: AuthService,
    protected authRedirectService: AuthRedirectService,
    protected baseSiteService: BaseSiteService,
    protected router: Router,
    protected semanticPathService: SemanticPathService,
    private distrelecUserService: DistrelecUserService,
    private globalMessageService: GlobalMessageService,
    private countryService: CountryService,
  ) {}

  redirectToPage(page: string, queryParams?: Params): UrlTree {
    this.authRedirectService.saveCurrentNavigationUrl();
    const urlTree = this.router.parseUrl(this.semanticPathService.get(page));
    if (queryParams) {
      urlTree.queryParams = queryParams;
    }
    return urlTree;
  }

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().pipe(
      switchMap((isLoggedIn: boolean) => {
        if (isLoggedIn) {
          return this.distrelecUserService.getUserInformation().pipe(
            switchMap((data: User) => {
              if (data.roles.includes('qualityAndLegal')) {
                return of(true);
              }
              return this.countryService.getActive().pipe(
                map((isoCode) => {
                  this.globalMessageService.add(
                    {
                      key: 'product.information.environmental.role_error',
                      params: { emailAddress: SalesOrgEmailsEnum[isoCode] },
                    },
                    GlobalMessageType.MSG_TYPE_ERROR,
                    5000,
                  );
                  return this.redirectToPage('qualityLanding');
                }),
              );
            }),
          );
        }
        return this.countryService.getActive().pipe(
          map((isoCode) => {
            this.globalMessageService.add(
              {
                key: 'product.information.environmental.role_error',
                params: { emailAddress: SalesOrgEmailsEnum[isoCode] },
              },
              GlobalMessageType.MSG_TYPE_ERROR,
              5000,
            );
            return this.redirectToPage('qualityLanding');
          }),
        );
      }),
    );
  }
}
