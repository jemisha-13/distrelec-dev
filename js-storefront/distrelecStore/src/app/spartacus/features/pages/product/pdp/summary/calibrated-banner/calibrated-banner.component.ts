import { Component, Input, SimpleChanges } from '@angular/core';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-calibrated-banner',
  templateUrl: './calibrated-banner.component.html',
  styleUrls: ['./calibrated-banner.component.scss'],
})
export class CalibratedBannerComponent {
  @Input() calibratedProductCode: string;
  @Input() isCalibrated: boolean;
  faAngleRight = faAngleRight;

  productCalibratedLabel: string;
  productCalibratedView: string;

  ngOnChanges(changes: SimpleChanges): void {
    this.calibratedLabelStatus(changes.isCalibrated.currentValue);
  }

  calibratedLabelStatus(calibrated: boolean): void {
    this.productCalibratedLabel = `product.calibrated_label_${calibrated}`;
    this.productCalibratedView = `product.calibrated_view_${calibrated}`;
  }
}
