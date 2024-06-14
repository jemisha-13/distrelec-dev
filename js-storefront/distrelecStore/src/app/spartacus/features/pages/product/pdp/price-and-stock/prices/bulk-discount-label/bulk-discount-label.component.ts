import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bulk-discount-label',
  templateUrl: './bulk-discount-label.component.html',
  styleUrls: ['./bulk-discount-label.component.scss'],
})
export class BulkDiscountLabelComponent {
  @Input() discountPrices;
}
