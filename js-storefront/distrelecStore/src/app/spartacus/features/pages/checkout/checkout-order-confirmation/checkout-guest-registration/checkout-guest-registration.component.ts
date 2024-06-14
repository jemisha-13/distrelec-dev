import { Component, Input, OnInit } from '@angular/core';
import { Order } from '@spartacus/order/root';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { of } from 'rxjs';
import { RecaptchaComponent } from 'ng-recaptcha';
import { catchError, switchMap, take } from 'rxjs/operators';
import { CheckoutService } from '@services/checkout.service';
import { DistGuestUserService } from '@services/guest-user.service';
import { Router } from '@angular/router';
import { AppendComponentService } from '@services/append-component.service';
import { DistLogoutService } from '@services/logout.service';
import { User } from '@spartacus/core';

@Component({
  selector: 'app-checkout-guest-registration',
  templateUrl: './checkout-guest-registration.component.html',
  styleUrls: ['./checkout-guest-registration.component.scss'],
})
export class CheckoutGuestRegistrationComponent implements OnInit {
  @Input() order: Order;
  @Input() customerUid: string;

  checkoutGuestRegistrationForm: FormGroup;
  passwordVisible: boolean;
  passwordConfirmVisible: boolean;
  passwordFieldValue: string = '';
  submitFormCounter: number = 0;

  constructor(
    private appendComponentService: AppendComponentService,
    private checkoutService: CheckoutService,
    private guestUserService: DistGuestUserService,
    private logoutService: DistLogoutService,
    private formBuilder: FormBuilder,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.logoutService.logoutGuestUser();
    this.checkoutGuestRegistrationForm = this.formBuilder.group({
      guestRegisterPassword: new FormControl(null, {
        validators: [Validators.required, Validators.minLength(6)],
      }),
      guestRegisterPasswordConfirm: new FormControl(null, {
        validators: [Validators.required, Validators.minLength(6)],
      }),
    });
  }

  registerButtonClick(recaptcha: RecaptchaComponent): void {
    this.submitFormCounter++;
    this.appendComponentService.startScreenLoading();

    if (this.submitFormCounter > 3) {
      recaptcha.execute();
    } else {
      this.formSubmit();
    }
  }

  formSubmit(): void {
    if (this.isFormValid()) {
      this.checkoutService
        .createDistrelecAccountFromGuest({
          password: this.getPasswordField().value,
          checkPwd: this.getPasswordConfirmField().value,
          uid: this.customerUid,
          guid: this.order.guid,
        })
        .pipe(
          take(1),
          // Login user with credentials and redirect to welcome page
          switchMap((userInfo: User) =>
            this.guestUserService.loginRequestAfterGuestRegistration(
              userInfo,
              this.customerUid,
              this.getPasswordField().value,
            ),
          ),
          catchError(() => {
            this.appendComponentService.stopScreenLoading();
            return of(false);
          }),
        )
        .subscribe();
    }
  }

  captchaResolvedFallback(event: string): void {
    if (!!event) {
      this.formSubmit();
      grecaptcha.reset();
    } else {
      this.appendComponentService.stopScreenLoading();
    }
  }

  getPasswordField(): AbstractControl {
    return this.checkoutGuestRegistrationForm.get('guestRegisterPassword');
  }

  getPasswordConfirmField(): AbstractControl {
    return this.checkoutGuestRegistrationForm.get('guestRegisterPasswordConfirm');
  }

  getPasswordFieldValue(): string {
    return this.getPasswordField().value;
  }

  getPasswordConfirmFieldValue(): string {
    return this.getPasswordConfirmField().value;
  }

  isPasswordFieldValid(): boolean {
    return this.getPasswordField().valid;
  }

  isPasswordConfirmFieldValid(): boolean {
    return this.getPasswordConfirmField().valid;
  }

  hasPasswordFieldErrors(): boolean {
    return !!this.getPasswordField().errors;
  }

  arePasswordsEqual(): boolean {
    return this.getPasswordFieldValue() === this.getPasswordConfirmFieldValue();
  }

  isFormValid(): boolean {
    return this.isPasswordFieldValid() && this.isPasswordConfirmFieldValid() && this.arePasswordsEqual();
  }

  redirectToRegistrationPage(): void {
    this.router.navigate(['/registration']);
  }

  onPasswordFieldKeyup(event: Event): void {
    this.passwordFieldValue = (event.target as HTMLInputElement).value;
  }
}
