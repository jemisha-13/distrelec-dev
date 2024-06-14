import { Component, Input } from '@angular/core';
import { Price } from '@spartacus/core';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { PriceService } from '@services/price.service';

@Component({
  selector: 'app-main-price',
  templateUrl: './main-price.component.html',
  styleUrls: ['./main-price.component.scss'],
})
export class MainPriceComponent {
  @Input() currentChannel: CurrentSiteSettings;
  @Input() price: Price;

  constructor(public priceService: PriceService) {}
}
