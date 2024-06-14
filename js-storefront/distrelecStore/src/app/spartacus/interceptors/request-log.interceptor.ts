import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class RequestLogInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('REQUEST: ', request.url);

    return next.handle(request).pipe(
      tap(
        (event) => {
          if (event instanceof HttpResponse) {
            console.log('RESPONSE: ', event.status, event.url);
          }
        },
        (error) => {
          console.log('RESPONSE_ERROR: ', error.status, error.message, error.url);
        },
      ),
    );
  }
}
