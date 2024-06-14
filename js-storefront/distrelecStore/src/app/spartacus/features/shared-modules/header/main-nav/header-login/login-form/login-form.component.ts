import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DistCookieService } from '@services/dist-cookie.service';
import { LoginService } from 'src/app/spartacus/services/login.service';
import { filter, first, map, switchMap, takeUntil } from 'rxjs/operators';
import { NavigationEnd, Router } from '@angular/router';
import { LoginServiceHelper } from '@helpers/login-helpers';
import { SideMenuCountService } from '@services/side-menu-count.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { HeaderLoginService } from '../header-login.service';
import { UserIdService } from '@spartacus/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-header-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['../header-login-modal.scss'],
  providers: [HeaderLoginService],
})
export class HeaderLoginFormComponent implements OnInit, OnDestroy {
  @Input() redirectTo?: string;
  @Input() onAction = false;

  isFailedLogin = false;
  headerLoginForm: FormGroup;
  isPasswordVisible: boolean;

  private destroyed$ = new Subject<void>();

  constructor(
    private cookieService: DistCookieService,
    private loginServiceHelper: LoginServiceHelper,
    private loginService: LoginService,
    private router: Router,
    private sideMenuCountService: SideMenuCountService,
    private headerLoginService: HeaderLoginService,
    private slideDrawerService: SlideDrawerService,
    private userIdService: UserIdService,
  ) {
    this.loginServiceHelper.checkIfCaptchaDisabled(this.loginService.isCaptchaDisabled_);
  }

  ngOnInit(): void {
    this.headerLoginForm = this.headerLoginService.setHeaderLoginForm();
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroyed$),
      )
      .subscribe((event: NavigationEnd) => {
        this.isFailedLogin = event.urlAfterRedirects.includes('/login?error=true');
      });
  }

  onSubmit(): void {
    this.closePanel();
    if (this.isFormValid()) {
      this.loginService
        .postLoginRequest(this.headerLoginForm.value.email, this.headerLoginForm.value.password)
        .pipe(
          first(),
          switchMap(() => this.userIdService.getUserId()),
          map((userId) => {
            this.assignRememberMeCookie();
            this.headerLoginForm.reset();
            // We access the cookie storage and set the saved value to the form after it's being refreshed
            this.headerLoginForm.patchValue({
              email: this.cookieService.get('email'),
              password: '',
              rememberMe:
                this.cookieService.get('rememberMe') !== '' ? JSON.parse(this.cookieService.get('rememberMe')) : '',
            });
            this.sideMenuCountService.setSideMenuCount();

            if (this.onAction && userId === 'current' && !this.isFailedLogin) {
              this.slideDrawerService.openPanel(new Event('default'), 'add-to-list-drawer', 'RIGHT');
              this.onAction = false;
            }

            if (this.redirectTo) {
              this.router.navigate([this.redirectTo]);
            }
          }),
        )
        .subscribe();
    }
  }

  isFormValid(): boolean {
    return this.headerLoginForm.get('email').value && this.headerLoginForm.get('password').value;
  }

  assignRememberMeCookie(): void {
    if (this.headerLoginForm.value.rememberMe) {
      this.cookieService.set('email', `${this.headerLoginForm.value.email}`, { expires: 1 });
    } else {
      this.cookieService.set('email', '');
    }

    this.cookieService.set('rememberMe', `${this.headerLoginForm.value.rememberMe}`);
  }

  onRegistrationClicked(): void {
    this.loginService.assignRedirectUrlAfterLogin(this.router.url);
  }

  closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }
}
