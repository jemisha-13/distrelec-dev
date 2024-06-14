import { Component, OnInit } from '@angular/core';
import { SetPasswordComponent } from '@features/shared-modules/set-password/set-password.component';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { AppendComponentService } from '@services/append-component.service';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { from, Observable } from 'rxjs';
import { createFrom, EventService, GlobalMessageService, GlobalMessageType, TranslationService } from '@spartacus/core';
import { EventHelper } from '@features/tracking/event-helper.service';
import { SetPasswordEvent } from '@features/tracking/events/set-password-event';
import { LoginService } from '@services/login.service';
import { PasswordService } from '@services/password.service';

import { SetInitialPasswordRequest } from '@model/auth.model';

@Component({
  selector: 'app-set-initial-password',
  templateUrl: '../../../shared-modules/set-password/set-password.component.html',
  styleUrls: ['../../../shared-modules/set-password/set-password.component.scss'],
})
export class SetInitialPasswordComponent extends SetPasswordComponent implements OnInit {
  constructor(
    protected appendComponentService: AppendComponentService,
    private activeRoute: ActivatedRoute,
    private eventHelper: EventHelper,
    private eventService: EventService,
    private globalMessageService: GlobalMessageService,
    private loginService: LoginService,
    private router: Router,
    private translation: TranslationService,
    private passwordService: PasswordService,
  ) {
    super(appendComponentService);
    this.pageTitle$ = this.translation.translate('setInitialPwd.title');
  }

  ngOnInit(): void {
    this.activeRoute.queryParams
      .pipe(
        tap(() => this.appendComponentService.startScreenLoading()),
        filter(Boolean),
        take(1),
        tap((params: Params) => {
          this.token = params.token;
          this.email = params.email;
          this.isEmailIncludedAsParam = !!params.email;
        }),
        switchMap(() => this.checkIsTokenValid()),
      )
      .subscribe();
  }

  onSubmit(): void {
    super.onSubmit();

    if (this.setPasswordForm.valid) {
      const payload: SetInitialPasswordRequest = {
        checkPassword: this.setPasswordForm.get('password').value,
        password: this.setPasswordForm.get('confirmPassword').value,
        token: this.token,
        migration: this.isEmailIncludedAsParam,
      };

      this.passwordService
        .setInitialPasswordRequest(payload)
        .pipe(
          tap(() => this.handleSetPasswordEventTracking()),
          switchMap(() => {
            if (!this.isEmailIncludedAsParam) {
              this.globalMessageService.add(
                'Your password has been changed. Please log in to access your account',
                GlobalMessageType.MSG_TYPE_CONFIRMATION,
                5000,
              );
              return this.router.navigate(['./login']).then(() => this.appendComponentService.stopScreenLoading());
            } else {
              this.eventService.dispatch(createFrom(SetPasswordEvent, { event: 'set_password' }));
              return this.loginService.postLoginRequest(
                this.email,
                this.setPasswordForm.get('password').value,
                'rs-welcome',
              );
            }
          }),
          catchError(() => this.redirectToForgottenPasswordAndShowWarning()),
        )
        .subscribe();
    }
  }

  private checkIsTokenValid(): Observable<boolean> {
    return this.passwordService.isInitialPasswordTokenValid(this.token, this.isEmailIncludedAsParam).pipe(
      take(1),
      tap((isValid: boolean) => {
        if (!isValid) {
          this.redirectToForgottenPasswordAndShowWarning();
        }
      }),
      tap(() => {
        this.appendComponentService.stopScreenLoading();
        this.handleEventTracking();
      }),
      catchError(() => this.redirectToForgottenPasswordAndShowWarning()),
    );
  }

  private redirectToForgottenPasswordAndShowWarning(): Observable<boolean> {
    return from(
      this.router.navigate(['/login/pw/request']).then(() => {
        this.globalMessageService.add({ key: 'account.activation.token.expired' }, GlobalMessageType.MSG_TYPE_WARNING);
        this.appendComponentService.stopScreenLoading();
        return false;
      }),
    );
  }

  private handleEventTracking(): void {
    this.eventHelper.trackBloomreachPasswordPageViewEvent(this.email);
  }

  private handleSetPasswordEventTracking(): void {
    this.eventHelper.trackBloomreachSetPasswordEvent(this.email);
  }
}
