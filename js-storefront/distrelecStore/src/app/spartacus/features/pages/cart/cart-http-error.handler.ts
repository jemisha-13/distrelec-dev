import { GlobalMessageService, HttpErrorHandler, Priority, TranslationService } from '@spartacus/core';
import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { AppendComponentService } from '@services/append-component.service';
import { DistCartService } from '@services/cart.service';
import { take } from 'rxjs/operators';
import { CartException, CartSuppressedException, CartTypeException } from '@model/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartHttpErrorHandler extends HttpErrorHandler {
  constructor(
    globalMessageService: GlobalMessageService,
    private appendComponentService: AppendComponentService,
    private cartService: DistCartService,
    protected translation: TranslationService,
  ) {
    super(globalMessageService);
  }

  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return this.getErrors(errorResponse)?.every((error) => this.isCartError(error.type));
  }

  handleError(request: HttpRequest<any>, errorResponse: HttpErrorResponse) {
    const ignoreError = this.getErrors(errorResponse)?.every((error) => this.isErrorToIgnore(error));

    if (!ignoreError && this.getErrors(errorResponse)) {
      const error = errorResponse.error?.errors[0];
      const popupType = this.returnPopupType(error.reason);

      if (error.reason === CartException.PUNCHOUT_PRODUCTS) {
        this.translation
          .translate(CartException.PUNCHOUT_PRODUCTS, { code: request.body.product.code })
          .pipe(take(1))
          .subscribe((translation) => {
            this.appendComponentService.appendWarningPopup(null, null, translation, null, popupType);
          });
      } else if (error.type === CartTypeException.CART_DISABLED) {
        this.appendComponentService.appendWarningPopup(null, null, null, CartException.ADD_TO_CART_DISABLED, popupType);
      } else if (error.type === CartTypeException.CART_MODIFICATION) {
        this.appendComponentService.appendWarningPopup(null, null, error.message, null, popupType);
      } else {
        this.appendComponentService.appendWarningPopup(null, null, null, error.reason, popupType);
      }
    }
  }

  isErrorToIgnore(error: { message: string; reason: string; type: string; subjectType: string }): boolean {
    this.removeCartTokenIfNotFound(error);
    return Object.values(CartSuppressedException).some((value) => value === error.reason);
  }

  removeCartTokenIfNotFound(error): void {
    if (error.reason === CartSuppressedException.NOT_FOUND) {
      this.cartService.removeCartToken();
    }
  }

  returnPopupType(reason: string): string {
    return Object.values(CartException).some((value) => value === reason) ? 'warning' : 'error';
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }

  private getErrors(response: HttpErrorResponse): any[] {
    return response.error?.errors;
  }

  private isCartError(type: string): boolean {
    return Object.values(CartTypeException).some((exceptionType) => type === exceptionType);
  }
}
