import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginModalComponent } from './login-modal.component';
import { RECAPTCHA_SETTINGS, RecaptchaSettings } from 'ng-recaptcha';
import { I18nModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  providers: [
    {
      provide: RECAPTCHA_SETTINGS,
      useValue: {
        siteKey: '6LfjPCwUAAAAAH85BRYNleY5CCafloqqRoAw-afk',
      } as RecaptchaSettings,
    },
  ],
  imports: [CommonModule, I18nModule, RouterModule, ReactiveFormsModule, DistrelecRecaptchaModule, FontAwesomeModule],
  declarations: [LoginModalComponent],
})
export class LoginModalModule {}
