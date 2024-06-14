import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetPasswordComponent } from '@features/shared-modules/set-password/set-password.component';
import { DistCardComponentModule } from '@design-system/card/card.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [SetPasswordComponent],
  imports: [
    CommonModule,
    I18nModule,
    ReactiveFormsModule,
    DistCardComponentModule,
    DistIconModule,
    DistButtonComponentModule,
  ],
  exports: [
    SetPasswordComponent,
    CommonModule,
    I18nModule,
    ReactiveFormsModule,
    DistCardComponentModule,
    DistIconModule,
    DistButtonComponentModule,
  ],
})
export class SetPasswordModule {}
