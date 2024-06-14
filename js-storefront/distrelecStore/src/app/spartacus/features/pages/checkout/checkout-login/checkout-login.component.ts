import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faArrowLeft, faCheck, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { DistCookieService } from '@services/dist-cookie.service';
import { BehaviorSubject } from 'rxjs';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { Router } from '@angular/router';
import { BaseSiteService } from '@spartacus/core';
import { AppendComponentService } from '@services/append-component.service';
import { SiteIdEnum } from '@model/site-settings.model';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-checkout-login',
  templateUrl: './checkout-login.component.html',
  styleUrls: ['./checkout-login.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CheckoutLoginComponent implements OnInit {
  checkoutLoginForm: UntypedFormGroup;
  resetPwdForm: UntypedFormGroup;
  countryCode: string;

  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;
  isErrorMessage_ = this.loginService.isErrorMessage_;
  showLoginForm_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  isPwdResetSuccess_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  isGuestEnabled: boolean;
  isExportShop: boolean;
  enableRegistration: boolean;

  faCheck: IconDefinition = faCheck;
  faArrowLeft: IconDefinition = faArrowLeft;

  constructor(
    private appendComponentService: AppendComponentService,
    private fb: UntypedFormBuilder,
    private cookieService: DistCookieService,
    private loginService: LoginService,
    private checkoutService: CheckoutService,
    private router: Router,
    private siteService: BaseSiteService,
  ) {}

  ngOnInit(): void {
    this.checkoutLoginForm = this.fb.group({
      loginEmail: new UntypedFormControl(
        this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '',
        { validators: [Validators.required] },
      ),
      forgotPwdEmail: new UntypedFormControl(
        this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '',
        { validators: [Validators.email] },
      ),
      guestCheckoutEmail: new UntypedFormControl('', { validators: [Validators.email] }),
      password: new UntypedFormControl('', {
        validators: [Validators.required],
      }),
      rememberMe: this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
    });

    // If user has on login/checkout/pw/change display correct form according to token validation
    if (this.checkoutService.resetPwdToken) {
      this.resetPwdForm = this.fb.group({
        pwd1: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
        pwd2: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
      });
      this.showLoginForm_.next(false);
    } else if (this.checkoutService.isTokenExpired) {
      this.showLoginForm_.next(false);
    }

    this.checkoutService.checkoutPageSteps_.next({ loginRegisterStep: 'current' });
    this.getActiveBaseSite();
  }

  getActiveBaseSite(): void {
    this.siteService
      .get()
      .pipe(take(1))
      .subscribe((baseSite: any) => {
        this.isGuestEnabled = baseSite.guestCheckoutEnabled;
        this.isExportShop = baseSite.uid === SiteIdEnum.EX;
        this.enableRegistration = baseSite.enableRegistration;
      });
  }

  triggerCheckbox(consent: any) {
    this.checkoutLoginForm.get(consent).setValue(!this.checkoutLoginForm.get(consent).value);
  }

  onRegisterClick(): void {
    this.router.navigate(['/registration/checkout']);
  }

  validateFields(emailFormName: string): void {
    if (!this.checkoutLoginForm.get(emailFormName).value) {
      this.checkoutLoginForm.controls[emailFormName].markAsTouched();
      this.checkoutLoginForm.controls[emailFormName].setErrors({ incorrect: true });
    } else if (!this.checkoutLoginForm.get('password').value) {
      this.checkoutLoginForm.controls.password.markAsTouched();
      this.checkoutLoginForm.controls.password.setErrors({ incorrect: true });
    }
  }
}
