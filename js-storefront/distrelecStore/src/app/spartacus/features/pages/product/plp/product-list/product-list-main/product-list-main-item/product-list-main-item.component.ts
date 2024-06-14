import { Component, Input, OnInit } from '@angular/core';
import { createFrom, EventService, Product } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Channel, CurrentSiteSettings } from '@model/site-settings.model';
import { faAngleDown, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { FloatingToolbarEvents } from '../floating-toolbar/floating-toolbar.component';
import { ProductListViewService } from '@features/pages/product/core/services/product-list-view.service';
import { EventHelper } from '@features/tracking/event-helper.service';
import { FactFinderProductParameters } from '@model/factfinder.model';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { ActivatedRoute, convertToParamMap, Params } from '@angular/router';
import { PageHelper } from '@helpers/page-helper';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { VolumePricesMapList } from '@model/product.model';
import { LabelConfigService } from '@services/label-config.service';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormGroup, UntypedFormControl } from '@angular/forms';

@Component({
  selector: 'app-product-list-main-item',
  templateUrl: './product-list-main-item.component.html',
  styleUrls: ['./product-list-main-item.component.scss'],
})
export class ProductListMainItemComponent implements OnInit {
  @Input() product: Product;
  @Input() index: number;
  @Input() currentChannel_: BehaviorSubject<CurrentSiteSettings>;
  @Input() showProductFamilyLink: boolean;
  @Input() channel$: Observable<Channel>;
  @Input() toolbarEvent: Subject<FloatingToolbarEvents>;

  availabilityData$: Observable<ProductAvailability>;
  detailView = false;
  schema;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  queryParams$: Observable<Params> = this.sessionService.getSessionId().pipe(
    map((sessionId) => ({
      ...this.getTrackingParameters(),
      sid: sessionId,
      itemList: this.getItemListEntity(),
    })),
  );

  userId: string;
  readonly faCheckCircle = faCheckCircle;
  readonly faAngleDown = faAngleDown;

  constructor(
    private productListViewService: ProductListViewService,
    private productAvailabilityService: ProductAvailabilityService,
    private eventHelper: EventHelper,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private pageHelper: PageHelper,
    private route: ActivatedRoute,
    private sessionService: SessionService,
    private eventService: EventService,
    private labelConfigService: LabelConfigService,
  ) {}

  ngOnInit() {
    this.addToCartForm.get('quantity').setValue(this.product.orderQuantityMinimum);
    this.schema = this.getSchema(this.product);
    this.availabilityData$ = this.productAvailabilityService.getAvailability(this.product?.code);
  }

  sendFactFinderCartEvent(quantity: number) {
    const trackingParams = this.getTrackingParameters();
    if (trackingParams.track === 'true') {
      const extraParams = {
        quantity,
        product: this.product.code,
        prodprice: this.product.price.value,
        pageType: this.pageHelper.getCurrentPageTemplate(),
      };
      this.eventHelper.trackFactFinderCartEvent({
        ...trackingParams,
        ...extraParams,
      } as unknown as FactFinderProductParameters);
    }
  }

  toggleView() {
    this.detailView = !this.detailView;
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    return this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(eelImageMap);
  }

  trackProductClick(product: Product) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: this.getItemListEntity(),
        index: this.index,
      } as ProductClickEvent),
    );
  }

  getItemListEntity(): ItemListEntity {
    return this.pageHelper.getListPageEntityType();
  }

  getSchema(product) {
    try {
      const { storefrontDomain, mediaDomain } = this.currentChannel_.getValue();

      return {
        '@context': 'http://schema.org',
        '@type': 'Product',
        name: product.name,
        url: storefrontDomain + product.url,
        image: product.images ? mediaDomain + product.images[0]?.url : '',
        description: product.description,
        sku: product.code,
        mpn: product.typeName,
        itemCondition: 'NewCondition',
        brand: {
          '@type': 'Brand',
          name: product.manufacturer,
        },
        offers: this.getVolumePricesMap(product),
      };
    } catch {
      return undefined;
    }
  }

  getPromoLabelColor(code: string) {
    return this.labelConfigService.getColorByLabel(code);
  }

  private getVolumePricesMap(product) {
    const stockLevel = 0; // Stock is not fetched in SSR

    const volumePricesMap: VolumePricesMapList[] = product.volumePricesMap;
    return volumePricesMap?.map((list) => ({
      '@type': 'Offer',
      availability: stockLevel ? 'In stock' : 'Out of stock',
      url: product.url,
      priceCurrency: product.price.currencyIso,
      price: product.price.formattedValue,
      eligibleQuantity: {
        '@type': 'QuantitativeValue',
        minValue: list.key,
      },
    }));
  }

  private getTrackingParameters(): FactFinderProductParameters {
    const product = this.product;
    const routeQueryParams = this.route.snapshot.queryParams;
    let trackQuery: string;

    if (routeQueryParams.redirectQuery) {
      trackQuery = routeQueryParams.redirectQuery;
    } else if (this.pageHelper.isCategoryPage()) {
      trackQuery = product.categoryCodePath;
    } else {
      trackQuery = routeQueryParams.q;
      if (routeQueryParams.trackQuery) {
        trackQuery = routeQueryParams.trackQuery;
      }
    }

    let filterApplied = '';
    const paramMap = convertToParamMap(routeQueryParams);
    paramMap.keys.forEach((p: string) => {
      if (p.startsWith('filter_') && !p.startsWith('filter_Category')) {
        if (filterApplied.length > 0) {
          filterApplied = filterApplied + '&';
        }

        filterApplied = filterApplied + p + '=' + paramMap.get(p);
      }
    });

    const trackingParameters: FactFinderProductParameters = {
      trackQuery,
      pos: +product.origPosition,
      origPos: this.index + 1,
      page: routeQueryParams.page,
      pageSize: routeQueryParams.pageSize,
      origPageSize: 50,
      track: (this.pageHelper.isCategoryPage() || this.pageHelper.isSearchPage()).toString(),
    };

    if (routeQueryParams.sort) {
      trackingParameters.sort = routeQueryParams.sort;
    }

    if (filterApplied.length > 0) {
      trackingParameters.filterapplied = filterApplied;
    }

    return trackingParameters;
  }

}
