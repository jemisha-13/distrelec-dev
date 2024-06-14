import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { DistCookieService } from '@services/dist-cookie.service';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { SiteConfigService } from '@services/siteConfig.service';
import { Router } from '@angular/router';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ShoppingListPayloadItem } from '@model/shopping-list.model';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss'],
})
export class LoginModalComponent implements OnInit {
  @Input() data: {
    pageToRedirect: string;
    shoppingListPayload?: {
      payloadItems: ShoppingListPayloadItem[];
      itemListEntity: ItemListEntity;
    };
  };
  userLoginForm: UntypedFormGroup;
  faTimes = faTimes;

  loginServiceSubscription: Subscription;
  isCaptchaDisabled_: BehaviorSubject<boolean> = this.loginService.isCaptchaDisabled_;

  constructor(
    private fb: UntypedFormBuilder,
    private appendComponentService: AppendComponentService,
    private loginService: LoginService,
    private cookieService: DistCookieService,
    private siteConfig: SiteConfigService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.userLoginForm = this.fb.group({
      email: [this.cookieService.get('email') !== '' ? this.cookieService.get('email') : '', [Validators.required]],
      password: ['', Validators.required],
      rememberMe: this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
    });
  }

  hideModal() {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeLoginComponentFromBody();
  }

  onSubmit(token?: string) {
    if (!this.userLoginForm.valid) {
      return;
    }
    this.hideModal();

    if (this.data.pageToRedirect === 'shoppingListModal') {
      this.loginService.assignRedirectUrlAfterLogin(this.router.url);
    } else if (this.siteConfig.getCurrentPageTemplate() !== 'ImportToolPageTemplate') {
      this.loginService.assignRedirectUrlAfterLogin(this.data.pageToRedirect);
    }

    this.loginServiceSubscription = this.loginService
      .postLoginRequest(this.userLoginForm.value.email, this.userLoginForm.value.password, this.data.pageToRedirect)
      .pipe(
        take(1),
        tap(() => {
          if (this.data.pageToRedirect === 'shoppingListModal') {
            this.appendComponentService.appendShoppingListModal(
              this.data.shoppingListPayload.payloadItems,
              this.data.shoppingListPayload.itemListEntity,
            );
          }
        }),
      )
      .subscribe();

    if (this.userLoginForm.value.rememberMe) {
      this.cookieService.set('email', `${this.userLoginForm.value.email}`, {
        expires: 1,
      });
    } else {
      this.cookieService.set('email', '');
    }
    this.cookieService.set('rememberMe', `${this.userLoginForm.value.rememberMe}`);
    // We access the cookie storage and set the saved value to the form after it's being refreshed
    this.userLoginForm.patchValue({
      email: this.cookieService.get('email'),
      password: '',
      rememberMe: this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
    });
    this.userLoginForm.reset();
  }
}
