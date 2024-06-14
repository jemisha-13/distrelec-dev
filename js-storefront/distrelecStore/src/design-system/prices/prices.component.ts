import { Component, Input, OnInit } from '@angular/core';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-dist-prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.scss'],
})
export class PricesComponent implements OnInit {
  @Input() formattedBasePrice: number;
  @Input() formattedPriceWithVat: number;
  @Input() currency: string;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() componentId: string;
  @Input() toDecimalPlaces: number;
  @Input() excVatText: string;
  @Input() incVatText: string;
  @Input() modifierClass: '';

  isB2B = false;

  constructor() {}

  ngOnInit(): void {
    this.isB2B = this.currentChannel.channel === 'B2B';
  }
}
