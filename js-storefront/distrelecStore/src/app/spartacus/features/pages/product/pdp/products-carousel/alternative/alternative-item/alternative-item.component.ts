import { Component, Input, OnInit } from '@angular/core';
import { ProductReference } from '@model/product-reference.model';
import { Observable } from 'rxjs';
import { ProductReferencesService } from '@features/pages/product/core/services/product-references.service';
import { ProductAvailability } from '@model/product-availability.model';
import { shareReplay, take } from 'rxjs/operators';
import { DefaultImageService } from '@services/default-image.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { createFrom, EventService } from '@spartacus/core';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { CurrentSiteSettings } from '@model/site-settings.model';

@Component({
  selector: 'app-alternative-item',
  templateUrl: './alternative-item.component.html',
  styleUrls: ['./alternative-item.component.scss'],
})
export class AlternativeItemComponent implements OnInit {
  @Input() product: ProductReference;
  @Input() index: number;
  @Input() itemListEntity: ItemListEntity;
  @Input() currentChannel: CurrentSiteSettings;

  alternativeStock$: Observable<ProductAvailability>;
  price$: Observable<Prices>;

  constructor(
    private productReferencesService: ProductReferencesService,
    private defaultImageService: DefaultImageService,
    private priceService: PriceService,
    private eventService: EventService,
  ) {}

  ngOnInit(): void {
    this.alternativeStock$ = this.productReferencesService
      .getCarouselItemStock(this.product.target)
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    this.price$ = this.priceService.getPrices(this.product.target.code);
  }

  findPortraitSmall(): string {
    const portraitSmallImage = this.product?.target?.images?.filter((image) => image.format === 'portrait_small');
    if (portraitSmallImage?.length) {
      return portraitSmallImage[0].url;
    }

    return this.defaultImageService.getDefaultImage();
  }

  trackProductClick(productRef: ProductReference, index: number) {
    const product = productRef.target;

    this.loadPrices(product.code)
      .pipe(take(1))
      .subscribe((prices) => {
        this.eventService.dispatch(
          createFrom(ProductClickEvent, {
            product: { ...product, ...prices },
            listType: this.itemListEntity,
            index,
          } as ProductClickEvent),
        );
      });
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }
}
