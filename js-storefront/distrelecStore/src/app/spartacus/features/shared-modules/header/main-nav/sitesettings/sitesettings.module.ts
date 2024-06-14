import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SiteSettingsComponent } from './sitesettings.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { ForModule } from '@rx-angular/template/for';
import { IfModule } from '@rx-angular/template/if';
import { LetModule } from '@rx-angular/template/let';
import { SlideDrawerModule } from '@design-system/slide-drawer/slide-drawer.module';
import { RadioModule } from '@design-system/radio/radio.module';

@NgModule({
  declarations: [SiteSettingsComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    FormsModule,
    NgSelectModule,
    ReactiveFormsModule,
    I18nModule,
    ForModule,
    IfModule,
    LetModule,
    SlideDrawerModule,
    RadioModule,
  ],
  exports: [SiteSettingsComponent, FormsModule],
})
export class SiteSettingsModule {}
