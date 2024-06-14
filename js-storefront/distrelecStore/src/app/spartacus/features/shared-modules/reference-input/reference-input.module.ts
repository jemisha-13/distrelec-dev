import { NgModule } from '@angular/core';
import { ReferenceInputComponent } from './reference-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [ReactiveFormsModule, I18nModule, CommonModule],
  declarations: [ReferenceInputComponent],
  exports: [ReferenceInputComponent],
})
export class ReferenceInputModule {}
