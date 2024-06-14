import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dist-product-status',
  templateUrl: './product-status.component.html',
  styleUrls: ['./product-status.component.scss'],
})
export class ProductStatusComponent {
  @Input() filter:
    | 'x-in-stock'
    | 'available-to-order'
    | 'coming-soon'
    | 'pre-order-now'
    | 'currently-not-available'
    | 'no-longer-available' = 'x-in-stock';
  @Input() text = 'X in stock';

  @Input() ids = {
    statusId: 'status_text',
  };
}
