import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { createFrom, EventService, Price, TranslationService, UserIdService } from '@spartacus/core';
import { Prices, VolumePriceMap } from '@model/price.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { formatDate } from '@angular/common';
import { PriceService } from '@services/price.service';
import { ViewItemEvent } from '@features/tracking/events/view-item-event';
import { ActivatedRoute } from '@angular/router';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { ICustomProduct } from '@model/product.model';
import { UntypedFormControl } from '@angular/forms';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PricesComponent implements OnInit, OnDestroy {
  @Input() prices: Prices;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() productData: ICustomProduct;
  @Input() salesUnit: string;
  @Input() allowBulk?: boolean;
  @Input() minQuantity?: number;
  @Input() quantityStep?: number;
  @Input() control?: UntypedFormControl;

  price$: Observable<Price> = this.productDataService.activePrice$;
  volumePricesMap: VolumePriceMap[];
  promotionEndDate$: Observable<string>;
  activeCountryCode$: Observable<string> = this.countryService.getActive();
  userId$: Observable<string> = this.userIdService.getUserId();

  schema;

  private subscriptions = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private eventService: EventService,
    private i18n: TranslationService,
    private allSiteSettingsService: AllsitesettingsService,
    public priceService: PriceService,
    private countryService: CountryService,
    private userIdService: UserIdService,
    private productDataService: ProductDataService,
  ) {}

  ngOnInit() {
    if (this.prices.promotionEndDate) {
      this.promotionEndDate$ = this.formatPromotionEndDate(this.prices.promotionEndDate);
    }

    this.volumePricesMap = this.prices.volumePricesMap;

    /* eslint-disable @typescript-eslint/naming-convention */
    this.schema = {
      '@context': 'http://schema.org',
      '@type': 'Product',
      name: this.productData?.name,
      image: this.productData?.images[0]?.url,
      description: this.productData?.description,
      sku: this.productData?.code,
      mpn: this.productData?.variantType,
      itemCondition: 'NewCondition',
      brand: {
        '@type': 'Brand',
        name: this.productData?.manufacturer,
      },
      offers: this.getVolumePricesMapSchema(),
    };
    /* eslint-enable @typescript-eslint/naming-convention */

    this.eventService.dispatch(
      createFrom(ViewItemEvent, {
        product: this.updateProductDataWithVolumePrices(this.prices.volumePricesMap),
        itemListUrlParam: this.activatedRoute.snapshot.queryParamMap.get('itemList'),
      }),
    );

    this.setProductPrice();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  setProductPrice(): void {
    this.control.setValue(this.minQuantity);
  }

  updateProductDataWithVolumePrices(volumePricesMap: VolumePriceMap[]): ICustomProduct {
    return {
      ...this.productData,
      volumePricesMap,
    };
  }

  getVolumePricesMapSchema() {
    const volumePricesMap = [];
    this.prices.volumePricesMap.forEach((el) => {
      volumePricesMap.push(el.value[0].value);
    });

    /* eslint-disable @typescript-eslint/naming-convention */
    return volumePricesMap?.map((list) => ({
      '@type': 'Offer',
      availability: this.productData?.stock?.stockLevelStatus,
      url: this.productData?.url,
      priceCurrency: this.productData?.price?.currencyIso,
      priceSpecification: {
        '@type': 'CompoundPriceSpecification',
        name: 'Price',
        price: list.priceWithVat,
        priceCurrency: this.productData?.price?.currencyIso,
        valueAddedTaxIncluded: true,
      },
      eligibleQuantity: {
        '@type': 'QuantitativeValue',
        minValue: list.minQuantity,
      },
    }));
    /* eslint-enable @typescript-eslint/naming-convention */
  }

  formatPromotionEndDate(promotionEndDate) {
    const timezone = promotionEndDate.toString().split('+')[1];

    return this.i18n.translate('text.store.dateformat').pipe(
      // Set the timezone to be the same as the one returned from ERP
      map((dateFormat) =>
        formatDate(promotionEndDate, dateFormat, this.allSiteSettingsService.getLocale(), `GMT+${timezone}`),
      ),
    );
  }

  isBulkDiscountLabelVisible(): Observable<boolean> {
    return combineLatest([this.activeCountryCode$, this.userId$]).pipe(
      map(([countryCode, userId]) => {
        const loggedInOrNotLoggedInCountries = countryCode === 'FR' || countryCode === 'LT' || countryCode === 'CH';

        return this.currentChannel.channel === 'B2B' && (userId === 'current' || loggedInOrNotLoggedInCountries);
      }),
    );
  }
}
