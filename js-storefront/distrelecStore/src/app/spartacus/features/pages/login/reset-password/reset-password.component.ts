import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { DistCookieService } from '@services/dist-cookie.service';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription, throwError } from 'rxjs';
import { LoginService } from '@services/login.service';
import { catchError, tap } from 'rxjs/operators';
import { CountryCodesEnum, CountryService } from '@context-services/country.service';
import { Router } from '@angular/router';
import { PasswordService } from '@services/password.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  userLoginForm: UntypedFormGroup;
  faAngleRight = faAngleRight;
  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  requestSubmitted = false;
  requestError = false;
  passwordResetTokenInvalid = false;
  isHelpText: boolean;

  private activeCountrySubscription: Subscription;

  constructor(
    private fb: UntypedFormBuilder,
    private loginService: LoginService,
    private cookieService: DistCookieService,
    private changeDetectorRef: ChangeDetectorRef,
    private countryService: CountryService,
    private router: Router,
    private passwordService: PasswordService,
  ) {}

  ngOnInit(): void {
    this.activeCountrySubscription = this.countryService.getActive().subscribe((siteId) => {
      this.isHelpText = siteId === CountryCodesEnum.SWEDEN || siteId === CountryCodesEnum.NORWAY;
    });
    this.userLoginForm = this.fb.group({
      forgottenPwdEmail: [
        this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '',
        [Validators.email],
      ],
    });

    this.passwordResetTokenInvalid = this.passwordService.isPasswordResetTokenInvalid_.getValue();
  }

  ngOnDestroy(): void {
    this.passwordService.isPasswordResetTokenInvalid_.next(false);
    if (this.activeCountrySubscription && !this.activeCountrySubscription.closed) {
      this.activeCountrySubscription.unsubscribe();
    }
  }

  submitForgottenPwd() {
    this.passwordService
      .submitForgottenPwd(this.userLoginForm.get('forgottenPwdEmail').value)
      .pipe(
        tap((isError) => {
          if (!isError) {
            this.displaySuccessMessage();
          } else {
            this.displayErrorMessage();
          }
        }),
        catchError((error) => {
          this.displayErrorMessage();
          return throwError(error);
        }),
      )
      .subscribe();
  }

  private displaySuccessMessage() {
    this.passwordResetTokenInvalid = false;
    this.requestSubmitted = true;
    this.requestError = false;
    this.loginService.isErrorMessage_.next(null);
    this.passwordService.isLoginForgotPwdReset_.next(true);
    this.router.navigate(['login']);
  }

  private displayErrorMessage() {
    this.passwordResetTokenInvalid = false;
    this.requestSubmitted = false;
    this.requestError = true;
    this.changeDetectorRef.detectChanges();
  }
}
