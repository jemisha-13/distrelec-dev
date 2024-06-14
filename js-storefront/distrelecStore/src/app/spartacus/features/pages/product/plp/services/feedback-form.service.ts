import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';

export type ZeroSearchResultFeedbackForm = {
  email: string;
  manufacturer: string;
  manufacturerType: string;
  productName: string;
  manufacturerTypeOtherName: string;
  tellUsMore: string;
  searchTerm: string;
};

export type FeedbackForm = {
  name: string;
  email: string;
  phone: string;
  feedback: string;
};

@Injectable({
  providedIn: 'root',
})
export class FeedbackFormService {
  headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  sendZeroSearchResultFeedback(form: ZeroSearchResultFeedbackForm) {
    return this.http.post(this.occEndpoints.buildUrl('/feedback/zeroResultSearch'), form, { headers: this.headers });
  }

  sendFeedback(form: FeedbackForm) {
    return this.http.post<any>(this.occEndpoints.buildUrl('/feedback/general'), form, { headers: this.headers });
  }
}
