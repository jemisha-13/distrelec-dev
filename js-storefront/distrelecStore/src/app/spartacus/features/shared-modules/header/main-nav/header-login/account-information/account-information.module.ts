import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { I18nModule } from '@spartacus/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IfModule } from '@rx-angular/template/if';
import { ForModule } from '@rx-angular/template/for';
import { DistButtonModule } from 'src/app/shared-components/dist-button/dist-button.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { HeaderLoginService } from '../header-login.service';
import { HeaderAccountInformationComponent } from './account-information.component';

@NgModule({
  declarations: [HeaderAccountInformationComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    I18nModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    IfModule,
    ForModule,
    DistButtonModule,
    DistIconModule,
  ],
  exports: [HeaderAccountInformationComponent],
  providers: [HeaderLoginService],
})
export class HeaderAccountInformationModule {}
