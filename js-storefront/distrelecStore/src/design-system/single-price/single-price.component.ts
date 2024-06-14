import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { DecimalPlacesPipe } from '@features/shared-modules/pipes/decimal-places-pipe';
import { VolumePricePipe } from '@features/shared-modules/pipes/volume-price.pipe';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-dist-single-price',
  templateUrl: './single-price.component.html',
  styleUrls: ['./single-price.component.scss'],
})
export class SinglePriceComponent implements OnInit, OnChanges {
  @Input() price: number;
  @Input() basePrice?: number;
  @Input() currency: string;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() componentId = 'pdp';
  @Input() priceLabel: string;
  @Input() toDecimalPlaces: number;
  @Input() modifierClass = '';

  formattedPrice;
  componentPriceId: string;
  componentPriceLabelId: string;

  private decimalPricePipe: DecimalPlacesPipe = new DecimalPlacesPipe();
  private volumePricePipe: VolumePricePipe = new VolumePricePipe();

  constructor() {}

  ngOnInit() {
    this.formattedPrice = this.volumePricePipe.transform(
      this.decimalPricePipe.transform(this.price, this.toDecimalPlaces),
      this.currency,
      this.currentChannel,
      this.basePrice,
    );
    this.componentId = 'price_component_' + this.componentId;
    this.componentPriceId = this.componentId + '_price_' + this.currentChannel?.channel;
    this.componentPriceLabelId = this.componentId + '_price_label_' + this.currentChannel?.channel;
  }

  ngOnChanges() {
    this.formattedPrice = this.volumePricePipe.transform(
      this.decimalPricePipe.transform(this.price, this.toDecimalPlaces),
      this.currency,
      this.currentChannel,
      this.basePrice,
    );
  }
}
