import { Component, Input } from '@angular/core';

export type PopupAlignment = 'left' | 'center' | 'right';
export type PopupPosition = 'right' | 'top';

@Component({
  selector: 'app-min-order-quantity-popup',
  templateUrl: './min-order-quantity-popup.component.html',
  styleUrls: ['./min-order-quantity-popup.component.scss'],
})
export class MinOrderQuantityPopupComponent {
  @Input() minQuantity = 1;
  @Input() productCode?: string;
  @Input() translationKey? = 'searchResults.moqMsg';
  @Input() alignment?: PopupAlignment = 'center';
  @Input() row?: string;
  @Input() position?: PopupPosition = 'top';
}
