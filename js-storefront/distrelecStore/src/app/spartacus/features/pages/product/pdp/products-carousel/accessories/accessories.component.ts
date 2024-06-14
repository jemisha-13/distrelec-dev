import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { ProductAccessoryType, ProductReferences } from '@model/product-reference.model';
import { SessionStorageService } from '@services/session-storage.service';
import { createFrom, EventService, Product, WindowRef } from '@spartacus/core';
import { EMPTY, from, Observable, of } from 'rxjs';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { filter, map, mergeMap, take, tap, toArray } from 'rxjs/operators';
import { Prices } from '@model/price.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability } from '@model/product-availability.model';
import { PriceService } from '@services/price.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { IntersectionObserverService } from '@services/intersection-observer';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ProductQuantityService } from '@services/product-quantity.service';
import { SalesStatusService } from '@services/sales-status.service';

@Component({
  selector: 'app-accessories',
  templateUrl: './accessories.component.html',
  styleUrls: ['./accessories.component.scss'],
})
export class AccessoriesComponent implements OnInit {
  @Input() productCode: string;
  @Input() accessoryType: ProductAccessoryType;
  @Input() title: string;

  @Input() productReferences: ProductReferences;

  @ViewChild('accessoriesList') accessoriesList: ElementRef;

  isLoading$: Observable<boolean>;
  availableAccessories$: Observable<Product[]>;

  collapsed = false;
  itemListEntity: ItemListEntity;

  showTotal = 4;
  isLessThanDesktop$ = this.breakpointService.isMobileOrTabletBreakpoint();
  isMobile$ = this.breakpointService.isMobileBreakpoint();

  constructor(
    private eventService: EventService,
    private productDataService: ProductDataService,
    private priceService: PriceService,
    private productAvailabilityService: ProductAvailabilityService,
    private sessionStorageService: SessionStorageService,
    private winRef: WindowRef,
    private breakpointService: DistBreakpointService,
    private intersectionObserverService: IntersectionObserverService,
    private elementRef: ElementRef,
    private productQuantityService: ProductQuantityService,
    private salesStatusService: SalesStatusService,
  ) {}

  ngOnInit(): void {
    if (this.winRef.isBrowser()) {
      this.loadCollapseState();
    }

    this.isLoading$ = this.productDataService.isLoading(this.productCode);
    this.itemListEntity = this.getItemListEntity();
    this.availableAccessories$ = this.getAvailableAccessories().pipe(
      mergeMap((product) =>
        this.productAvailabilityService.getAvailability(product.code).pipe(
          take(1),
          mergeMap((stockData) => this.filterAvailableAccessories(product, stockData).pipe(take(1))),
          filter((hasStock) => hasStock),
          map(() => product),
        ),
      ),
      mergeMap((product) =>
        this.loadPrices(product.code).pipe(
          take(1),
          map((prices) => ({ ...product, ...prices })),
        ),
      ),
      toArray(),
      tap((products) => {
        if (products) {
          this.intersectionObserverService.observeViewport(
            this.elementRef.nativeElement,
            () => this.eventService.dispatch(createFrom(ViewItemListEvent, { products, listType: ItemListEntity.ACCESSORIES }))
          );
        }
      }),
    );
  }

  onViewAllOrLess(accessoriesLength) {
    // If already collapsed, set max total to 4 and collapsed to not collapsed
    if (this.collapsed) {
      this.showTotal = 4;
      this.collapsed = !this.collapsed;

      // If not collapsed, set max total to max accessories length and not collapsed to collapsed
    } else {
      this.showTotal = accessoriesLength;
      this.collapsed = !this.collapsed;
    }
    this.saveCollapseState();
  }

  saveCollapseState(): void {
    this.sessionStorageService.setItem(this.getAccessoriesSessionId(), this.collapsed ? '1' : '0');
  }

  getAccessoriesSessionId(): string {
    return `${this.productCode}_${this.accessoryType}_accessories_collapsed`;
  }

  loadCollapseState(): void {
    const sessionCollapseState = this.sessionStorageService.getItem(this.getAccessoriesSessionId());

    if (sessionCollapseState) {
      this.collapsed = sessionCollapseState === '1';

      if (this.collapsed) {
        this.showTotal = 10;
      }
    }
  }

  getItemListEntity(): ItemListEntity {
    if (this.accessoryType === 'optional') {
      return ItemListEntity.OPTIONAL;
    } else if (this.accessoryType === 'compatible') {
      return ItemListEntity.COMPATIBLE;
    } else if (this.accessoryType === 'mandatory') {
      return ItemListEntity.MANDATORY;
    } else if (this.accessoryType === 'alternatives') {
      return ItemListEntity.ALTERNATIVE;
    }
  }

  getAvailableAccessories(): Observable<Product> {
    try {
      switch (this.accessoryType) {
        case 'optional':
          return from(this.productReferences.accessories.carouselProducts);
        case 'compatible':
          return from(this.productReferences.crosselling.carouselProducts);
        case 'mandatory':
          return from(this.productReferences.mandatory.carouselProducts);
        case 'alternatives':
          return from(this.productReferences.alternatives.carouselProducts);
        default:
          return from([]);
      }
    } catch {
      return from([]);
    }
  }

  filterAvailableAccessories(product: Product, stockData: ProductAvailability): Observable<boolean> {
    if (stockData === null) {
      return EMPTY;
    }

    if (
      ((this.salesStatusService.endOfStockSalesStatus(product.salesStatus) ||
        stockData.stockLevels?.[0].mview === 'BTR') &&
        stockData?.stockLevelTotal === 0) ||
      this.salesStatusService.alwaysBlockedSalesStatus(product.salesStatus)
    ) {
      return of(true);
    }
    return this.productQuantityService
      .status50CountryCheck(product.salesStatus, stockData)
      .pipe(map((isStatus50Disabled) => !isStatus50Disabled));
  }

  private loadPrices(productCode: string): Observable<Prices> {
    return this.priceService.getPrices(productCode);
  }
}
