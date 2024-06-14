import { NgModule } from '@angular/core';
import { SetInitialPasswordComponent } from '@features/pages/my-account/set-initial-password/set-initial-password.component';
import { CmsPageGuard } from '@spartacus/storefront';
import { mapToCanActivate, RouterModule } from '@angular/router';
import { SetPasswordModule } from '@features/shared-modules/set-password/set-password.module';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'setinitialpw',
        component: SetInitialPasswordComponent,
        canActivate: mapToCanActivate([CmsPageGuard]),
        pathMatch: 'full',
        data: {
          parent: [],
          breadcrumb: 'Set initial password',
          isChild: true,
          pageLabel: '/account/password/setinitialpw',
        },
      },
    ]),
    SetPasswordModule,
  ],
  declarations: [SetInitialPasswordComponent],
})
export class MyAccountPasswordModule {}
