import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-forgotten-pwd-form',
  templateUrl: './forgotten-pwd-form.component.html',
  styleUrls: ['./forgotten-pwd-form.component.scss'],
})
export class ForgottenPwdFormComponent {
  @Input() checkoutLoginForm: UntypedFormGroup;
  @Input() isErrorMessage_: BehaviorSubject<string | { country: string; href: string }>;
  @Input() isCaptchaDisabled_: BehaviorSubject<boolean>;
  @Input() showLoginForm_: BehaviorSubject<boolean>;

  @Input() validateFields: (emailForm: string) => void;

  isForgottenPwdSuccess_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(public checkoutService: CheckoutService) {}

  submitForgotPwd($event?): void {
    this.validateFields('forgotPwdEmail');
    if (this.checkoutLoginForm.get('forgotPwdEmail').value) {
      this.checkoutService
        .submitForgottenPwdCheckout(this.checkoutLoginForm.get('forgotPwdEmail').value)
        .subscribe(() => {
          this.isForgottenPwdSuccess_.next(true);
          this.checkoutService.isTokenExpired = false;
        });
    }
  }

  returnToLogin(): void {
    this.showLoginForm_.next(true);
    this.checkoutService.isTokenExpired = false;
  }
}
