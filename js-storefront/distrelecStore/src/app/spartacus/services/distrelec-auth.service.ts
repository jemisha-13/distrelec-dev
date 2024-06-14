import { Injectable } from '@angular/core';
import {
  AuthActions,
  AuthRedirectService,
  AuthService,
  AuthStorageService,
  LanguageService,
  OAuthLibWrapperService,
  RoutingService,
  StateWithClientAuth,
  UserIdService,
} from '@spartacus/core';
import { Store } from '@ngrx/store';
import { first } from 'rxjs/operators';
import { TokenResponse } from 'angular-oauth2-oidc/types';

@Injectable({
  providedIn: 'root',
})
export class DistrelecAuthService extends AuthService {
  constructor(
    protected store: Store<StateWithClientAuth>,
    protected userIdService: UserIdService,
    protected oAuthLibWrapperService: OAuthLibWrapperService,
    protected authStorageService: AuthStorageService,
    protected authRedirectService: AuthRedirectService,
    protected routingService: RoutingService,
    protected languageService: LanguageService,
  ) {
    super(store, userIdService, oAuthLibWrapperService, authStorageService, authRedirectService, routingService);
  }

  /**
   * @Override
   * Set the user's language from the token response
   *
   * Loads a new user token with Resource Owner Password Flow.
   * @param userId
   * @param password
   */
  async loginWithCredentials(userId: string, password: string): Promise<void> {
    await this.oAuthLibWrapperService.authorizeWithPasswordFlow(userId, password).then((data: TokenResponse) => {
      this.languageService.setActive(data.language);
      this.store.dispatch(new AuthActions.Login());
    });
  }

  coreLogout(): Promise<void> {
    this.resetRefreshToken();
    return super.coreLogout();
  }

  private resetRefreshToken() {
    // reset because hybris will automatically delete it when deleting access token
    this.authStorageService
      .getToken()
      .pipe(first())
      .subscribe((token) => {
        if (!!token.refresh_token) {
          // eslint-disable-next-line @typescript-eslint/naming-convention
          this.authStorageService.setToken({ ...token, refresh_token: undefined });
        }
      });
  }
}
