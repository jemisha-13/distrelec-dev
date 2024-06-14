import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { LetModule } from '@rx-angular/template/let';
import { CommonModule } from '@angular/common';
import { HeaderListComponent } from './list.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { HeaderLoginFormModule } from '../header-login/login-form/login-form.module';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { DistTextFieldComponentModule } from '@design-system/text-field/text-field.module';
import { DistButtonModule } from 'src/app/shared-components/dist-button/dist-button.module';
import { ShoppingListAddToListModule } from '@features/pages/shopping-list/add-to-shopping-list/add-to-shopping-list.module';

@NgModule({
  declarations: [HeaderListComponent],
  imports: [
    FontAwesomeModule,
    I18nModule,
    CommonModule,
    DistIconModule,
    SlideDrawerModule,
    HeaderLoginFormModule,
    AlertBannerComponentModule,
  ],
  exports: [HeaderListComponent],
})
export class HeaderListModule {}
