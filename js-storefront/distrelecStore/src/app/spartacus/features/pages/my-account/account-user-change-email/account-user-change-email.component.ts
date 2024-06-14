import { AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Subscription } from 'rxjs';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { TranslationService } from '@spartacus/core';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-account-user-change-email',
  templateUrl: './account-user-change-email.component.html',
  styleUrls: ['./account-user-change-email.component.scss'],
})
export class AccountUserChangeEmailComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() userDetails$: BehaviorSubject<any>;

  userEmailChangeForm = new UntypedFormGroup({
    email: new UntypedFormControl('', [Validators.required, Validators.email]),
    password: new UntypedFormControl('', [Validators.required]),
  });

  faAngleRight = faAngleRight;
  updating = false;
  buttonDisabled = false;
  responseType;
  responseMessage;
  password;
  userNewEmail;
  subscription: Subscription;

  constructor(
    private distrelecUserService: DistrelecUserService,
    private loginService: LoginService,
    private translation: TranslationService,
  ) {}

  ngOnInit() {}

  ngAfterViewInit(): void {}

  changeUserEmail() {
    this.updating = true;
    this.buttonDisabled = true;
    this.responseMessage = '';
    this.responseType = '';

    const newEmail = this.userNewEmail;
    const password = this.password;

    //let's update the details now
    this.subscription = this.distrelecUserService
      .changeEmail(newEmail, password)
      .pipe(first())
      .subscribe(
        () => {
          // update user information on successful relogin to refresh token
          this.loginService.postLoginRequest(newEmail, password);
          // update user
          this.distrelecUserService.getUserInformation();
          this.userEmailChangeForm.controls.password.reset();
          this.userEmailChangeForm.controls.email.reset();
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            if (err.type === 'PasswordMismatchError') {
              this.responseMessage = 'form.incorrect_password';
            } else {
              this.responseMessage = err.message;
            }
          });

          this.updating = false;
          this.buttonDisabled = false;
        },
        () => {
          //completed
          this.responseType = 'success';
          this.translation
            .translate('form.email_updated')
            .pipe(first())
            .subscribe((val) => (this.responseMessage = val));
          this.updating = false;
          this.buttonDisabled = false;
          this.loginService.stopLoading();
        },
      );
  }

  ngOnDestroy() {
    if (this.subscription && !this.subscription.closed) {
      this.subscription.unsubscribe();
    }
  }
}
