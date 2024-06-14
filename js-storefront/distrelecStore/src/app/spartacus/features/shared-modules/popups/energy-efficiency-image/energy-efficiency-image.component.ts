import { Component, Input } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-energy-efficiency-image',
  templateUrl: './energy-efficiency-image.component.html',
  styleUrls: ['./energy-efficiency-image.component.scss'],
})
export class EnergyEfficiencyImageComponent {
  @Input() data: {
    eelImageSize: string;
    energyEfficiencyLabelImageUrl?: any;
    isSearchPopup?: boolean;
  };

  constructor(private appendComponentService: AppendComponentService) {}

  closePopup() {
    this.appendComponentService.removeBackdropComponentFromBody(
      this.data.isSearchPopup ? 'APP-ENERGY-EFFICIENCY-LABEL' : null,
    );
    this.appendComponentService.removeEnergyEfficiencyModal();
  }
}
