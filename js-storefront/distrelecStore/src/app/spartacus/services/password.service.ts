import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuthService,
  ClientAuthenticationTokenService,
  InterceptorUtil,
  OccEndpointsService,
  USE_CLIENT_TOKEN,
} from '@spartacus/core';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, first, map, switchMap, tap } from 'rxjs/operators';
import { LoginService } from './login.service';
import { DistrelecUserService } from '@services/user.service';
import { PasswordUpdateResponse, SetInitialPasswordRequest } from '@model/auth.model';

@Injectable({
  providedIn: 'root',
})
export class PasswordService {
  isLoginForgotPwdReset_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isPasswordResetTokenInvalid_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isPasswordReseted_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private clientAuthenticationTokenService: ClientAuthenticationTokenService,
    private http: HttpClient,
    private occEndpointsService: OccEndpointsService,
    private authService: AuthService,
    private loginService: LoginService,
    private userService: DistrelecUserService,
  ) {}

  createAuthHeader(): HttpHeaders {
    // We must create the token first before calling forgotten pwd endpoint
    this.clientAuthenticationTokenService.loadClientAuthenticationToken();

    const headers = new HttpHeaders({
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'Content-Type': 'application/json',
    });

    return InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
  }

  submitForgottenPwd(email): Observable<boolean> {
    const url = this.occEndpointsService.buildUrl('forgottenpasswordtokens', {
      queryParams: {
        userId: email,
      },
    });
    return this.http.post<any>(url, {}, { headers: this.createAuthHeader() }).pipe(
      first(),
      catchError(() => of(true)),
      map(() => false),
    );
  }

  isChangePasswordTokenValid(token: string): Observable<boolean> {
    return this.http.get<boolean>(
      this.occEndpointsService.buildUrl('change/password/token-validation', { queryParams: { token } }),
      { headers: this.createAuthHeader() },
    );
  }

  updatePassword(password: string, token: string): Observable<PasswordUpdateResponse> {
    const url = this.occEndpointsService.buildUrl('/password/update');
    return this.http
      .post<PasswordUpdateResponse>(url, {
        password,
        token,
      })
      .pipe(
        tap((response) => {
          switch (response.value) {
            case 'SUCCESS':
              this.isPasswordReseted_.next(true);
              break;
            case 'TOKEN_INVALIDATED':
              this.loginService.isErrorMessage_.next('updatePwd.token.invalidated');
              break;
            case 'ERROR':
              this.loginService.isErrorMessage_.next('updatePwd.token.invalid');
          }
        }),
        catchError((error) => {
          this.loginService.isErrorMessage_.next('updatePwd.token.invalid');
          throw error;
        }),
      );
  }

  changePassword(checkNewPassword: string, currentPassword: string, newPassword: string): Observable<any> {
    const url = this.occEndpointsService.buildUrl(`/users/current/password`);
    return this.http
      .put<any>(url, {
        checkNewPassword,
        currentPassword,
        newPassword,
      })
      .pipe(
        first(),
        switchMap(() => from(this.authService.coreLogout())),
        switchMap((_) =>
          this.userService.getUserDetails().pipe(
            first(),
            switchMap((userDetails) => this.loginService.postLoginRequest(userDetails.uid, newPassword)),
          ),
        ),
      );
  }

  setInitialPasswordRequest(payload: SetInitialPasswordRequest): Observable<SetInitialPasswordRequest> {
    return this.http.post<SetInitialPasswordRequest>(
      this.occEndpointsService.buildUrl(`/account/password/set-initial-password`),
      payload,
      { headers: this.createAuthHeader() },
    );
  }

  isInitialPasswordTokenValid(token: string, migration: boolean): Observable<boolean> {
    return this.http.get<boolean>(
      this.occEndpointsService.buildUrl(
        `/initial/password/token-validation?token=${encodeURIComponent(token)}&migration=${migration}`,
      ),
      { headers: this.createAuthHeader() },
    );
  }
}
