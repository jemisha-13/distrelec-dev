import { Component, Input } from '@angular/core';
import { PriceService } from '@services/price.service';
import { Price } from '@spartacus/core';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { VolumePriceMap } from '@model/price.model';

@Component({
  selector: 'app-volume-prices',
  templateUrl: './volume-prices.component.html',
})
export class VolumePricesComponent {
  @Input() price: Price;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() volumePricesMap: VolumePriceMap[];

  constructor(public priceService: PriceService) {}
}
