import { Injectable } from '@angular/core';
import { ErrorModel, GlobalMessageType, HttpErrorHandler, Priority } from '@spartacus/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class BomToolErrorHandler extends HttpErrorHandler {
  hasMatch(errorResponse: HttpErrorResponse): boolean {
    return this.getErrors(errorResponse).some((e) => e.type === 'BomToolFileError');
  }

  handleError(request: HttpRequest<any>, response: HttpErrorResponse): void {
    this.getErrors(response)
      .filter((e) => e.type === 'BomToolFileError')
      .forEach((error) => {
        this.globalMessageService.add(error.message, GlobalMessageType.MSG_TYPE_ERROR);
      });
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }

  protected getErrors(response: HttpErrorResponse): ErrorModel[] {
    return (response.error?.errors || []).filter((error: ErrorModel) => error.type !== 'JaloObjectNoLongerValidError');
  }
}
