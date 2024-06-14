import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { SubscribeNewsletter, UnsubscribeNewsletter } from '@model/newsletter.model';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root',
})
export class NewsletterService {
  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private loginService: LoginService,
    private loginServiceHelper: LoginServiceHelper,
  ) {}

  getUnsubscribeFeedback(
    category: string,
    smcOutboundId: string,
    unsubscribeCategory: string,
  ): Observable<UnsubscribeNewsletter> {
    return this.http.get<UnsubscribeNewsletter>(this.occEndpoints.buildUrl('/newsletter/unsubscribe-feedback'), {
      params: {
        category: category,
        smcOutboundId: smcOutboundId,
        unsubscribeFromCategory: unsubscribeCategory,
      },
    });
  }

  submitUnsubscribeFeedback(
    outboundId: string,
    category: string,
    email: string,
    reason: string,
    alternateEmail: string = '',
    otherReason: string = '',
  ): Observable<object> {
    return this.http.post(this.occEndpoints.buildUrl('/newsletter/unsubscribe-feedback'), {
      alternateEmail: alternateEmail,
      category: category,
      email: email,
      reason: reason,
      otherReason: otherReason,
      smcOutboundId: outboundId,
    });
  }

  resubscribeNewsletter(email: string, resubscribeCategory: string): Observable<object> {
    return this.http.put(this.occEndpoints.buildUrl('/newsletter/resubscribe'), {
      email: email,
      category: resubscribeCategory,
    });
  }

  unsubscribeAll(email: string): Observable<object> {
    return this.http.put(this.occEndpoints.buildUrl('/newsletter/unsubscribeall'), {
      email: email,
    });
  }

  submitNewsletterSubscribe(
    isCheckout: boolean,
    email: string,
    isPersonalisationOn: boolean,
    placement?: string,
  ): Observable<SubscribeNewsletter> {
    return this.http.post<SubscribeNewsletter>(
      this.getNewsletterEndpoint(isCheckout, email, isPersonalisationOn, placement),
      {},
    );
  }

  protected getNewsletterEndpoint(checkout, email, personalization, placement?): string {
    return this.occEndpoints.buildUrl('/newsletter/subscribe', {
      queryParams: {
        checkout: checkout,
        email: email,
        personalization: personalization,
        placement: placement,
      },
    });
  }
}
