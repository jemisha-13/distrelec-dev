import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { CmsComponent, CmsService, createFrom, EventService, Page } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { concatMap, filter, map, mergeMap, switchMap, take, tap, toArray } from 'rxjs/operators';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { UseWebpImage } from '@helpers/useWebpImage';
import { from, Observable, of } from 'rxjs';

import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { IntersectionObserverService } from '@services/intersection-observer';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ICustomProduct } from '@model/product.model';

interface DistFeaturedProductsComponent extends CmsComponent {
  linkText?: string;
  products?: string;
}

@Component({
  selector: 'app-featured-products',
  templateUrl: './featured-products.component.html',
  styleUrls: ['./featured-products.component.scss'],
})
export class FeaturedProductsComponent implements OnInit {
  @Input() featuredProductsData: DistFeaturedProductsComponent;

  readonly FEATURE_TITLE_MAX_LENGTH: number = 48;
  readonly FEATURED_PRODUCTS_COMPONENT_TYPECODE: string = 'DistFeaturedProductsComponent';

  componentData$: Observable<DistFeaturedProductsComponent>;
  currentPageObservable$: Observable<Page> = this.pageService.getCurrentPage();

  faAngleRight = faAngleRight;
  featuredProducts$: Observable<ICustomProduct[]>;
  useWebpImg = UseWebpImage;
  linkText: string;
  currentChannelData$ = this.siteSettingsService.currentChannelData$;
  productAdditionalClasses$: Observable<any>;
  itemListEntity = ItemListEntity;

  constructor(
    private cms: CmsComponentData<DistFeaturedProductsComponent>,
    private eventService: EventService,
    private productDataService: ProductDataService,
    private priceService: PriceService,
    private productAvailabilityService: ProductAvailabilityService,
    private siteSettingsService: AllsitesettingsService,
    private pageService: CmsService,
    private intersectionObserverService: IntersectionObserverService,
    private elementRef: ElementRef,
  ) {}

  ngOnInit(): void {
    this.componentData$ = this.featuredProductsData ? of(this.featuredProductsData) : this.cms.data$;
    this.productAdditionalClasses$ = this.currentPageObservable$.pipe(
      map((page) => page?.pageId === 'outletStorePage'),
    );
    this.renderFeaturedProducts();
  }

  renderFeaturedProducts(): void {
    this.featuredProducts$ = this.componentData$.pipe(
      filter((comp) => comp.typeCode === this.FEATURED_PRODUCTS_COMPONENT_TYPECODE),
      take(1),
      tap((componentData) => (this.linkText = componentData.linkText)),
      switchMap((componentData) =>
        this.productAvailabilityService.fetchProductsAccessibility(componentData.products).pipe(
          switchMap((availableProductCodes) => from(availableProductCodes)),
          concatMap((productCode) =>
            this.productDataService.getProductData(productCode).pipe(
              map((product) => ({ ...product, featureProductTitle: this.featureProductTitle(product) })),
              take(1),
            ),
          ),
          filter((product) => product.buyable),
        ),
      ),
      mergeMap((product) => {
        const { code } = product;
        return this.loadProductPrices(code).pipe(map((prices) => ({ ...product, ...prices }) as ICustomProduct));
      }),
      toArray(),
      map((products: ICustomProduct[]) => {
        if (products) {
          this.intersectionObserverService.observeViewport(
            this.elementRef.nativeElement,
            () => this.eventService.dispatch(createFrom(ViewItemListEvent, { products, listType: ItemListEntity.FEATURED }))
          );
        }

        return products;
      }),
    );
  }

  trackProductClick(product, index: number): void {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: ItemListEntity.FEATURED,
        index,
      } as ProductClickEvent),
    );
  }

  featureProductTitle(product): string {
    let title = '';
    if (product.typeName) {
      title = product.typeName + ' - ';
    }
    title += product.name;
    if (product.distManufacturer) {
      title += ' - ' + product.distManufacturer.name;
    }
    title = title.trim();
    if (title.length > this.FEATURE_TITLE_MAX_LENGTH) {
      return title.substring(0, this.FEATURE_TITLE_MAX_LENGTH) + '...';
    } else {
      return title;
    }
  }

  private loadProductPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }
}
