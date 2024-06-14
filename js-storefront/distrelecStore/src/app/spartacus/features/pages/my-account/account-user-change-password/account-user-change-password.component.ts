import { AfterViewInit, Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { Subscription } from 'rxjs';
import { TranslationService } from '@spartacus/core';
import { first } from 'rxjs/operators';
import { LoginService } from '@services/login.service';
import { PasswordService } from '@services/password.service';

@Component({
  selector: 'app-account-user-change-password',
  templateUrl: './account-user-change-password.component.html',
  styleUrls: ['./account-user-change-password.component.scss'],
})
export class AccountUserChangePasswordComponent implements OnInit, AfterViewInit, OnDestroy {
  userPasswordChangeForm = new UntypedFormGroup(
    {
      currentPassword: new UntypedFormControl('', [Validators.required]),
      newPassword: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
    },
    this.passwordConfirming,
  );

  faAngleRight = faAngleRight;
  updating = false;
  buttonDisabled = false;
  responseType;
  responseMessage;
  currentPassword;
  newPassword;
  confirmPassword;

  private subscription: Subscription = new Subscription();

  constructor(
    private ngZone: NgZone,
    private translation: TranslationService,
    private loginService: LoginService,
    private passwordService: PasswordService,
  ) {}

  ngOnInit() {}

  ngAfterViewInit(): void {}

  changeUserPassword() {
    this.updating = true;
    this.buttonDisabled = true;
    this.responseMessage = '';
    this.responseType = '';

    const password = this.currentPassword;
    const newPassword = this.newPassword;
    const confirmPassword = this.confirmPassword;

    this.subscription.add(
      this.passwordService.changePassword(confirmPassword, password, newPassword).subscribe({
        error: (response) => {
          this.responseType = 'danger';
          response?.error?.errors.forEach((err) => {
            if (err.type === 'PasswordMismatchError') {
              this.responseMessage = 'form.incorrect_password';
              this.userPasswordChangeForm.controls.currentPassword.reset();
            } else {
              this.responseMessage = err.message;
            }
          });

          this.updating = false;
          this.buttonDisabled = false;
        },
        complete: () => {
          //completed
          this.responseType = 'success';

          this.translation
            .translate('form.password_updated')
            .pipe(first())
            .subscribe((val) => (this.responseMessage = val));
          this.ngZone.run(() => {
            setTimeout(() => {
              this.responseType = '';
              this.responseMessage = '';
            }, 5000);
          });

          this.updating = false;
          this.buttonDisabled = false;
          this.userPasswordChangeForm.reset();
          this.loginService.stopLoading();
        },
      }),
    );
  }

  ngOnDestroy() {
    if (this.subscription && !this.subscription.closed) {
      this.subscription.unsubscribe();
    }
  }

  passwordConfirming(c: AbstractControl): { invalid: boolean } {
    if (
      c.get('confirmPassword').value &&
      c.get('confirmPassword').value.length >= 6 &&
      c.get('newPassword').value !== c.get('confirmPassword').value
    ) {
      c.get('confirmPassword').setErrors({ noMatch: true });
      return { invalid: true };
    }
  }
}
