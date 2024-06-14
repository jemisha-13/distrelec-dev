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
import { HeaderLoginFormComponent } from './login-form.component';
import { HeaderLoginService } from '../header-login.service';
import { CheckboxModule } from '@design-system/checkbox/checkbox.module';
import { DistTextFieldComponentModule } from '@design-system/text-field/text-field.module';

@NgModule({
  declarations: [HeaderLoginFormComponent],
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
    CheckboxModule,
    DistTextFieldComponentModule,
  ],
  exports: [HeaderLoginFormComponent],
  providers: [HeaderLoginService],
})
export class HeaderLoginFormModule {}
