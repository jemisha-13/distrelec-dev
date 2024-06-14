import {
  AuthHttpHeaderService,
  AuthRedirectService,
  AuthService,
  AuthStorageService,
  AuthToken,
  GlobalMessageService,
  OAuthLibWrapperService,
  OccEndpointsService,
  RoutingService,
} from '@spartacus/core';
import { HttpEvent, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { DistCartService } from '@services/cart.service';
import { LocalStorageService } from '@services/local-storage.service';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class DistAuthHttpHeaderService extends AuthHttpHeaderService {
  constructor(
    protected authService: AuthService,
    protected authStorageService: AuthStorageService,
    protected oAuthLibWrapperService: OAuthLibWrapperService,
    protected routingService: RoutingService,
    protected occEndpoints: OccEndpointsService,
    protected globalMessageService: GlobalMessageService,
    protected authRedirectService: AuthRedirectService,
    private cartService: DistCartService,
    private localStorageService: LocalStorageService,
    private router: Router,
  ) {
    super(
      authService,
      authStorageService,
      oAuthLibWrapperService,
      routingService,
      occEndpoints,
      globalMessageService,
      authRedirectService,
    );
  }

  handleExpiredAccessToken(
    request: HttpRequest<any>,
    next: HttpHandler,
    initialToken: AuthToken | undefined,
  ): Observable<HttpEvent<AuthToken>> {
    return this.getValidToken(initialToken).pipe(
      switchMap((token) =>
        token
          ? next.handle(this.createNewRequestWithNewToken(request, token))
          : next.handle(this.createNewRequestWithoutToken(request)),
      ),
    );
  }

  handleExpiredRefreshToken() {
    this.cartService.revokeCartEntries();
    this.resetCartId();
    this.localStorageService.removeItem('guest');
    this.localStorageService.removeItem('addressId');
    this.localStorageService.removeItem('IS_NEW_REGISTRATION');
    this.localStorageService.removeItem('success');
    this.localStorageService.removeItem('purchaseBlockedProducts');

    if (this.isNotHomepageOrNewPageOpening()) {
      super.handleExpiredRefreshToken();
    } else {
      this.authService.coreLogout();
    }
  }

  private isNotHomepageOrNewPageOpening() {
    // Regex matching '/' and '/en', 'en/', '/en/', 'fr/' etc
    const baseContextUrlPattern = /^(\/)?([a-z]{2}(\/)?)?$/;
    return !baseContextUrlPattern.test(this.router.url);
  }

  private createNewRequestWithoutToken(request: HttpRequest<any>): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: '',
      },
    });
  }

  private resetCartId(): void {
    const cartRegexExpression = new RegExp('spartacus⚿(distrelec_[A-Z]{2}|undefined)⚿cart', 'g');
    this.localStorageService.getItems().forEach((key) => {
      if (key.match(cartRegexExpression)) {
        this.localStorageService.removeItem(key);
      }
    });
  }
}
