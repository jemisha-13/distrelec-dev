import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { DistCartService } from '@services/cart.service';
import { CheckoutService } from '@services/checkout.service';
import { LocalStorageService } from '@services/local-storage.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { faCircleExclamation, faXmark, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { HttpErrorResponse } from '@angular/common/http';
import { CartStoreService } from '@state/cartState.service';
import { AppendComponentService } from '@services/append-component.service';
import { CartComponentService } from '@features/pages/cart/cart-component.service';
import { DistGuestUserService } from '@services/guest-user.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-checkout-guest-form',
  templateUrl: './checkout-guest-form.component.html',
})
export class CheckoutGuestFormComponent {
  @Input() checkoutLoginForm: UntypedFormGroup;
  @Input() isExportShop: boolean;

  faCircleExclamation: IconDefinition = faCircleExclamation;
  faXmark: IconDefinition = faXmark;

  isGuestLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  activeSiteId$: Observable<string> = this.countryService.getActive();

  constructor(
    private appendComponentService: AppendComponentService,
    private globalMessageService: GlobalMessageService,
    private countryService: CountryService,
    private localStorageService: LocalStorageService,
    private cartComponentService: CartComponentService,
    private checkoutService: CheckoutService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private guestUserService: DistGuestUserService,
    private router: Router,
    private channelService: ChannelService,
  ) {}

  submitGuestCheckout(): void {
    if (!this.checkoutLoginForm.get('guestCheckoutEmail').value) {
      this.setGuestFormError();
    } else if (!this.checkoutLoginForm.get('guestCheckoutEmail').errors) {
      this.isGuestLoading_.next(true);

      const voucherCode: string = this.localStorageService.getItem('voucherCode');
      this.guestUserService
        .checkoutAsGuest(this.checkoutLoginForm.get('guestCheckoutEmail').value)
        .pipe(
          tap(() => this.channelService.setActive('B2C')),
          switchMap(() => {
            if (voucherCode) {
              return this.handleVoucherValidation(voucherCode);
            }
            return of(null);
          }),
          switchMap(() => this.checkoutService.prepareForCheckout()),
          map((cartData: Cart) => {
            this.guestUserService.setGuestStatus();
            this.resetGuestForm();

            if (cartData.hasUnallowedBackorder) {
              this.router.navigate(['checkout/backorderDetails']);
            } else if (
              this.cartComponentService.checkIfPunchedOutProduct(cartData) ||
              this.cartComponentService.checkIfPhasedOutProduct(cartData) ||
              this.cartComponentService.checkIfEOLProduct(cartData)
            ) {
              this.router.navigate(['cart']);
            } else {
              this.validateCheckoutAndRedirect(cartData);
            }
          }),
          catchError((error: HttpErrorResponse) => of(this.handleGuestError(error))),
        )
        .subscribe();
    }
  }

  handleVoucherValidation(voucherCode): Observable<Cart> {
    return this.cartService.validateVoucher(voucherCode).pipe(
      map((response) => {
        this.globalMessageService.add({ key: 'cart.voucher_success' }, GlobalMessageType.MSG_TYPE_CONFIRMATION);
        return response;
      }),
      tap(() => this.localStorageService.removeItem('voucherCode')),
    );
  }

  setGuestFormError() {
    this.checkoutLoginForm.controls.guestCheckoutEmail.markAsTouched();
    this.checkoutLoginForm.controls.guestCheckoutEmail.setErrors({ incorrect: true });
  }

  isBelowMinimumOrderValue(): boolean {
    const cartData = this.cartStoreService.getCartState().value;
    if (this.cartService.isBelowMinimumOrderValue(cartData)) {
      this.router.navigate(['cart']);
      this.appendComponentService.appendMOVpopup(
        cartData.movLimit,
        cartData.subTotal.value,
        cartData.subTotal.currencyIso,
      );
      this.appendComponentService.appendBackdropModal({ lightTheme: true });
      return false;
    }
    return true;
  }

  resetGuestForm() {
    this.checkoutLoginForm.patchValue({
      guestCheckoutEmail: '',
    });
    this.isGuestLoading_.next(false);
  }

  validateCheckoutAndRedirect(cartData: Cart): void {
    // in case user's cart is over MOQ, they will be redirected to relevant page on checkout
    if (this.isBelowMinimumOrderValue()) {
      this.navigateAfterFastCheckout(cartData.eligibleForFastCheckout);
    }
  }

  navigateAfterFastCheckout(isFastCheckout: boolean): void {
    if (isFastCheckout) {
      this.router.navigate(['checkout/review-and-pay']);
    } else {
      this.router.navigate(['checkout/delivery']);
    }
  }

  handleGuestError(err: HttpErrorResponse): void {
    const message = err.error?.errors[0]?.message;
    if (message === 'login.checkout.guest.existing.address.error') {
      this.checkoutLoginForm.controls.guestCheckoutEmail.markAsTouched();
      this.checkoutLoginForm.controls.guestCheckoutEmail.setErrors({ alreadyExist: true });
    } else if (message === 'login.checkout.guest.registration.info') {
      this.checkoutLoginForm.controls.guestCheckoutEmail.markAsTouched();
      this.checkoutLoginForm.controls.guestCheckoutEmail.setErrors({ manyAttempts: true });
    } else if (message === 'cart.voucher_invalid') {
      this.checkoutService.errorMessage_.next(null);
      this.router.navigate(['cart']);
      this.globalMessageService.add({ key: 'cart.voucher_invalid' }, GlobalMessageType.MSG_TYPE_ERROR);
    } else {
      this.checkoutLoginForm.controls.guestCheckoutEmail.markAsTouched();
      this.checkoutLoginForm.controls.guestCheckoutEmail.setErrors({ incorrect: true });
    }
    this.isGuestLoading_.next(false);
  }
}
