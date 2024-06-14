import { HttpErrorHandler, Priority } from '@spartacus/core';

import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class MyRegistrationHttpErrorHandler extends HttpErrorHandler {
  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return errorResponse.url.includes('/validateUid');
  }

  handleError(request: HttpRequest<any>, errorResponse: HttpErrorResponse): void {}

  getPriority(): Priority {
    return Priority.HIGH;
  }
}
