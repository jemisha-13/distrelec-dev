import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderHistoryService {
  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  getOrderDetails(orderCode: string): Observable<any> {
    return this.http.get<any>(this.occEndpoints.buildUrl(`/users/current/order-details/${orderCode}`), {
      headers: { 'Cache-Control': 'no-cache' },
    });
  }
}
