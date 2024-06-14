import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { PriceService } from '@services/price.service';
import { Prices } from '@model/price.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-shopping-list-pricing',
  templateUrl: './pricing.component.html',
  styleUrls: ['./pricing.component.scss'],
})
export class ShoppingListPricingComponent implements OnInit, OnChanges {
  @Input() productCode: string;
  @Input() productQty: number;
  @Input() prices: Prices;
  @Input() salesUnit: string;
  @Input() indexingPos: number;

  basePrice_: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  priceWithVat_: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  currentChannel$: BehaviorSubject<CurrentSiteSettings>;

  currencyIso: string;
  productPriceVAT: number;

  constructor(
    private priceService: PriceService,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.productQty) {
      this.basePrice_.next(
        this.priceService.getPriceForQuantity(this.prices.volumePricesMap, changes.productQty.currentValue, 'B2B'),
      );
      this.priceWithVat_.next(
        this.priceService.getPriceForQuantity(this.prices.volumePricesMap, changes.productQty.currentValue, 'B2C'),
      );
    }
  }

  ngOnInit(): void {
    this.currencyIso = this.priceService.getCurrencyFromPrice(this.prices.volumePricesMap);
    this.basePrice_.next(this.priceService.getPriceForQuantity(this.prices.volumePricesMap, this.productQty, 'B2B'));
    this.priceWithVat_.next(this.priceService.getPriceForQuantity(this.prices.volumePricesMap, this.productQty, 'B2C'));
    this.productPriceVAT = this.prices.price.priceWithVat;
    this.currentChannel$ = this.siteSettingsService.currentChannelData$;
  }
}
