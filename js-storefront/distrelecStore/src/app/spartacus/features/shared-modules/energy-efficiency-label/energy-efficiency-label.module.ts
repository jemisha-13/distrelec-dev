import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnergyEfficiencyLabelComponent } from './energy-efficiency-label.component';
import { SharedModule } from '@features/shared-modules/shared.module';
import { EnergyEfficiencyImageComponent } from '../popups/energy-efficiency-image/energy-efficiency-image.component';

@NgModule({
  declarations: [EnergyEfficiencyLabelComponent, EnergyEfficiencyImageComponent],
  imports: [CommonModule, SharedModule],
  exports: [EnergyEfficiencyLabelComponent, EnergyEfficiencyImageComponent],
})
export class EnergyEfficiencyLabelModule {}
