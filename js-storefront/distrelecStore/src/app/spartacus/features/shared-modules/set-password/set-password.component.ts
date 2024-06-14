import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { validateAllFormFields } from '@helpers/form-helper';
import { Observable } from 'rxjs';
import { successTick } from '@assets/icons/definitions/success-tick';
import { DistIcon } from '@model/icon.model';
import { AppendComponentService } from '@services/append-component.service';
import { warning } from '@assets/icons/icon-index';

@Component({
  selector: 'app-set-password',
  templateUrl: './set-password.component.html',
  styleUrls: ['./set-password.component.scss'],
})
export class SetPasswordComponent {
  @Input() type: string;

  setPasswordForm: FormGroup = new FormGroup(
    {
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
    },
    this.passwordConfirming,
  );

  token: string;
  email: string;
  isEmailIncludedAsParam = false;

  successTick: DistIcon = successTick;
  warningIcon = warning;

  passwordVisible: boolean;
  confirmPasswordVisible: boolean;

  pageTitle$: Observable<string>;

  constructor(protected appendComponentService: AppendComponentService) {}

  onSubmit(): void {
    if (this.setPasswordForm.valid) {
      this.appendComponentService.startScreenLoading();
    } else {
      validateAllFormFields(this.setPasswordForm);
    }
  }

  private passwordConfirming(c: AbstractControl): { invalid: boolean } {
    if (c.get('password').value !== c.get('confirmPassword').value && c.get('confirmPassword').value.length >= 6) {
      c.get('confirmPassword').setErrors({ noMatch: true });
      return { invalid: true };
    }
  }
}
