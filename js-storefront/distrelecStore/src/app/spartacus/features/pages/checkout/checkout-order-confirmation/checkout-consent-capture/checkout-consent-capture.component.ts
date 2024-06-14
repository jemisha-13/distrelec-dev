import { Component, Input, OnInit } from '@angular/core';
import { faSquare, faSquareCheck, IconDefinition } from '@fortawesome/free-regular-svg-icons';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { catchError, first, tap } from 'rxjs/operators';
import { BehaviorSubject, of } from 'rxjs';
import { NewsletterService } from '@services/newsletter.service';
import { LoginService } from '@services/login.service';
import { Order } from '@spartacus/order/root';
import { faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-checkout-consent-capture',
  templateUrl: './checkout-consent-capture.component.html',
  styleUrls: ['./checkout-consent-capture.component.scss'],
})
export class CheckoutConsentCaptureComponent implements OnInit {
  @Input() order: Order;
  @Input() customerUid: string;

  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  subscribeEmailSuccess_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  showDoubleOptInModal_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  subscribeForm: FormGroup;

  isPersonalRecomm = false;
  subscribeEmailError: string = '';
  subscribeFormSubmits = 0;

  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;
  faArrowRight: IconDefinition = faArrowRight;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private appendComponentService: AppendComponentService,
    private newsletterSubscribeService: NewsletterService,
  ) {}

  ngOnInit() {
    this.setSubscribeForm();
  }

  setSubscribeForm() {
    this.subscribeForm = this.fb.group({
      email: new FormControl(this.customerUid, [Validators.required, Validators.email]),
    });
  }

  submitSubscribeClick(recaptcha): void {
    this.subscribeFormSubmits++;
    this.appendComponentService.startScreenLoading();

    if (this.subscribeFormSubmits > 3 && !this.isCaptchaDisabled_.value) {
      recaptcha.execute();
    } else {
      this.submitSubscribe();
    }
  }
  submitSubscribe(): void {
    this.newsletterSubscribeService
      .submitNewsletterSubscribe(
        true,
        this.subscribeForm.get('email').value,
        this.isPersonalRecomm,
        'order_confirmation',
      )
      .pipe(
        first(),
        tap((data) => {
          if (data.doubleOptIn) {
            this.activateDoubleOptInModal();
          } else {
            this.subscribeEmailSuccess_.next(true);
          }

          if (data.errorMessage !== '') {
            this.subscribeEmailSuccess_.next(false);
            this.subscribeEmailError = data.errorMessage;
          } else {
            this.subscribeEmailError = '';
          }

          this.appendComponentService.stopScreenLoading();
        }),
        catchError(() => {
          this.appendComponentService.stopScreenLoading();
          return of(false);
        }),
      )
      .subscribe();
  }

  activateDoubleOptInModal(visibility: boolean = true): void {
    this.showDoubleOptInModal_.next(visibility);
  }

  subscribeCaptchaResolvedFallback(event?): void {
    if (!!event) {
      this.submitSubscribe();
      grecaptcha.reset();
    } else {
      this.appendComponentService.stopScreenLoading();
    }
  }

  onChangePersonalRecommendationsCheckbox(event): void {
    this.isPersonalRecomm = event.target.checked;
  }
}
