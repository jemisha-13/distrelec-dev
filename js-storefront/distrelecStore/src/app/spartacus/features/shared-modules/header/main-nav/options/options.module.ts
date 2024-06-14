import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CmsConfig, ConfigModule, I18nModule } from '@spartacus/core';
import { HeaderOptionsComponent } from './options.component';
import { LetModule } from '@rx-angular/template/let';
import { CommonModule } from '@angular/common';
import { HeaderCartModule } from '../header-cart/header-cart.module';
import { HeaderLoginModule } from '../header-login/header-login.module';
import { HeaderListModule } from '../list/list.module';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { ShoppingListAddToListModule } from '@features/pages/shopping-list/add-to-shopping-list/add-to-shopping-list.module';
import { AlertBannerComponentModule } from '@design-system/alert-banner/alert-banner.module';
import { HeaderLoginFormModule } from '../header-login/login-form/login-form.module';

@NgModule({
  declarations: [HeaderOptionsComponent],
  imports: [
    FontAwesomeModule,
    I18nModule,
    CommonModule,
    LetModule,
    HeaderCartModule,
    HeaderLoginModule,
    HeaderListModule,
    ShoppingListAddToListModule,
    SlideDrawerModule,
    AlertBannerComponentModule,
    HeaderLoginFormModule,
    ConfigModule.forRoot({
      cmsComponents: {
        MiniCartComponent: {
          //TODO backend to have one options component
          component: HeaderOptionsComponent,
        },
      },
    } as CmsConfig),
  ],
  exports: [HeaderOptionsComponent],
})
export class HeaderOptionsModule {}
