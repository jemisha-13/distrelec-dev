import {
  AuthService,
  ForbiddenHandler,
  GlobalMessageService,
  GlobalMessageType,
  OccEndpointsService,
  Priority,
} from '@spartacus/core';
import { HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class DistCheckoutForbiddenHandler extends ForbiddenHandler {
  constructor(
    protected globalMessageService: GlobalMessageService,
    protected authService: AuthService,
    protected occEndpoints: OccEndpointsService,
    private router: Router,
  ) {
    super(globalMessageService, authService, occEndpoints);
  }

  handleError(request: HttpRequest<any>) {
    if (request.url.includes('/carts')) {
      return this.router.navigate(['/cart']).then(() => {
        this.globalMessageService.add({ key: 'checkout.error.invalid.accountType' }, GlobalMessageType.MSG_TYPE_ERROR);
      });
    }
    if (request.url.includes('/quotations')) {
      // todo add translation and potential redirection
      return this.globalMessageService.add(
        { key: 'checkout.error.invalid.accountType' },
        GlobalMessageType.MSG_TYPE_ERROR,
      );
    }
    if (request.url.includes('/order-returns')) {
      // todo add translation and potential redirection
      return this.globalMessageService.add(
        { key: 'checkout.error.invalid.accountType' },
        GlobalMessageType.MSG_TYPE_ERROR,
      );
    }
    super.handleError(request);
  }

  getPriority(): Priority {
    return Priority.HIGH;
  }
}
