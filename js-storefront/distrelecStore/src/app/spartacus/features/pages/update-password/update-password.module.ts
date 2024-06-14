import { NgModule } from '@angular/core';
import { UpdatePasswordComponent } from './update-password.component';
import { provideConfig } from '@spartacus/core';
import { SetPasswordModule } from '@features/shared-modules/set-password/set-password.module';

@NgModule({
  declarations: [UpdatePasswordComponent],
  imports: [SetPasswordModule],
  providers: [
    provideConfig({
      cmsComponents: {
        UpdatePasswordComponent: {
          component: UpdatePasswordComponent,
        },
      },
    }),
  ],
})
export class UpdatePasswordModule {}
