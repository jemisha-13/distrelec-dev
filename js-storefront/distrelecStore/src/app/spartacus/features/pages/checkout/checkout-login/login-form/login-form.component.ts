import { Component, Input, OnDestroy, ViewEncapsulation } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faCheckSquare, faInfoCircle, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { LoginService } from '@services/login.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-login-form',
  styleUrls: ['./login-form.component.scss'],
  templateUrl: './login-form.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class LoginFormComponent implements OnDestroy {
  @Input() checkoutLoginForm: UntypedFormGroup;
  @Input() isErrorMessage_: BehaviorSubject<string | { country: string; href: string }>;
  @Input() isCaptchaDisabled_: BehaviorSubject<boolean>;
  @Input() showLoginForm_: BehaviorSubject<boolean>;
  @Input() isPwdResetSuccess_: BehaviorSubject<boolean>;

  @Input() validateFields: (emailForm: string) => void;
  @Input() triggerCheckbox: (rememberMe: string) => void;

  faSquare: IconDefinition = faSquare;
  faCheckSquare: IconDefinition = faCheckSquare;
  faInfoCircle: IconDefinition = faInfoCircle;

  constructor(
    private loginService: LoginService,
    private cookieService: DistCookieService,
  ) {}

  ngOnDestroy(): void {
    this.isPwdResetSuccess_.next(false);
    this.checkoutLoginForm.setErrors(null);
  }

  signIn(): void {
    this.validateFields('loginEmail');
    if (this.checkoutLoginForm.valid) {
      this.loginService
        .postLoginRequestAfterCheckout(this.checkoutLoginForm.value.loginEmail, this.checkoutLoginForm.value.password)
        .pipe(first())
        .subscribe({
          complete: () => {
            if (this.checkoutLoginForm.value.rememberMe) {
              this.cookieService.set('email', `${this.checkoutLoginForm.value.loginEmail}`, {
                expires: 1,
              });
            } else {
              this.cookieService.set('email', '');
            }
            this.cookieService.set('rememberMe', `${this.checkoutLoginForm.value.rememberMe}`);
            this.checkoutLoginForm.reset();

            // We access the cookie storage and set the saved value to the form after it's being refreshed
            this.checkoutLoginForm.patchValue({
              email: this.cookieService.get('email'),
              password: '',
              rememberMe:
                this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
            });
          },
        });
      // Override the current cookie with saved email to save this.email if checked
    }
  }

  setForgottenPwdValue(): void {
    this.showLoginForm_.next(false);
    if (this.checkoutLoginForm.get('loginEmail').value) {
      this.checkoutLoginForm.patchValue({ forgotPwdEmail: this.checkoutLoginForm.get('loginEmail').value });
    }
  }

  onCaptchaResolved(event, captchaRef) {
    captchaRef.ngOnDestroy();
    this.signIn();
  }
}
