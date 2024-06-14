import { NgModule } from '@angular/core';
import { CmsConfig, HttpErrorHandler, provideConfig } from '@spartacus/core';
import { LoginGuard } from '@spartacus/storefront';
import { DistrelecLoginGuard } from '@features/pages/login/guards/distrelec-login.guard';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { LoginHttpErrorHandler } from './login-http-error.handler';
import { LoginLayoutModule } from '@features/pages/login/layout-config/layout-config.module';

@NgModule({
  imports: [LoginLayoutModule],
  providers: [
    provideConfig({
      featureModules: {
        login: {
          module: () => import('@features/pages/login/login.module').then((m) => m.LoginModule),
          cmsComponents: ['LoginComponent', 'ResetPasswordComponent'],
        },
      },
    } as CmsConfig),
    // Overriding "LoginGuard" from Spartacus with our custom guard which also contains logic for checking if user is GUEST
    {
      provide: LoginGuard,
      useClass: DistrelecLoginGuard,
    },
    {
      provide: HttpErrorHandler,
      useExisting: LoginHttpErrorHandler,
      multi: true,
    },
  ],
})
export class LoginFeatureModule {}
