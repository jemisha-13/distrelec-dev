import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TextFieldComponent } from './text-field.component';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [CommonModule, DistIconModule, ReactiveFormsModule, FormsModule],
  declarations: [TextFieldComponent],
  exports: [TextFieldComponent],
})
export class DistTextFieldComponentModule {}
