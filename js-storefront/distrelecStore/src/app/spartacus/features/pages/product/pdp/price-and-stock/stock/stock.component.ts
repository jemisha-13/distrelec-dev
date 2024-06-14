import { Component, ComponentRef, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { faEnvelope } from '@fortawesome/free-regular-svg-icons';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { AppendComponentService } from '@services/append-component.service';
import { SalesStatusConfiguration, SalesStatusService } from '@services/sales-status.service';
import { CountryCodesEnum, CountryService } from '@context-services/country.service';
import { angleRight, deliveryIcon, emailIcon, iconClock, tickCancelCircle, xInStock } from '@assets/icons/icon-index';
import { ProductAvailability, StockLevel } from '@model/product-availability.model';
import { processStockData } from './stock-helper';
import { NotifyMeComponent } from '../../notify-me/notify-me.component';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class StockComponent implements OnInit {
  @Input() productAvailability: ProductAvailability;
  @Input() salesStatus: string;
  @Input() displayNotifyModal$: BehaviorSubject<boolean>;
  @Input() productCode: string;

  @Input() size?: string = '';

  faEnvelope = faEnvelope;
  hasAvailabilityData = false;
  isWaldomDeliveryTime = false;
  isWaldomNextDayDeliveryTime = false;
  furtherStockWaldom = false;
  furtherStockAdditional = false;
  isStockLevelWaldom = false;
  furtherStock = false;
  replenishDeliveryText = '';
  replenishDeliveryTextPickup = '';
  cdcReplenishmentDeliveryTime = '';
  isoCode = '';
  stockLevelAvailable = 0;
  stockLevelTotal = 0;
  furtherStockAvailable = 0;
  isMoreStockAvailable = false;
  moreStockAvailableText = '';
  pickUp = false;
  stockLevelPickup = 0;
  isBto = false;
  isDir = false;
  isProductFromWaldom = false;
  oosFurther = false;
  stockedCdcOnly = false;
  notifyMePopupRef: ComponentRef<NotifyMeComponent>;

  xInStock = xInStock;
  deliveryIcon = deliveryIcon;
  angleRight = angleRight;
  tickCancelCircle = tickCancelCircle;
  iconClock = iconClock;
  emailIcon = emailIcon;

  salesData: SalesStatusConfiguration;

  constructor(
    private salesStatusService: SalesStatusService,
    private appendComponentService: AppendComponentService,
    private countryService: CountryService,
  ) {}

  isStockAvailableTitle(stockLevelAvailable): boolean {
    return (
      (stockLevelAvailable > '0' && !this.isBto && !this.isDir) ||
      this.salesStatus === '20' ||
      this.salesStatus === '21'
    );
  }

  isNotifyBackInStockPopupWithStock(): boolean {
    return (
      this.salesData?.isNotifyMeBackInStock &&
      !this.isBto &&
      !this.isDir &&
      !this.isStockLevelWaldom &&
      this.salesStatus === '20'
    );
  }

  isNotifyBackInStockPopupNoStock(): boolean {
    return this.salesData?.isNotifyMeBackInStock && !this.isStockLevelWaldom && !this.isBto && !this.isDir;
  }

  isShippingAvailableInFuture(): boolean {
    return (
      (this.isBto || this.isDir) && this.salesStatus !== '60' && this.salesStatus !== '61' && this.salesStatus !== '90'
    );
  }

  isNoStockForBTRorDir(): boolean {
    return (
      (this.isBto || this.isDir) &&
      (this.salesStatus === '60' || this.salesStatus === '61' || this.salesStatus === '90')
    );
  }

  isStockAvailableTOorderForCDCOnly(): boolean {
    return (
      !this.stockLevelAvailable &&
      this.furtherStockAvailable &&
      this.stockedCdcOnly &&
      this.salesStatus !== '30' &&
      this.salesStatus !== '31' &&
      this.salesStatus !== '52' &&
      this.salesStatus !== '60' &&
      this.salesStatus !== '61'
    );
  }

  isStockAvailableTOorderForCDCOnlyStatus6061(): boolean {
    return (
      !this.stockLevelAvailable &&
      this.furtherStockAvailable &&
      this.stockedCdcOnly &&
      this.salesStatus !== '30' &&
      this.salesStatus !== '31' &&
      this.salesStatus !== '52' &&
      (this.salesStatus === '60' || this.salesStatus === '61')
    );
  }

  isStatus50(salesStatus: string): boolean {
    return salesStatus === '50' || salesStatus === '51' || salesStatus === '52' || salesStatus === '53';
  }

  isStatus90(salesStatus: string): boolean {
    return salesStatus === '90' || salesStatus === '91';
  }

  isStockAvailableToBackorder(): boolean {
    return (
      !this.stockLevelAvailable &&
      this.furtherStockAvailable &&
      this.isBto &&
      (this.salesStatus < '40' || this.salesStatus === '41' || this.salesStatus === '52')
    );
  }

  isStockAvailableForCDC(): boolean {
    return (
      !this.stockLevelAvailable &&
      this.furtherStockAvailable &&
      this.stockedCdcOnly &&
      !this.isBto &&
      (this.salesStatus < '40' || this.salesStatus === '41' || this.salesStatus === '52')
    );
  }

  isOutOfStockMessage(): boolean {
    return (
      !this.stockLevelAvailable &&
      this.furtherStockAvailable &&
      (this.salesStatus > '41' || this.salesStatus === '40') &&
      this.salesStatus !== '50' &&
      this.salesStatus !== '52' &&
      this.isoCode !== CountryCodesEnum.SWITZERLAND &&
      this.isoCode !== CountryCodesEnum.LIECHTENSTEIN
    );
  }

  isWaldomNextDayDeliveryTimeForWebshop(): boolean {
    return (
      this.isWaldomNextDayDeliveryTime &&
      this.salesStatus !== '60' &&
      this.salesStatus !== '61' &&
      this.salesStatus !== '90'
    );
  }

  isWaldomStockNotCDCAndBelow60(): boolean {
    return this.furtherStockWaldom && !this.stockedCdcOnly && this.salesStatus < '60';
  }

  isAdditionalStockWarehouse7371(): boolean {
    return (
      this.furtherStockWaldom &&
      this.furtherStockAdditional &&
      this.salesStatus < '60' &&
      !(this.stockedCdcOnly && this.isStatus50(this.salesStatus))
    );
  }

  isAdditionalStockWarehouse7374(): boolean {
    return (
      !this.furtherStockWaldom &&
      this.furtherStockAdditional &&
      !this.isBto &&
      !this.isDir &&
      this.salesStatus !== '20' &&
      this.salesStatus !== '41' &&
      this.salesStatus !== '60' &&
      this.salesStatus !== '61' &&
      this.salesStatus !== '90'
    );
  }

  isSameDayDeliveryContent(): boolean {
    return (
      this.pickUp &&
      this.salesStatus !== '20' &&
      this.salesStatus !== '21' &&
      this.salesStatus !== '60' &&
      this.salesStatus !== '61' &&
      this.salesStatus !== '90'
    );
  }

  isSameDayDeliveryMessageBeNl(): boolean {
    return (
      (this.isoCode === CountryCodesEnum.NETHERLANDS || this.isoCode === CountryCodesEnum.BELGIUM) &&
      (this.salesStatus === '30' || this.salesStatus === '31') &&
      !this.isBto &&
      !this.isDir &&
      !this.isProductFromWaldom
    );
  }

  isMoreStockAvailableAndStatusBelow40(): boolean {
    return this.isMoreStockAvailable && this.salesStatus <= '40';
  }

  isWaldomBelow40NotFrDelivery(): boolean {
    return this.isWaldomDeliveryTime && this.salesStatus <= '40';
  }

  isBTODelivery(): boolean {
    return (
      (this.isBto || this.isDir) && this.salesStatus !== '60' && this.salesStatus !== '61' && this.salesStatus !== '90'
    );
  }

  isNoStockForBTOText(): boolean {
    return (this.isBto || this.isDir) && (this.salesStatus === '30' || this.salesStatus === '31');
  }

  isFurtherStock(): boolean {
    return this.furtherStockAdditional && this.furtherStockWaldom;
  }

  isStockedProduct(): boolean {
    return !this.isBto && !this.isDir && !this.isStockLevelWaldom;
  }

  isFurtherStockWithDelivery(): boolean {
    return (
      this.salesStatus !== '20' &&
      this.salesStatus !== '21' &&
      this.salesStatus !== '40' &&
      this.salesStatus !== '41' &&
      this.salesStatus <= '52'
    );
  }

  isFurtherStockWithReplenishDelivery(): boolean {
    return (
      !this.furtherStockWaldom &&
      this.furtherStockAdditional &&
      !this.oosFurther &&
      this.salesStatus !== '50' &&
      this.salesStatus !== '51' &&
      this.salesStatus !== '52'
    );
  }

  isFurtherStockWithPickupDelivery(): boolean {
    return this.oosFurther && this.furtherStockAdditional && !this.isBto && this.salesStatus !== '52';
  }

  returnIconForInStock(): string {
    if (this.stockedCdcOnly && this.isStatus50(this.salesStatus)) {
      return 'times';
    } else {
      return this.salesData.icon;
    }
  }

  isNordicCountries(): boolean {
    return (
      this.isoCode === CountryCodesEnum.NORWAY ||
      this.isoCode === CountryCodesEnum.FINLAND ||
      this.isoCode === CountryCodesEnum.SWEDEN
    );
  }

  isSwitzerlandOrLiechtenstein(): boolean {
    return this.isoCode === CountryCodesEnum.SWITZERLAND || this.isoCode === CountryCodesEnum.LIECHTENSTEIN;
  }

  returnIconClass(): string {
    return this.salesStatus.includes('3') || this.salesStatus.includes('5') || this.salesStatus.includes('9')
      ? 'tick-cancel-circle-grey'
      : '';
  }

  appendNotifyMePopup(): void{
    this.notifyMePopupRef = this.appendComponentService.appendComponent(NotifyMeComponent, {
      productCode: this.productCode,
    });

    this.appendComponentService.appendBackdropModal();
    this.notifyMePopupRef.instance.isClickInsideComponent = true;
    this.notifyMePopupRef.instance.productCode = this.productCode;

    this.notifyMePopupRef.instance.close.pipe(first()).subscribe(() => {
      this.appendComponentService.destroyComponent(this.notifyMePopupRef);
      this.appendComponentService.removeBackdropComponentFromBody();
    });
  }

  appendPopup(): void {
    this.appendComponentService.appendContentPopup(this.lcPopupTitle(), this.lcPopUpContent());
  }

  lcPopUpContent(): string {
    if (this.isoCode === CountryCodesEnum.SWITZERLAND || this.isoCode === CountryCodesEnum.LIECHTENSTEIN) {
      return 'form.lc_popup_content.ch';
    }

    return 'form.lc_popup_content.nl';
  }

  lcPopupTitle(): string {
    if (this.isoCode === CountryCodesEnum.SWITZERLAND || this.isoCode === CountryCodesEnum.LIECHTENSTEIN) {
      return 'form.lc_popup_title.ch';
    }

    return 'form.lc_popup_title.nl';
  }

  ngOnInit() {
    this.countryService
      .getActive()
      .pipe(first())
      .subscribe((isoCode) => (this.isoCode = isoCode));

    if (
      this.productAvailability.stockLevels.filter((item: StockLevel) => item.waldom || item.mview === 'BTR').length > 0
    ) {
      this.isProductFromWaldom = true;
    }
    this.hasAvailabilityData = true;
    this.cdcReplenishmentDeliveryTime = this.productAvailability.stockLevels[0].replenishmentDeliveryTime.replace(
      ' ',
      '',
    );

    const stockData = processStockData({
      isoCode: `${this.isoCode}`,
      res: this.productAvailability,
      salesStatus: `${this.salesStatus}`,
    });

    this.isStockLevelWaldom = stockData.isStockLevelWaldom;
    this.isWaldomDeliveryTime = stockData.isWaldomDeliveryTime;
    this.replenishDeliveryText = stockData.replenishDeliveryText;
    this.stockLevelAvailable = stockData.stockLevelAvailable;
    this.isWaldomNextDayDeliveryTime = stockData.isWaldomNextDayDeliveryTime;
    this.isBto = stockData.isBto;
    this.isDir = stockData.isDir;
    this.furtherStockWaldom = stockData.furtherStockWaldom;
    this.replenishDeliveryTextPickup = stockData.replenishDeliveryTextPickup;
    this.furtherStockAvailable = stockData.furtherStockAvailable;
    this.furtherStockAdditional = stockData.furtherStockAdditional;
    this.oosFurther = stockData.oosFurther;
    this.pickUp = stockData.pickUp;
    this.stockLevelPickup = stockData.stockLevelPickup;
    this.furtherStock = stockData.furtherStock;
    this.moreStockAvailableText = stockData.moreStockAvailableText;
    this.isMoreStockAvailable = stockData.isMoreStockAvailable;
    this.stockedCdcOnly = stockData.stockedCdcOnly;
    this.stockLevelTotal = stockData?.stockLevelTotal;

    this.salesData = this.salesStatusService.getSalesStatusConfiguration(this.salesStatus);
  }

  isProductUnbuyable(): boolean {
    if (this.stockLevelTotal > 0 && (this.salesStatus === '40' || this.salesStatus === '41')) {
      return false;
    }
    if (this.stockLevelAvailable === 0) {
      return (
        (this.salesStatus !== '21' && this.salesStatus !== '30' && this.salesStatus !== '31') || this.isStockLevelWaldom
      );
    }
    return false;
  }
}
