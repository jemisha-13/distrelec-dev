import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NotLoggedInAuthGuard } from '@features/guards/not-logged-in-auth.guard';
import { RegistrationGuard } from '@features/guards/registration.guard';
import { RouterModule } from '@angular/router';

import { RegisterComponent } from './register.component';
import { B2cFormComponent } from './b2c-form/b2c-form.component';
import { B2bFormComponent } from './b2b-form/b2b-form.component';

// Shared components
import { CommunicationPrefComponent } from './shared-components/communication-pref/communication-pref.component';
import { AboutYouFormComponent } from './shared-components/about-you-form/about-you-form.component';

// B2B components
import { CustomerNumberComponent } from './b2b-form/customer-number/customer-number.component';
import { VatValidationComponent } from './b2b-form/vat-validation/vat-validation.component';
import { CountryListComponent } from './b2b-form/country-list/country-list.component';
import { ItBisnodeFormComponent } from './b2b-form/it-bisnode-form/it-bisnode-form.component';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { PasswordStrengthBarModule } from '@features/shared-modules/components/password-strength-bar/password-strength-bar.module';
import { CountrySelectComponent } from './shared-components/about-you-form/country-select/country-select.component';

@NgModule({
  imports: [
    CommonModule,
    ConfigModule.forRoot({
      cmsComponents: {
        RegistrationComponent: {
          component: RegisterComponent,
          guards: [NotLoggedInAuthGuard, RegistrationGuard],
        },
      },
    } as CmsConfig),
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    FontAwesomeModule,
    DistrelecRecaptchaModule,
    PasswordStrengthBarModule,
    RouterModule,
  ],
  declarations: [
    RegisterComponent,
    B2cFormComponent,
    B2bFormComponent,
    CustomerNumberComponent,
    VatValidationComponent,
    AboutYouFormComponent,
    CommunicationPrefComponent,
    CountryListComponent,
    ItBisnodeFormComponent,
    CountrySelectComponent,
  ],
  exports: [
    RegisterComponent,
    B2cFormComponent,
    B2bFormComponent,
    CustomerNumberComponent,
    VatValidationComponent,
    AboutYouFormComponent,
    CommunicationPrefComponent,
    CountryListComponent,
    ItBisnodeFormComponent,
    CountrySelectComponent,
  ],
  providers: [],
})
export class RegisterModule {}
