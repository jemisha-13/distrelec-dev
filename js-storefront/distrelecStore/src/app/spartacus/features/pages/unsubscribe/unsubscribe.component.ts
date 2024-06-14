import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { faCheckSquare, faCircleCheck } from '@fortawesome/free-solid-svg-icons';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { NewsletterService } from '@services/newsletter.service';
import { Router } from '@angular/router';
import { UnsubscribeNewsletter, UnsubscribeUrlParams } from '@model/newsletter.model';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { DistCookieService } from '@services/dist-cookie.service';
import { WindowRef } from '@spartacus/core';

@Component({
  selector: 'app-unsubscribe',
  templateUrl: './unsubscribe.component.html',
  styleUrls: ['./unsubscribe.component.scss'],
})
export class UnsubscribeComponent implements OnInit {
  faCircleCheck = faCircleCheck;
  faSquare = faSquare;
  faCheckSquare = faCheckSquare;

  customerDetails$: Observable<UnsubscribeNewsletter>;
  urlParameters: UnsubscribeUrlParams;

  categoryCode: string;
  customerEmail: string;
  newEmail = '';
  submitButtonDisabled = true;
  otherReason = '';

  feedback;
  resubscribeFeedback;
  unsubscribeAllFeedback;

  unsubscribeForm = this.formBuilder.group({
    reason1: false,
    reason2: false,
    reason3: false,
    reason4: false,
    reasonOther: false,
  });
  constructor(
    private formBuilder: UntypedFormBuilder,
    private newsletterService: NewsletterService,
    protected router: Router,
    private cdRef: ChangeDetectorRef,
    private cookieService: DistCookieService,
    private winRef: WindowRef,
  ) {}

  handleSubmit(): void {
    const reasons: string[] = [];
    if (this.unsubscribeForm.get('reason1').value) {
      reasons.push('1');
    }
    if (this.unsubscribeForm.get('reason2').value) {
      reasons.push('2');
    }
    if (this.unsubscribeForm.get('reason3').value) {
      reasons.push('3');
    }
    if (this.unsubscribeForm.get('reason4').value) {
      reasons.push('4');
    }

    const reason: string = reasons.join(',');
    this.newsletterService
      .submitUnsubscribeFeedback(
        this.urlParameters.smcId,
        this.urlParameters.category,
        this.customerEmail,
        reason,
        this.newEmail,
        this.otherReason,
      )
      .subscribe(() => {
        this.feedback = true;
        this.cdRef.detectChanges();
      });
  }

  resubscribe(email: string): void {
    this.newsletterService.resubscribeNewsletter(email, this.urlParameters.category).subscribe((data) => {
      this.resubscribeFeedback = data;
      this.cdRef.detectChanges();
    });
  }

  unsubscribeAll(email: string): void {
    this.newsletterService.unsubscribeAll(email).subscribe((data) => {
      this.unsubscribeAllFeedback = data;
      this.cdRef.detectChanges();
    });
  }

  updateFormValue(control: string): void {
    this.unsubscribeForm.get(control).setValue(!this.unsubscribeForm.get(control).value);
    this.submitButtonDisabled = this.updateButtonDisabled();
  }

  updateButtonDisabled(): boolean {
    if (
      this.unsubscribeForm.get('reason1').value ||
      this.unsubscribeForm.get('reason2').value ||
      this.unsubscribeForm.get('reason3').value ||
      this.unsubscribeForm.get('reason4').value
    ) {
      return false;
    }
    return true;
  }

  formatUrlParams(urlString: string): UnsubscribeUrlParams {
    const url = new URL(urlString, this.winRef.location.origin);
    const params = url.searchParams;
    return {
      category: params.get('category'),
      smcId: params.get('sap-outbound-id'),
      unsubscribeCategory: params.get('unsubscribeFromCategory'),
    };
  }

  ngOnInit() {
    this.urlParameters = this.formatUrlParams(this.router.url);
    this.customerDetails$ = this.newsletterService
      .getUnsubscribeFeedback(
        this.urlParameters.category,
        this.urlParameters.smcId,
        this.urlParameters.unsubscribeCategory,
      )
      .pipe(
        tap((data) => {
          const category = this.urlParameters.category;
          if (category === data.customerSurveyId) {
            this.categoryCode = 'unsubscribe.feedback.infoText.customersurvey.id56';
          } else if (category === data.knowhowId) {
            this.categoryCode = 'unsubscribe.feedback.infoText.knowhow.id81';
          } else if (category === data.salesAndClearanceId) {
            this.categoryCode = 'unsubscribe.feedback.infoText.salesandclearance.id82';
          } else if (category === data.personaliseRecommendationId) {
            this.categoryCode = 'unsubscribe.feedback.infoText.personaliserecommendation.id86';
          } else {
            this.categoryCode = 'unsubscribe.feedback.infoText.default';
          }

          this.customerEmail = data.email;
        }),
      );
    if (this.cookieService.get('subscribePopupDelay')) {
      this.cookieService.delete('popUpShownDelay');
    }
    this.cookieService.set('popUpShownDelay', 'true', 30, '/');
    this.cookieService.delete('subscribePopupDelay');
  }
}
