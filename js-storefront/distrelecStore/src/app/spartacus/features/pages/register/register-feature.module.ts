import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterLayoutModule } from './layout-config/layout-config.module';
import { CmsConfig, HttpErrorHandler, provideConfig } from '@spartacus/core';
import { MyRegistrationHttpErrorHandler } from '@features/pages/register/registration-http-error.handler';

@NgModule({
  imports: [CommonModule, RegisterLayoutModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        registration: {
          module: () => import('./register.module').then((m) => m.RegisterModule),
          cmsComponents: ['RegistrationComponent'],
        },
      },
    }),
    {
      provide: HttpErrorHandler,
      useExisting: MyRegistrationHttpErrorHandler,
      multi: true,
    },
  ],
})
export class RegisterFeatureModule {}
