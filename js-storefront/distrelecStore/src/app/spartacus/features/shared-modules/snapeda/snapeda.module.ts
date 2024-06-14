import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SnapedaComponent } from './snapeda.component';

@NgModule({
  declarations: [SnapedaComponent],
  imports: [CommonModule],
  exports: [SnapedaComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SnapedaModule {}
