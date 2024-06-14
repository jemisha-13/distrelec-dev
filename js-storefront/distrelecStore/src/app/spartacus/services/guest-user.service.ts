import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { AuthStorageService, OccEndpointsService, User, UserIdService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { from, Observable, of } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { LoginService } from './login.service';
import { DistLogoutService } from './logout.service';

@Injectable({
  providedIn: 'root',
})
export class DistGuestUserService {
  userId$: Observable<string> = this.userIdService.getUserId();

  constructor(
    private authStorageService: AuthStorageService,
    private cartStoreService: CartStoreService,
    private http: HttpClient,
    private userIdService: UserIdService,
    private loginService: LoginService,
    private loginServiceHelper: LoginServiceHelper,
    private logoutService: DistLogoutService,
    private occEndpointsService: OccEndpointsService,
  ) {}

  checkoutAsGuest(email: string): Observable<void> {
    // Sync the cart state to have updated user info populated for checkout
    return this.userId$.pipe(
      first(),
      switchMap((userId) =>
        this.http.put<void>(
          this.occEndpointsService.buildUrl(
            `/users/${userId}/carts/${this.cartStoreService.getCartId()}/email?email=${encodeURIComponent(email)}`,
            {},
          ),
          {},
          {},
        ),
      ),
    );
  }

  loginRequestAfterGuestRegistration(userInfo: User, userId: string, password: string): Observable<any> {
    return from(this.loginService.authorizeLogin(userId, password)).pipe(
      tap(() => {
        this.loginServiceHelper.setUserSpecificSettings(userInfo);
        this.cartStoreService.resetCart();
        // If user already had something in their cart before registering, create a new cart and merge them two
        this.handleUserSuccessForExGuestLogin();
      }),
      catchError((err) => {
        this.loginService.isErrorMessage_.next('login.error_true');
        return of(err);
      }),
    );
  }

  handleUserSuccessForExGuestLogin(): void {
    this.loginServiceHelper.redirectGuestAfterRegistering();
  }

  isGuestSession(): boolean {
    return this.authStorageService.getItem('guest');
  }

  verifyGuestStatus(): void {
    if (this.isGuestSession()) {
      if (!this.authStorageService.getItem('guestExpiry') || this.isGuestSessionExpired()) {
        this.logoutService.logoutGuestUser();
      } else {
        this.setGuestStatus();
      }
    }
  }

  setGuestStatus(): void {
    this.authStorageService.setItem('guest', true);
    const expiryTime = new Date();
    expiryTime.setHours(expiryTime.getHours() + 4);
    this.authStorageService.setItem('guestExpiry', expiryTime.getTime());
  }

  isGuestSessionExpired(): boolean {
    const guestExpiry = this.authStorageService.getItem('guestExpiry');
    const guestExpiryDate = new Date(+guestExpiry);
    const now = new Date();
    return guestExpiryDate < now;
  }
}
