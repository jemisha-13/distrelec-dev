import { Component, Input } from '@angular/core';
import { PromoLabel } from './promo-label.model';

@Component({
  selector: 'app-promo-label',
  templateUrl: './promo-label.component.html',
  styleUrls: ['./promo-label.component.scss'],
})
export class PromoLabelComponent {
  @Input() promoLabel: PromoLabel;
}
