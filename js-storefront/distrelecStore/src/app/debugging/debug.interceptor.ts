import { HttpEvent, HttpEventType, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Inject, Injectable, Optional } from '@angular/core';
import { REQUEST, RESPONSE } from '@spartacus/setup/ssr';
import { Response } from 'express';

@Injectable({
  providedIn: 'root',
})
export class DebugHttpInterceptor implements HttpInterceptor {
  constructor(
    @Inject(REQUEST) private request: Request,
    @Optional() @Inject(RESPONSE) private response: Response,
  ) {
    this.id = response.getHeader('SSR-UUID');
  }

  logBeforeTimeout = false;

  id = null;

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let startTime = Date.now();
    return next.handle(req).pipe(
      tap(
        (_event) => {
          if (_event.type == HttpEventType.Response && this.response?.headersSent) {
            console.log(
              'Request for SSR with ID:' +
                this.id +
                ':' +
                this.request.url +
                ' - ' +
                req.url +
                ' FINISHED AFTER TIMEOUT - time in ms:' +
                (new Date().valueOf() - startTime.valueOf()),
            );
          } else if (_event.type == HttpEventType.Response && this.logBeforeTimeout && !this.response?.headersSent) {
            console.log(
              'Request for SSR with ID:' +
                this.id +
                ':' +
                this.request.url +
                ' - ' +
                req.url +
                ' FINISHED BEFORE TIMEOUT - time in ms:' +
                (new Date().valueOf() - startTime.valueOf()),
            );
          }
        },
        (_error) => {
          console.log('Request for SSR with ID:' + this.id + ':' + this.request.url + ' - ' + req.url + ' - FAILED');
        },
      ),
    );
  }
}
