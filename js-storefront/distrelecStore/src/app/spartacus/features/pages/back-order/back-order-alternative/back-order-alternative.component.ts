import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProductAvailability } from '@model/product-availability.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { Observable } from 'rxjs';
import { PriceService } from '@services/price.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { Channel } from '@model/site-settings.model';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-back-order-alternative',
  templateUrl: './back-order-alternative.component.html',
  styleUrls: ['./back-order-alternative.component.scss'],
})
export class BackOrderAlternativeComponent implements OnInit {
  @Input() product;
  @Input() itemNumber;

  @Output() sendEventToParent = new EventEmitter<any>();

  stockLevel_: Observable<ProductAvailability>;
  openModal = false;
  productPrice?: number;
  activeChannel: Channel;

  currentChannelData$ = this.siteSettingsService.currentChannelData$.pipe(
    tap((channelData) => (this.activeChannel = channelData.channel)),
  );

  constructor(
    private productAvailability: ProductAvailabilityService,
    private priceService: PriceService,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  ngOnInit(): void {
    this.stockLevel_ = this.productAvailability.getAvailability(this.product.code);
    this.setPrice(this.product.orderQuantityMinimum);
  }

  productCodeReceived(quantity: any): void {
    this.sendEventToParent.emit({ itemNumber: '', productCode: this.product.code, quantity, reference: '' });
  }

  public setPrice(quantity: number): void {
    this.productPrice = this.priceService.getPriceForQuantity(
      this.product.volumePricesMap,
      quantity,
      this.activeChannel,
    );
  }
}
