import { Component, Input } from '@angular/core';
import { faTimes, faAngleDown } from '@fortawesome/free-solid-svg-icons';
import { AppendComponentService } from '@services/append-component.service';

@Component({
  selector: 'app-shipping-tracking-modal',
  templateUrl: './shipping-tracking-modal.component.html',
  styleUrls: ['./shipping-tracking-modal.component.scss'],
})
export class ShippingTrackingModalComponent {
  @Input() deliveryDetails: any;

  @Input() data: {
    deliveryDetails?: any;
  };

  faTimes = faTimes;
  faAngleDown = faAngleDown;

  constructor(private appendComponentService: AppendComponentService) {}

  closeModal(status): void {
    this.appendComponentService.removeBackdropComponentFromBody();
    this.appendComponentService.removeShippingTrackingComponentFromBody();
  }
}
