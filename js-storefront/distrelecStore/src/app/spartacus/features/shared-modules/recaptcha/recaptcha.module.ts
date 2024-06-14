import { NgModule } from '@angular/core';
import { RECAPTCHA_SETTINGS, RecaptchaModule, RecaptchaSettings } from 'ng-recaptcha';
import { environment } from '@environment';

@NgModule({
  declarations: [],
  imports: [RecaptchaModule],
  providers: [
    {
      provide: RECAPTCHA_SETTINGS,
      useValue: {
        siteKey: environment.recaptchaKey,
      } as RecaptchaSettings,
    },
  ],
  exports: [RecaptchaModule],
})
export class DistrelecRecaptchaModule {}
