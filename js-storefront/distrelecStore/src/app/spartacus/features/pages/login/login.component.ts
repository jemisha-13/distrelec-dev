import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription } from 'rxjs';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { DistCookieService } from '@services/dist-cookie.service';
import { RoutingService } from '@spartacus/core';
import { first } from 'rxjs/operators';
import { PasswordService } from '@services/password.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, AfterViewInit, OnDestroy {
  userLoginForm: UntypedFormGroup;
  faAngleRight = faAngleRight;
  isErrorBorder: boolean;
  loginFormSubmits = 0;
  isErrorMessage_: BehaviorSubject<string | { country: string; href: string }> = this.loginService.isErrorMessage_;
  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  isPasswordChanged = true;
  isForgottenPwdSuccess_: BehaviorSubject<boolean> = this.passwordService.isLoginForgotPwdReset_;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private fb: UntypedFormBuilder,
    private loginService: LoginService,
    private cookieService: DistCookieService,
    private routingService: RoutingService,
    private passwordService: PasswordService,
  ) {}

  subscribeCaptchaResolvedFallback(event?): void {
    if (!!event) {
      this.onSubmit();
      grecaptcha.reset();
    }
  }

  submitSubscribeClick(recaptcha): void {
    // Increase submit counter
    this.loginFormSubmits++;
    // If user clicks more than 3 times on submit button, execute captcha first
    // Otherwise execute request
    if (this.loginFormSubmits > 3 && !this.isCaptchaDisabled_.value) {
      recaptcha.execute();
    } else {
      this.onSubmit();
    }
  }

  onSubmit(): void {
    this.isErrorBorder = false;
    this.isForgottenPwdSuccess_.next(false);

    this.loginService
      .postLoginRequest(this.userLoginForm.value.email, this.userLoginForm.value.password)
      .pipe(first())
      .subscribe({
        complete: () => {
          this.handleRememberEmail();
          // We access the cookie storage and set the saved value to the form after it's being refreshed
          this.userLoginForm.patchValue({
            email: this.cookieService.get('email'),
            password: '',
            rememberMe:
              this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
          });
          this.userLoginForm.get('email').setErrors(null);
          this.userLoginForm.get('password').setErrors(null);
        },
      });
  }

  // Override the current cookie with saved email to save this.email if checked
  handleRememberEmail() {
    if (this.userLoginForm.value.rememberMe) {
      this.cookieService.set('email', `${this.userLoginForm.value.email}`, {
        expires: 1,
      });
    } else {
      this.cookieService.set('email', '');
    }
    this.cookieService.set('rememberMe', `${this.userLoginForm.value.rememberMe}`);
  }

  onForgottenPwd() {
    this.routingService.goByUrl('/login/pw/request');
  }

  goToAccountLink(url): void {
    this.routingService.goByUrl(url);
  }

  ngOnInit() {
    this.userLoginForm = this.fb.group({
      email: [this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '', [Validators.required]],
      password: ['', Validators.required],
      rememberMe: this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
    });
    const afterPasswordChange = this.passwordService.isPasswordReseted_.getValue();
    this.isPasswordChanged = afterPasswordChange;
  }

  ngAfterViewInit(): void {
    this.subscriptions.add(
      this.loginService.isErrorMessage_.subscribe((data) => {
        if (data) {
          this.isErrorBorder = true;
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.passwordService.isPasswordReseted_.next(false);
  }
}
