import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnDestroy, Renderer2, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faCheckSquare } from '@fortawesome/free-solid-svg-icons';
import { LoginService } from '@services/login.service';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { SiteConfigService } from '@services/siteConfig.service';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import { RecaptchaComponent } from 'ng-recaptcha';

@Component({
  selector: 'app-communication-pref',
  templateUrl: './communication-pref.component.html',
  styleUrls: ['./communication-pref.component.scss'],
})
export class CommunicationPrefComponent implements OnDestroy {
  @Input() onControlTouch: (args: any) => void;
  @Input() generalRegForm: UntypedFormGroup;
  @Input() stepsList_: BehaviorSubject<{ key: string; active: boolean }[]>;
  @Input() activeSiteId: string;
  @ViewChild('captchaRef') captchaRef: RecaptchaComponent;
  @Input() isEmailExist$: BehaviorSubject<{ isEmailExist: boolean }>;

  postRegisterSubscription: Subscription;

  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  errorMessage_: BehaviorSubject<string> = this.registerService.errorMessage_;
  isCheckout: boolean = this.siteConfigService.getCurrentPageTemplate() === 'CheckoutPageTemplate';
  faSquare = faSquare;
  faCheckSquare = faCheckSquare;
  emailConsent: boolean;
  consentEmail: string;

  constructor(
    private registerService: RegisterService,
    // Please keep Renderer2 and WindowRef for onControlTouch() which is passed from the parent class
    private renderer: Renderer2,
    private appendComponentService: AppendComponentService,
    private siteConfigService: SiteConfigService,
    private loginService: LoginService,
  ) {}

  ngOnDestroy(): void {
    if (this.postRegisterSubscription && !this.postRegisterSubscription.closed) {
      this.postRegisterSubscription.unsubscribe();
    }
  }

  // Since we change the styling of the checkbox,
  // To achieve this, we hide the original checkbox and trigger icons based on user's click
  triggerConsent(consent: any) {
    if (consent === 'marketingConsent') {
      this.emailConsent = this.generalRegForm.get(consent).value;
    }
    this.onControlTouch('checkPwd');
  }

  onSubmit() {
    if (!this.isCaptchaDisabled_.value) {
      this.captchaRef.execute();
    } else {
      this.registerAccount();
    }
  }

  registerAccount() {
    this.updateSteps();
    this.appendComponentService.appendBackdropModal();
    this.appendComponentService.appendLoadingLogo();
    this.registerService.isRegistrationComplete_.next(false);

    this.postRegisterSubscription = this.registerService
      .callRegistrationEndpoint(this.generalRegForm.value, this.activeSiteId, this.isCheckout)
      .pipe(
        tap(() => {
          this.updateFormOnSuccess();
        }),
        catchError((res) => of(this.updateFormOnError(res))),
      )
      .subscribe();
  }

  updateFormOnError(res: HttpErrorResponse) {
    // Attach the error message here
    this.registerService.submitRegistrationError(res.error.errors[0].message);

    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeLoadingLogoFromBody();
    if (!this.isCaptchaDisabled_.value) {
      this.captchaRef.reset();
    }
  }

  updateFormOnSuccess() {
    this.errorMessage_.next('');
    this.generalRegForm.reset();
  }

  updateSteps() {
    if (this.generalRegForm.get('type').value === 'B2C') {
      this.stepsList_.next([
        { key: 'registration.general.account_type', active: true },
        { key: 'registration.general.your_details', active: true },
      ]);
    } else {
      this.stepsList_.next([
        { key: 'registration.general.account_type', active: true },
        { key: 'registration.b2b.company_title', active: true },
        { key: 'registration.general.your_details', active: true },
      ]);
    }
  }
}
