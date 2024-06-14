import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReevooComponent } from './reevoo.component';

@NgModule({
  declarations: [ReevooComponent],
  imports: [CommonModule],
  exports: [ReevooComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ReevooModule {}
