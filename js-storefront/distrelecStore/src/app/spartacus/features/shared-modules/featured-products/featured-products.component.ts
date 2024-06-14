import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { FeaturedProductService } from '@services/featured-products.service';
import { OwlOptions } from 'ngx-owl-carousel-o';
import { from, Observable } from 'rxjs';
import { UseWebpImage } from '@helpers/useWebpImage';
import { concatMap, switchMap, take, toArray } from 'rxjs/operators';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { createFrom, EventService, Product, WindowRef } from '@spartacus/core';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-featured-products',
  templateUrl: './featured-products.component.html',
  styleUrls: ['./featured-products.component.scss'],
})
export class SharedFeaturedProductsComponent implements OnInit, AfterViewInit {
  @Input() component;
  @Input() maxNumberToDisplay;
  @Input() showPromotionalText;
  @Input() productCodes;
  faAngleRight = faAngleRight;

  $featuredProducts: Observable<Product[]>;
  customOptions: OwlOptions;
  useWebpImg = UseWebpImage;
  itemListEntity = ItemListEntity;

  isBrowser = this.winRef.isBrowser();

  constructor(
    private eventService: EventService,
    private fProductsService: FeaturedProductService,
    private productDataService: ProductDataService,
    private winRef: WindowRef,
  ) {}

  ngOnInit(): void {
    if (this.productCodes?.length) {
      this.$featuredProducts = this.fProductsService.getProducts(this.productCodes.split(' '));
    } else {
      this.$featuredProducts = this.component.data$.pipe(
        take(1),
        switchMap((data: any) => from(data?.products?.split(' ') ?? [])),
        concatMap((productCode: string) => this.productDataService.getProductData(productCode).pipe(take(1))),
        toArray(),
      );
    }

    this.customOptions = {
      loop: false,
      mouseDrag: false,
      touchDrag: false,
      pullDrag: false,
      dots: false,
      navSpeed: 700,
      margin: 15,
      navText: ['<i class="arrow left"></i>', '<i class="arrow right"></i>'],
      items: this.maxNumberToDisplay,
      nav: true,
    };
  }

  ngAfterViewInit() {
    // run this code only if third party object has been initialised
    if (this.customOptions?.responsive) {
      this.customOptions.responsive = {
        0: {
          items: 1,
        },
        768: {
          items: 2,
        },
        992: {
          items: this.maxNumberToDisplay,
        },
      };
    }
  }

  trackProductClick(product, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: ItemListEntity.FEATURED,
        index,
      } as ProductClickEvent),
    );
  }
}
