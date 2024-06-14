import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManufacturersComponent } from './manufacturers.component';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@features/shared-modules/shared.module';
import { DistButtonModule } from 'src/app/shared-components/dist-button/dist-button.module';

@NgModule({
  declarations: [ManufacturersComponent],
  imports: [CommonModule, RouterModule, SharedModule, DistButtonModule],
  exports: [ManufacturersComponent],
})
export class ManufacturersModule {}
