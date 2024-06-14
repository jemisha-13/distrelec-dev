import { GlobalMessageService, HttpErrorHandler, Priority } from '@spartacus/core';

import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { LoginService } from '@services/login.service';
import { DistCartService } from '@services/cart.service';

/**
 * HDLS-2592: suppress the error banner on cart as temprorary solution
 * Recalculate errors - HDLS-2599
 */
@Injectable({
  providedIn: 'root',
})
export class LoginHttpErrorHandler extends HttpErrorHandler {
  constructor(
    globalMessageService: GlobalMessageService,
    private loginService: LoginService,
    private cartService: DistCartService,
  ) {
    super(globalMessageService);
  }

  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return errorResponse.error?.error === 'invalid_grant';
  }

  handleError(request: HttpRequest<any>, errorResponse: HttpErrorResponse) {
    if (this.hasMatch(errorResponse)) {
      if (errorResponse.error?.error_description === 'Bad credentials') {
        this.loginService.isErrorMessage_.next('login.error_true');
      } else {
        this.loginService.isErrorMessage_.next('httpHandlers.sessionExpired');
        this.cartService.revokeCartEntries();
      }
    }
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }

  protected getErrors(response: HttpErrorResponse): boolean {
    return response.error?.errors;
  }
}
