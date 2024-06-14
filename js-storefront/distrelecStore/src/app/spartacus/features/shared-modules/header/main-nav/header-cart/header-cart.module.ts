import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { HeaderCartComponent } from './header-cart.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ForModule } from '@rx-angular/template/for';
import { IfModule } from '@rx-angular/template/if';
import { LetModule } from '@rx-angular/template/let';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';

@NgModule({
  declarations: [HeaderCartComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    RouterModule,
    ForModule,
    IfModule,
    LetModule,
    SlideDrawerModule,
    DistButtonComponentModule,
    DistIconModule,
  ],
  exports: [HeaderCartComponent],
})
export class HeaderCartModule {}
