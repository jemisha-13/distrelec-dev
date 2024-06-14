import { HttpInterceptor, HttpRequest } from '@angular/common/http';
import { WithCredentialsInterceptor } from '@spartacus/core';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class DistWithCredentialsInterceptor extends WithCredentialsInterceptor implements HttpInterceptor {
  protected requiresWithCredentials(request: HttpRequest<any>): boolean {
    return !request.url.includes('/basesites') && super.requiresWithCredentials(request);
  }
}
