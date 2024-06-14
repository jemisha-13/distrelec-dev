import { Component, ElementRef, EventEmitter, Input, Output } from '@angular/core';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { AppendComponentService } from '@services/append-component.service';
import { EnergyEfficiencyImageComponent } from '../popups/energy-efficiency-image/energy-efficiency-image.component';

type SizeType = 'large' | 'small';

@Component({
  selector: 'app-energy-efficiency-label',
  templateUrl: './energy-efficiency-label.component.html',
  styleUrls: ['./energy-efficiency-label.component.scss'],
})
export class EnergyEfficiencyLabelComponent {
  @Input() id: string;
  @Input() energyEfficiency: string;
  @Input() arrowSize: SizeType = 'large';
  @Input() eelImageSize: SizeType = 'large';
  @Input() energyEfficiencyLabelImageUrl: string;
  @Input() isSearchPopup? = false;
  @Input() isQuickOrder = false;
  @Output() popupState: EventEmitter<{ id: string; callback: () => void }> = new EventEmitter();

  constructor(
    private energyEfficiencyLabelService: EnergyEfficiencyLabelService,
    private appendComponentService: AppendComponentService,
    private el: ElementRef,
  ) {}

  togglePopup(): void {
    // Energy efficiency label can be opened from the search flyout, in this case we don't need to replace the backdrop
    this.appendComponentService.appendBackdropModal(
      {
        opacity: '025',
      },
      this.isSearchPopup ? this.el.nativeElement : null,
    );

    this.appendComponentService.appendEnergyEfficiencyModal(
      this.eelImageSize,
      this.energyEfficiencyLabelImageUrl,
      this.isSearchPopup,
    );
  }

  isValidEnergyEfficiencyLabel(): boolean {
    return this.energyEfficiencyLabelService.isValid(this.energyEfficiency);
  }
}
