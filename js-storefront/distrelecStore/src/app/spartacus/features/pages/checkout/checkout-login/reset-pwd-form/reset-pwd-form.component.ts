import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faEye, faEyeSlash, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CheckoutService } from '@services/checkout.service';
import { LoginService } from '@services/login.service';
import { PasswordService } from '@services/password.service';
import { BehaviorSubject } from 'rxjs';
import { PasswordUpdateResponse } from '@model/auth.model';

@Component({
  selector: 'app-reset-pwd-form',
  templateUrl: './reset-pwd-form.component.html',
  styleUrls: ['./reset-pwd-form.component.scss'],
})
export class ResetPwdFormComponent implements OnInit {
  @Input() resetPwdForm: UntypedFormGroup;
  @Input() isPwdResetSuccess_: BehaviorSubject<boolean>;
  @Input() showLoginForm_: BehaviorSubject<boolean>;
  faEye: IconDefinition = faEye;
  faEyeSlash: IconDefinition = faEyeSlash;

  isPasswordVisible: boolean;
  isConfrmPasswordVisible: boolean;
  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;

  constructor(
    private checkoutService: CheckoutService,
    private loginService: LoginService,
    private passwordService: PasswordService,
  ) {}

  ngOnInit(): void {}

  backToLogin(): void {
    this.resetPwdForm.removeControl('pwd1');
    this.resetPwdForm = null;
    this.showLoginForm_.next(true);
    this.checkoutService.isTokenExpired = false;
  }

  resetPwd(event?): void {
    this.passwordService
      .updatePassword(this.resetPwdForm.get('pwd2').value, this.checkoutService.resetPwdToken)
      .pipe()
      .subscribe((response: PasswordUpdateResponse) => {
        this.resetPwdForm.removeControl('pwd1');
        this.showLoginForm_.next(true);
        this.checkoutService.resetPwdToken = '';

        if (response.value === 'SUCCESS') {
          this.isPwdResetSuccess_.next(true);
        }
      });
  }
}
