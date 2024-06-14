import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PasswordStrengthBarComponent } from '@features/shared-modules/components/password-strength-bar/password-strength-bar.component';

@NgModule({
  imports: [CommonModule],
  exports: [PasswordStrengthBarComponent],
  declarations: [PasswordStrengthBarComponent],
})
export class PasswordStrengthBarModule {}
