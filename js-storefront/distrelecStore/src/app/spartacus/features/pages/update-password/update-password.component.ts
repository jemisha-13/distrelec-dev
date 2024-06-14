import { Component, OnInit } from '@angular/core';
import { SetPasswordComponent } from '@features/shared-modules/set-password/set-password.component';
import { map, tap } from 'rxjs/operators';
import { CmsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { PasswordService } from '@services/password.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppendComponentService } from '@services/append-component.service';
import { PasswordUpdateResponse } from '@model/auth.model';

@Component({
  selector: 'app-update-password',
  templateUrl: '../../shared-modules/set-password/set-password.component.html',
  styleUrls: ['../../shared-modules/set-password/set-password.component.scss'],
})
export class UpdatePasswordComponent extends SetPasswordComponent implements OnInit {
  constructor(
    protected appendComponentService: AppendComponentService,
    private activeRoute: ActivatedRoute,
    private pageService: CmsService,
    private passwordService: PasswordService,
    private router: Router,
  ) {
    super(appendComponentService);
    this.pageTitle$ = this.pageService.getCurrentPage().pipe(map((page) => page.title));
    this.token = this.activeRoute.snapshot.queryParams.token;
  }

  ngOnInit(): void {
    const tokenValidationStatus: Observable<boolean> = this.passwordService.isChangePasswordTokenValid(this.token);

    tokenValidationStatus
      .pipe(
        tap((result: boolean) => {
          if (!result) {
            this.passwordService.isPasswordResetTokenInvalid_.next(true);
            this.navigateToRequestPassword();
          }
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    super.onSubmit();

    if (this.setPasswordForm.valid) {
      this.passwordService.updatePassword(this.setPasswordForm.controls.password.value, this.token).subscribe({
        next: (result: PasswordUpdateResponse): void => {
          switch (result.value) {
            case 'SUCCESS':
              this.navigateToLogin();
              break;
            case 'TOKEN_INVALIDATED':
            case 'ERROR':
              this.navigateToRequestPassword();
              break;
            default:
              this.navigateToLogin();
              break;
          }
        },
      });
    }
  }

  private navigateToRequestPassword(): void {
    this.appendComponentService.stopScreenLoading();
    this.router.navigate(['/login/pw/request']);
  }

  private navigateToLogin(): void {
    this.appendComponentService.stopScreenLoading();
    this.router.navigate(['/login']);
  }
}
