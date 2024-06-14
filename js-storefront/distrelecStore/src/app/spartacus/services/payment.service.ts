import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BaseSiteService, OccEndpointsService, UserIdService, WindowRef } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  cartTokenKey: any;
  cartTokenValue: any;
  siteId: string;
  userId: string;

  constructor(
    private winRef: WindowRef,
    private baseSiteService: BaseSiteService,
    private occEndpoitService: OccEndpointsService,
    private userIdService: UserIdService,
    private http: HttpClient,
    private localStorage: LocalStorageService,
    private router: Router,
  ) {}

  postPaymentRequest(params: any, condition: string): Observable<any> {
    if (condition === 'success') {
      this.localStorage.setItem('success', 'loading');
    } else {
      this.localStorage.setItem('error', 'loading');
    }

    return this.baseSiteService.getActive().pipe(
      // We need to build the token from scratch since using cart service to retrieve the value here would cause
      // 400 error on adding cart as the iframe has no basesite defined
      tap((activeSiteId) => {
        this.cartTokenKey = `spartacus⚿${activeSiteId}⚿cart`;
        this.cartTokenValue = JSON.parse(this.winRef.localStorage.getItem(`spartacus⚿${activeSiteId}⚿cart`));
      }),
      withLatestFrom(this.userIdService.getUserId()),
      switchMap(([, userId]) => {
        this.userId = userId;
        return this.http.post<any>(
          this.occEndpoitService.buildUrl(`/users/${userId}/carts/${this.cartTokenValue.active}/payment/${condition}`),
          params,
          {},
        );
      }),
      map((response) => {
        // Paypal loads in the new page and hence router will work
        if (this.localStorage.getItem('paypal')) {
          this.localStorage.removeItem('paypal');
          this.localStorage.removeItem('addressId');
          if (condition === 'success') {
            this.localStorage.setItem('dispatchPurchaseEvent', true);
            this.router.navigate(['/checkout/orderConfirmation/' + response?.orderCode]);
          } else {
            this.localStorage.setItem('error', response?.errorCode);
            this.router.navigate(['/checkout/review-and-pay']);
          }
        } else {
          // If payment is made inside of evo iframe, then local storage is used to update components outside of iframe
          if (condition === 'success') {
            this.localStorage.setItem('success', 'complete');
            this.localStorage.setItem('orderCode', response?.orderCode);
          } else {
            this.localStorage.setItem('error', response?.errorCode);
          }
        }
      }),
      catchError((err) => of(this.setError(err.error.errors[0]))),
    );
  }

  setError(error) {
    if (error.reason === 'PurchaseBlockedProducts') {
      const productCodes = error.message.replaceAll('-', '').split(',');
      this.localStorage.setItem('purchaseBlockedProducts', productCodes);
      this.localStorage.setItem('error', error.reason);
      // Redirect user directly to backorder details page if they place an order with paypal
      if (this.localStorage.getItem('paypal')) {
        this.router.navigate(['checkout/backorderDetails']);
        this.localStorage.removeItem('paypal');
      }
    } else {
      this.localStorage.setItem('error', error.message);
    }
  }
}
