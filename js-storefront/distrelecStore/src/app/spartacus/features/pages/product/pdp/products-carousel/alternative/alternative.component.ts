import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChildren,
} from '@angular/core';
import { ProductReferencesService } from '@features/pages/product/core/services/product-references.service';
import { ProductReference, ProductReferences } from '@model/product-reference.model';
import { from, Observable, Subscription } from 'rxjs';
import { createFrom, EventService, WindowRef } from '@spartacus/core';
import { ProductAvailability } from '@model/product-availability.model';
import { filter, map, mergeMap, shareReplay, take, toArray } from 'rxjs/operators';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Prices } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';

@Component({
  selector: 'app-alternative',
  templateUrl: './alternative.component.html',
  styleUrls: ['./alternative.component.scss'],
})
export class AlternativeComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() productCode: string;
  @Input() productReferencesNEW: ProductReferences;

  @ViewChildren('listRef', {
    read: ElementRef,
  })
  elRef;
  elRefAfterView: HTMLElement;
  alternativeStock$: Observable<ProductAvailability[]>;
  itemListEntity = ItemListEntity;

  currentChannelData$ = this.siteSettingsService.currentChannelData$;

  productReferences: ProductReference[] = [];
  private subscription: Subscription = new Subscription();

  constructor(
    private eventService: EventService,
    private productReferencesService: ProductReferencesService,
    private changeDetector: ChangeDetectorRef,
    private priceService: PriceService,
    private winRef: WindowRef,
    private siteSettingsService: AllsitesettingsService,
  ) {}

  viewMore(event): void {
    if (this.winRef.isBrowser()) {
      event.target.classList.add('d-none');
      this.elRefAfterView.classList.add('show-more');
    }
  }

  ngOnInit(): void {
    this.subscription.add(
      this.productReferencesService
        .getAlternatives(this.productCode)
        .pipe(filter((res) => res.length > 0))
        .subscribe((res) => {
          this.productReferences = res.filter((product) => product.target.buyable);
          const altProducts = this.productReferences.map((data) => data.target);

          from(altProducts)
            .pipe(
              take(1),
              mergeMap((product) =>
                this.loadPrices(product.code).pipe(
                  take(1),
                  map((prices) => ({ ...product, ...prices })),
                ),
              ),
              toArray(),
            )
            .subscribe((alternativeProducts) => {
              this.eventService.dispatch(
                createFrom(ViewItemListEvent, { products: alternativeProducts, listType: ItemListEntity.ALTERNATIVE }),
              );
            });

          this.changeDetector.detectChanges();
        }),
    );

    this.alternativeStock$ = this.productReferencesService
      .getCarouselStock(this.productReferencesNEW?.alternatives?.carouselProducts)
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  ngAfterViewInit(): void {
    this.subscription = this.elRef.changes.subscribe((val) => {
      this.elRefAfterView = val.first.nativeElement;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }
}
