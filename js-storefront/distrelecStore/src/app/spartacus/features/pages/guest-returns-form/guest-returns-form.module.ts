import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GuestReturnsFormComponent } from './guest-returns-form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { BreadcrumbWrapperModule } from '@features/shared-modules/breadcrumb/breadcrumb-wrapper.module';
import { SharedModule } from '@features/shared-modules/shared.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { ButtonComponentModule } from '@features/shared-modules/components/button/button.module';

@NgModule({
  declarations: [GuestReturnsFormComponent],
  imports: [
    CommonModule,
    BreadcrumbWrapperModule,
    FontAwesomeModule,
    FormsModule,
    I18nModule,
    ReactiveFormsModule,
    SharedModule,
    NgSelectModule,
    DistrelecRecaptchaModule,
    ButtonComponentModule,
  ],
  exports: [GuestReturnsFormComponent],
  providers: [
    provideConfig({
      cmsComponents: {
        DistRMAGuestReturnsFormComponent: {
          component: GuestReturnsFormComponent,
        },
      },
    } as CmsConfig),
  ],
})
export class GuestReturnsFormModule {}
