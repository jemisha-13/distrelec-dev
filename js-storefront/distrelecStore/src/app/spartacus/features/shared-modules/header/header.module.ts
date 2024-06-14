import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { HeaderLayoutModule } from './layout-config/layout-config.module';
import { LogoutModule } from '@spartacus/storefront';
import { ProgressBarModule } from './checkout-header/progress-bar/progress-bar.module';
import { MainNavModule } from './main-nav/main-nav.module';
import { HeaderListModule } from './main-nav/list/list.module';
import { HeaderOptionsModule } from './main-nav/options/options.module';

@NgModule({
  imports: [
    CommonModule,
    LogoutModule,
    HeaderLayoutModule,
    ProgressBarModule,
    FormsModule,
    FontAwesomeModule,
    MainNavModule,
    HeaderListModule,
    HeaderOptionsModule,
  ],
})
export class HeaderModule {}
