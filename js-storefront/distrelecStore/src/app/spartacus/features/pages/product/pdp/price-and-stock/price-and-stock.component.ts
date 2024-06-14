import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, first, map, take, tap } from 'rxjs/operators';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { ProductAvailability } from '@model/product-availability.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { Prices, VolumePriceMap } from '@model/price.model';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { EventHelper } from '@features/tracking/event-helper.service';
import { FactFinderProductParameters } from '@model/factfinder.model';
import { PageHelper } from '@helpers/page-helper';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PriceService } from '@services/price.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { Channel, CurrentSiteSettings } from '@model/site-settings.model';
import { ICustomProduct } from '@model/product.model';
import { UntypedFormControl } from '@angular/forms';

@Component({
  selector: 'app-price-and-stock',
  templateUrl: './price-and-stock.component.html',
  styleUrls: ['./price-and-stock.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PriceAndStockComponent implements OnInit {
  @Input() product: ICustomProduct;
  @Input() displayNotifyModal$: BehaviorSubject<boolean>;
  @Input() ghsIcons: string[];

  isWaldom = false;
  availableQty = 0;
  itemListEntity: ItemListEntity;
  quantityControl: UntypedFormControl = this.productDataService.getFormQuantity();

  model$: Observable<{ productAvailability: ProductAvailability; channel: CurrentSiteSettings; prices: Prices }>;

  private channel: Channel;
  private volumePricesMap: VolumePriceMap[];

  constructor(
    private productAvailabilityService: ProductAvailabilityService,
    private productDataService: ProductDataService,
    private activatedRoute: ActivatedRoute,
    private eventHelper: EventHelper,
    private pageHelper: PageHelper,
    private priceService: PriceService,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  ngOnInit(): void {
    this.model$ = combineLatest([this.getProductAvailability(), this.getChannelData(), this.getPrices()]).pipe(
      filter(([productAvailability, channel, prices]) => !!productAvailability && !!channel && !!prices),
      map(([productAvailability, channel, prices]) => ({
        productAvailability,
        channel,
        prices,
      })),
    );

    this.activatedRoute.queryParamMap.pipe(take(1)).subscribe((params) => {
      this.itemListEntity = params.get('itemList') as ItemListEntity;
    });
  }

  getProductAvailability(): Observable<ProductAvailability> {
    return this.productAvailabilityService.getAvailability(this.product.code).pipe(
      tap((data) => {
        if (data?.stockLevels[0].waldom) {
          this.isWaldom = data?.stockLevels[0].waldom;
          this.availableQty = data?.stockLevels[0].available;
        }
      }),
    );
  }

  getPrices(): Observable<Prices> {
    return this.priceService.getPrices(this.product.code).pipe(
      filter((prices) => !!prices),
      tap((prices) => {
        this.volumePricesMap = prices.volumePricesMap;
        this.productDataService.prices$.next(prices);
      }),
    );
  }

  getChannelData(): Observable<CurrentSiteSettings> {
    return this.siteSettingsService.getCurrentChannelData().pipe(
      tap((channel) => {
        this.channel = channel.channel;
      }),
    );
  }

  sendFactFinderCartEvent(quantity: number): void {
    this.activatedRoute.queryParamMap.pipe(first()).subscribe((params: ParamMap) => {
      if (params.get('track') === 'true') {
        const unpackedParams = {
          track: params.get('track'),
          pos: params.get('pos'),
          origPos: params.get('origPos'),
          page: this.pageHelper.getPreviousPageTemplate(),
          pageSize: params.get('pageSize'),
          origPageSize: params.get('origPageSize'),
          product: this.product.code,
          prodprice: this.priceService.getPriceForQuantity(this.volumePricesMap, quantity, this.channel),
          campaign: params.get('campaign'),
          trackQuery: params.get('trackQuery'),
          filterapplied: params.has('filterapplied') ? params.get('filterapplied').replace(/ /g, '+') : null,
        } as unknown as FactFinderProductParameters;

        this.eventHelper.trackFactFinderCartEvent({ ...unpackedParams, quantity });
      }
    });
  }
}
