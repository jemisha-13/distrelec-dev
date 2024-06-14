import { HttpErrorHandler, Priority, GlobalMessageType } from '@spartacus/core';

import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class MyAccountHttpErrorHandler extends HttpErrorHandler {
  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return (
      errorResponse.url.includes('/users/current/password') ||
      errorResponse.url.includes('/users/current/email') ||
      errorResponse.url.includes('/users/current/invoice-history') ||
      errorResponse.url.includes('/users/current/update/order-reference') ||
      errorResponse.url.includes('/users/current/user-management/create-employee') ||
      errorResponse.url.includes('/users/current/addresses')
    );
  }

  handleError(request: HttpRequest<any>, errorResponse: HttpErrorResponse): void {
    if (errorResponse.url.includes('/users/current/addresses')) {
      if (
        errorResponse.url.includes('/setDefaultAddress') ||
        (request.method !== 'PATCH' && request.method !== 'POST')
      ) {
        (errorResponse.error?.errors || []).forEach((error) => {
          this.globalMessageService.add(error.message, GlobalMessageType.MSG_TYPE_ERROR);
        });
      }
    }
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }
}
