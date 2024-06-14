import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { HeaderLoginComponent } from './header-login.component';
import { I18nModule } from '@spartacus/core';
import { HeaderLoginService } from './header-login.service';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { HeaderLoginFormModule } from './login-form/login-form.module';
import { HeaderAccountInformationModule } from './account-information/account-information.module';

@NgModule({
  declarations: [HeaderLoginComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    I18nModule,
    SlideDrawerModule,
    DistIconModule,
    HeaderLoginFormModule,
    HeaderAccountInformationModule,
  ],
  exports: [HeaderLoginComponent],
  providers: [HeaderLoginService],
})
export class HeaderLoginModule {}
