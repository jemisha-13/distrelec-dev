import { BadRequestHandler, GlobalMessageService } from '@spartacus/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class DeactivatedUserErrorHandler extends BadRequestHandler {
  constructor(
    protected globalMessageService: GlobalMessageService,
    private router: Router,
  ) {
    super(globalMessageService);
  }

  handleError(request: HttpRequest<any>, response: HttpErrorResponse) {
    if (response.error?.errors?.[0]?.message === 'httpHandlers.accountDeactivated') {
      this.router.navigate(['/']).then((success) => {
        if (success) {
          super.handleError(request, response);
        }
      });
    }
  }
}
