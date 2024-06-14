import { NgModule } from '@angular/core';
import { HttpErrorHandler } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { MyAccountHttpErrorHandler } from '@features/pages/my-account/my-account-http-error.handler';
import { AccountInformationLayoutModule } from '@features/pages/my-account/layout-config/layout-config.module';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'my-account',
        loadChildren: () => import('./account-information.module').then((m) => m.AccountInformationModule),
      },
      {
        path: 'account/password',
        loadChildren: () => import('./my-account-password.module').then((m) => m.MyAccountPasswordModule),
      },
    ]),
    AccountInformationLayoutModule,
  ],
  providers: [
    {
      provide: HttpErrorHandler,
      useExisting: MyAccountHttpErrorHandler,
      multi: true,
    },
  ],
})
export class MyAccountFeatureModule {}
