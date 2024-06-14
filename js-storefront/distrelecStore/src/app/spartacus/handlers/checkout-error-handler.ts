import { Injectable } from '@angular/core';
import { GlobalMessageService, HttpErrorHandler, Priority, TranslationService } from '@spartacus/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { CartStoreService } from '@state/cartState.service';
import { CheckoutService } from '@services/checkout.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { CartComponentService } from '@features/pages/cart/cart-component.service';
import { Router } from '@angular/router';
import { catchError, switchMap, take } from 'rxjs/operators';
import { EMPTY, of, throwError } from 'rxjs';

import { CartSuppressedException } from '@model/cart.model';

class TranslationLoadingError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'TranslationLoadingError';
  }
}

@Injectable({
  providedIn: 'root',
})
export class DistCheckoutHttpErrorHandler extends HttpErrorHandler {
  constructor(
    globalMessageService: GlobalMessageService,
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
    private siteConfigService: SiteConfigService,
    private i18n: TranslationService,
    private cartComponentService: CartComponentService,
    private router: Router,
  ) {
    super(globalMessageService);
  }

  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return this.siteConfigService.getCurrentPageTemplate() === 'CheckoutPageTemplate';
  }

  handleError(request: HttpRequest<any>, response: HttpErrorResponse) {
    if (this.isPunchoutError(response)) {
      this.cartStoreService.setCartState(response.error);
      this.cartComponentService.showPunchOutErrorGlobalMessage(response.error.punchedOutProducts);
      return this.router.navigate(['cart']);
    }
    const error = response.error?.errors;
    if (error && error.length > 0 && !this.isErrorIgnored(response)) {
      this.getTranslationForMessageOrLoadFallback(error[0].message);
    }
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }

  private isPunchoutError(response: HttpErrorResponse): boolean {
    return !!response.error && !!response.error.punchedOutProducts;
  }

  private isErrorIgnored(response: HttpErrorResponse) {
    return (
      this.cartNotFoundWhenMerging(response) ||
      this.tokensExpired(response) ||
      this.isMOVError(response) ||
      this.isUnallowedBackorder(response)
    );
  }
  private cartNotFoundWhenMerging(response: HttpErrorResponse) {
    return response.error?.errors?.some((e) => e.reason === CartSuppressedException.NOT_FOUND);
  }

  private tokensExpired(response: HttpErrorResponse): boolean {
    return (
      response.error?.error === 'invalid_token' && response.error?.errors?.some((e) => e.type === 'InvalidTokenError')
    );
  }

  private isMOVError(response: HttpErrorResponse): boolean {
    return response.error?.errors?.some((e) => e.message === CartSuppressedException.INVALID_MOV);
  }

  private isUnallowedBackorder(response: HttpErrorResponse): boolean {
    return response.error?.errors?.some((e) => e.message === CartSuppressedException.INVALID_BACKORDER);
  }

  private getTranslationForMessageOrLoadFallback(message: string) {
    return this.i18n
      .translate(message)
      .pipe(
        take(1),
        switchMap((result) =>
          this.translationNotFound(result, message)
            ? throwError(new TranslationLoadingError('checkout.support_failed'))
            : of(result),
        ),
        catchError((err) => {
          if (err instanceof TranslationLoadingError) {
            this.checkoutService.errorMessage_?.next(err.message);
            return EMPTY;
          }
          return throwError(err);
        }),
      )
      .subscribe(() => this.checkoutService.errorMessage_?.next(message));
  }

  private translationNotFound(result: string, message: string): boolean {
    // this will happen only in prod mode, check spartacus translate method impl
    const NON_BREAKING_SPACE = String.fromCharCode(160);
    return result.includes(message + ':' + message) || result === NON_BREAKING_SPACE;
  }
}
