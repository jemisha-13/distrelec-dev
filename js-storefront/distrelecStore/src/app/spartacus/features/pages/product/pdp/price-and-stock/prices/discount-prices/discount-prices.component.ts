import { Component, Input } from '@angular/core';
import { Price } from '@spartacus/core';
import { VolumePriceMap } from '@model/price.model';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { PriceService } from '@services/price.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-discount-prices',
  templateUrl: './discount-prices.component.html',
  styleUrls: ['./discount-prices.component.scss'],
})
export class DiscountPricesComponent {
  @Input() price: Price;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() volumePricesMap: VolumePriceMap[];

  constructor(public priceService: PriceService) {}
}
