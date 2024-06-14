import { HttpClient } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { BaseSite, BaseSiteService, OccEndpointsService, UserIdService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, of, ReplaySubject, Subject, Subscription } from 'rxjs';
import { distinctUntilChanged, map, takeUntil, tap } from 'rxjs/operators';
import { AvailabilityResponse, ProductAvailability } from '@model/product-availability.model';
import { SalesStatusService } from '@services/sales-status.service';
import debounce from 'lodash-es/debounce';
import { iconClock, tickCancelCircle, xInStock } from '@assets/icons';

type Cache<T> = Record<string, Subject<T>>;

export enum ManufacturerCodesEnum {
  RS = 'RSP',
}

@Injectable({
  providedIn: 'root',
})
export class ProductAvailabilityService implements OnDestroy {
  stockLevelTotal_: BehaviorSubject<number> = new BehaviorSubject<number>(null);
  productsAvailability_: BehaviorSubject<ProductAvailability[]> = new BehaviorSubject<ProductAvailability[]>(null);
  productsAvailabilityWithQty_: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  iconClock = iconClock;
  tickCancelCircle = tickCancelCircle;
  xInStock = xInStock;

  private subscriptions = new Subscription();
  private activeBaseSite: BaseSite;

  private cache: Cache<ProductAvailability> = {};
  private queuedCodes = new Set<string>();

  private debouncedFetchAvailabilities = debounce(() => {
    if (this.winRef.isBrowser()) {
      const productCodes = Array.from(this.queuedCodes);
      if (productCodes.length === 0) {
        return; // nothing to do
      }
      this.queuedCodes.clear();

      this.fetchProductAvailabilities(productCodes).subscribe((availabilities) => {
        const returnedCodes = new Set<string>();
        availabilities.forEach((availability) => {
          this.cacheAvailability(availability);
          returnedCodes.add(availability.productCode);
        });

        productCodes.forEach((code) => {
          if (!returnedCodes.has(code)) {
            this.cache[code].error(new Error(`Product ${code} is not available`));
          }
        });
      });
    }
  }, 100);

  private destroyed$ = new ReplaySubject<void>(1);

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private baseSiteService: BaseSiteService,
    private salesStatusService: SalesStatusService,
    private winRef: WindowRef,
    private userIdService: UserIdService,
  ) {
    if (this.winRef.isBrowser()) {
      this.subscriptions.add(this.baseSiteService.get().subscribe((baseSite) => (this.activeBaseSite = baseSite)));

      this.userIdService
        .getUserId()
        .pipe(takeUntil(this.destroyed$), distinctUntilChanged())
        .subscribe(() => this.reset());
    }
  }

  ngOnDestroy() {
    if (this.winRef.isBrowser()) {
      this.subscriptions.unsubscribe();
      this.reset();
    }
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  reset(): void {
    if (this.winRef.isBrowser()) {
      this.productsAvailability_.next(null);
      this.productsAvailabilityWithQty_.next(null);
      Object.values(this.cache).forEach((subject) => subject.complete());
      this.cache = {};
    }
  }

  getAvailabilityWithQuantity(
    productCodes?: Array<string>,
    quantityArray?: Array<string>,
  ): Observable<ProductAvailability[]> {
    if (!this.winRef.isBrowser()) {
      return of([]);
    }

    const payload = [];

    for (let i = 0; i < productCodes.length; i++) {
      payload.push(`${productCodes[i]};${quantityArray[i]}`);
    }

    const url = this.occEndpoints.buildUrl('products/availability', {
      queryParams: {
        fields: 'FULL',
        productCodes: payload,
      },
    });

    return this.http.get<AvailabilityResponse>(url).pipe(
      map((data) => data.productAvailability),
      tap((data) => {
        this.productsAvailabilityWithQty_.next(data);
      }),
    );
  }

  /**
   * Prefer getAvailability() as it will return value from cache and fetch data in batches
   *
   * @param productCodes
   */
  getProductAvailabilities(productCodes: Array<string>): Observable<ProductAvailability[]> {
    if (!this.winRef.isBrowser()) {
      return of([]);
    }

    return this.fetchProductAvailabilities(productCodes).pipe(
      tap((availabilities) => {
        availabilities.forEach((availability) => this.cacheAvailability(availability));
        this.productsAvailability_.next(availabilities);
      }),
    );
  }

  /**
   * Get product availability from cache.
   * If no value is cached for the product it's added to a queue and fetched in a batch after 100ms
   *
   * @param productCode
   * @param immediate - if true, fetches availability immediately, bypassing the 100ms delay
   */
  getAvailability(productCode: string, immediate?: boolean): Observable<ProductAvailability | null> {
    if (!this.winRef.isBrowser()) {
      return of(null);
    }

    if (!this.cache[productCode]) {
      this.cache[productCode] = new ReplaySubject<ProductAvailability>(1);
      this.queuedCodes.add(productCode);
    }
    this.debouncedFetchAvailabilities();
    if (immediate) {
      this.debouncedFetchAvailabilities.flush();
    }
    return this.cache[productCode];
  }

  isOutOfStock(availability?: ProductAvailability): boolean {
    if (this.winRef.isBrowser()) {
      if (!availability) {
        return true;
      }

      return availability.stockLevelTotal === 0;
    }
  }

  fetchProductsAccessibility(productCodes: string): Observable<string[]> {
    if (!this.winRef.isBrowser()) {
      return of([]);
    }

    return this.http
      .get<any>(this.buildProductAccessibilityEndpoint(productCodes))
      .pipe(map((data) => data.productAccessibility));
  }

  // We can have a case that in the cart we have two products with same productCode (one normal and one quote product)
  // BE will ignore this and will give us only one availability for both items in response
  // Hence we are extending availabilites array with additional same item
  // So it matches with cart item index as we are iterating through them in template file
  // Usage example: "features/pages/cart/desktop-cart/desktop-cart.component.html" -> "productAvailability[i]"
  extendAvailabilitiesForMultipleSameProducts(
    availabilities: ProductAvailability[],
    productCodes: string[],
  ): ProductAvailability[] {
    if (this.winRef.isBrowser()) {
      if (availabilities.length === productCodes.length) {
        this.productsAvailability_.next(availabilities);
        return availabilities;
      } else {
        const extendedProductAvailabilities = [];
        productCodes.forEach((productCode) => {
          const matchingAvailability = availabilities.find((availability) => availability.productCode === productCode);
          if (matchingAvailability) {
            extendedProductAvailabilities.push(matchingAvailability);
          }
        });

        this.productsAvailability_.next(extendedProductAvailabilities);
        return extendedProductAvailabilities;
      }
    }
  }

  protected buildProductAvailabilityEndpoint(productCodes: Array<string>): string {
    if (this.winRef.isBrowser()) {
      return this.occEndpoints.buildUrl('products/availability', {
        queryParams: {
          fields: 'FULL',
          productCodes,
        },
      });
    }
  }

  protected buildProductAccessibilityEndpoint(productCodes: string): string {
    if (this.winRef.isBrowser()) {
      return this.occEndpoints.buildUrl('products/accessibility', {
        queryParams: {
          fields: 'FULL',
          productCodes,
        },
      });
    }
  }

  private fetchProductAvailabilities(productCodes: string[]): Observable<ProductAvailability[]> {
    if (!this.winRef.isBrowser()) {
      return of(null);
    }

    return this.http
      .get<any>(this.buildProductAvailabilityEndpoint(productCodes))
      .pipe(map((data) => this.extendAvailabilitiesForMultipleSameProducts(data.productAvailability, productCodes)));
  }

  private cacheAvailability(availability: ProductAvailability): void {
    if (this.winRef.isBrowser()) {
      const code = availability?.productCode;
      if (!this.cache[code]) {
        this.cache[code] = new ReplaySubject<ProductAvailability>(1);
      }
      this.cache[code].next(availability);
    }
  }

  getConfig(salesStatus, availability: ProductAvailability) {
    const isWaldom = availability?.stockLevels[0]?.waldom;

    if (availability?.stockLevelTotal > 0) {
      if (salesStatus === '20') {
        return { message: 'salesStatus.pdp.status_20', icon: this.iconClock };
      }
      if (salesStatus === '21') {
        return { message: 'salesStatus.pdp.status_21', icon: this.iconClock };
      }
      if (salesStatus > '21' && salesStatus <= '53' && salesStatus !== '52') {
        return {
          message: 'plp.facet_buttons_and_headers.in_stock',
          icon: this.xInStock,
          stockTotal: availability?.stockLevelTotal,
        };
      }
      if (salesStatus === '52') {
        if (availability?.stockLevels?.[0]?.available > 0 && availability?.stockLevels?.[1]?.available === 0) {
          return {
            message: 'product.status.currently.not.available',
            class: 'tick-cancel-circle-grey',
            icon: this.tickCancelCircle,
          };
        } else {
          return {
            message: 'plp.facet_buttons_and_headers.in_stock',
            icon: this.xInStock,
            stockTotal: availability?.stockLevelTotal,
          };
        }
      }
      if (salesStatus > '53') {
        return {
          message: 'product.status.nolongeravailable',
          class: 'tick-cancel-circle-grey',
          icon: this.tickCancelCircle,
        };
      }
    }

    if (availability?.stockLevelTotal === 0) {
      if (salesStatus === '20') {
        return { message: 'salesStatus.pdp.status_20', icon: this.iconClock };
      }
      if (salesStatus === '21') {
        return { message: 'salesStatus.pdp.status_21', icon: this.iconClock };
      }
      if ((salesStatus === '30' || salesStatus === '31') && isWaldom) {
        return {
          message: 'product.status.currently.not.available',
          class: 'tick-cancel-circle-grey',
          icon: this.tickCancelCircle,
        };
      }
      if ((salesStatus === '30' || salesStatus === '31') && !isWaldom) {
        return {
          message: 'plp.facet_buttons_and_headers.available_to_back_order',
          icon: this.iconClock,
          class: 'check-icon',
        };
      }
      if (salesStatus === '40' || salesStatus === '41' || salesStatus === '60' || salesStatus === '61') {
        return { message: 'product.status.nolongeravailable', icon: this.tickCancelCircle };
      }
      if ((salesStatus >= '50' && salesStatus <= '53') || salesStatus === '90') {
        return {
          message: 'product.status.currently.not.available',
          class: 'tick-cancel-circle-grey',
          icon: this.tickCancelCircle,
        };
      }
    }
  }
}
