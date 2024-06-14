import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotifyMeService {
  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');

  getNotifyMe(productCodes: string[], customerEmail: string): Observable<any> {
    return this.http.post<any>(this.buildNotifyMeRequest(productCodes, customerEmail), { headers: this.headers });
  }

  protected buildNotifyMeRequest(productCodes: string[], customerEmail: string): string {
    return this.occEndpoints.buildUrl('products/notifyBackInStock', {
      queryParams: {
        articleNumbers: productCodes,
        customerEmail,
      },
    });
  }
}
