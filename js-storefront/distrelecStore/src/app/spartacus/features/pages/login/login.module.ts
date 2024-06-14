import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login.component';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReactiveFormsModule } from '@angular/forms';
import { BreadcrumbWrapperModule } from '../../shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { RouterModule } from '@angular/router';
import { ResetPasswordComponent } from './reset-password/reset-password.component';

@NgModule({
  declarations: [LoginComponent, ResetPasswordComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    I18nModule,
    DistrelecRecaptchaModule,
    BreadcrumbWrapperModule,
    RouterModule,
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        LoginComponent: {
          component: LoginComponent,
        },
        ResetPasswordComponent: {
          component: ResetPasswordComponent,
        },
      },
    }),
  ],
})
export class LoginModule {}
