import { Component, Input } from '@angular/core';
import { Price } from '@spartacus/core';

@Component({
  selector: 'app-prices-xuom',
  templateUrl: './prices-xuom.component.html',
  styleUrls: ['./prices-xuom.component.scss'],
})
export class PricesXUOMComponent {
  @Input() price: Price;
}
