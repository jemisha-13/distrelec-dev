import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OccEndpointsService } from '@spartacus/core';

export type ErrorFeedbackForm = {
  productId: string;
  errorReason: string;
  errorDescription: string;
  customerName: string;
  customerEmailId: string;
};
@Injectable({
  providedIn: 'root',
})
export class ReportErrorFormService {
  headers = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
  ) {}

  sendErrorFeedback(form: ErrorFeedbackForm) {
    return this.http.post<any>(this.occEndpoints.buildUrl('errorfeedback/'), form, { headers: this.headers });
  }
}
