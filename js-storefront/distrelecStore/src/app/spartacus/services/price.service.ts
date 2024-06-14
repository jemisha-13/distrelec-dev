import { Injectable, OnDestroy } from '@angular/core';
import { Prices, VolumePrice, VolumePriceMap, VolumePriceType } from '@model/price.model';
import { Channel } from '@model/site-settings.model';
import { EMPTY, Observable, of, ReplaySubject, Subject, timer } from 'rxjs';
import { OccEndpointsService, Price, UserIdService, WindowRef } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { catchError, switchMap, take, takeUntil, tap } from 'rxjs/operators';

const STORAGE_KEY = 'prices';
const CLEANUP_TIME = 3600000;

@Injectable({
  providedIn: 'root',
})
export class PriceService implements OnDestroy {
  private cache: Map<string, Observable<Prices>> = new Map();
  private parsedData: Map<string, Prices> = new Map();

  private readonly stopCleanup$: Subject<void> = new Subject<void>();

  constructor(
    private occEndpoints: OccEndpointsService,
    private http: HttpClient,
    private winRef: WindowRef,
    private userIdService: UserIdService,
  ) {
    if (!this.winRef.isBrowser()) {
      return;
    }

    this.setupLocalStorageCache();
  }

  getPriceFromVolumePrices(volumePricesMap: VolumePriceMap[], volumePrices: VolumePrice[]): Price {
    if (this.hasCustomPrices(volumePricesMap)) {
      return this.getCustomPrice(volumePrices);
    }
    return this.getListPrice(volumePrices);
  }

  getVolumePriceForQuantity(volumePricesMap: VolumePriceMap[], quantity: number): VolumePrice[] {
    quantity = quantity < volumePricesMap[0]?.key ? volumePricesMap[0]?.key : quantity;
    return volumePricesMap?.reduce((previous, current) => (quantity >= current.key ? current : previous), {
      key: 0,
      value: [],
    }).value;
  }

  getVolumePriceForMinQuantity(volumePricesMap: VolumePriceMap[]): VolumePrice[] {
    return volumePricesMap[0].value;
  }

  getPriceForQuantity(volumePricesMap: VolumePriceMap[], quantity: number, channel: Channel): number {
    const price: Price = this.getPriceFromVolumePrices(
      volumePricesMap,
      this.getVolumePriceForQuantity(volumePricesMap, quantity),
    );
    return this.getPriceBasedOnChannel(price, channel);
  }

  getCurrencyFromPrice(volumePricesMap: VolumePriceMap[]): string {
    try {
      return this.getPriceFromVolumePrices(volumePricesMap, this.getVolumePriceForMinQuantity(volumePricesMap))
        .currencyIso;
    } catch {
      console.warn('Failed to get currency from price');
      return '';
    }
  }

  hasDiscountPrices(volumePricesMap: VolumePriceMap[]): boolean {
    return volumePricesMap?.some((volumePriceMap) => this.isVolumePriceDiscounted(volumePriceMap.value));
  }

  hasCustomPrices(volumePricesMap: VolumePriceMap[]): boolean {
    return volumePricesMap?.every((volumePriceMap) => this.getCustomPrice(volumePriceMap.value));
  }

  isVolumePriceDiscounted(volumePrices: VolumePrice[]): boolean {
    const customPrice = this.getCustomPrice(volumePrices);
    const listPrice = this.getListPrice(volumePrices);

    return this.isPriceDiscounted(customPrice, listPrice);
  }

  isPriceDiscounted(customPrice: Price, listPrice: Price): boolean {
    return listPrice && customPrice && listPrice.value > customPrice?.value;
  }

  getCustomPrice(volumePrices: VolumePrice[]): Price {
    return volumePrices?.find((volumePrice) => volumePrice.key === VolumePriceType.CUSTOM)?.value;
  }

  getListPrice(volumePrices: VolumePrice[]): Price {
    return volumePrices?.find((volumePrice) => volumePrice.key === VolumePriceType.LIST)?.value;
  }

  getPriceBasedOnChannel(price: Price, channel: Channel): number {
    if (channel === 'B2B') {
      return this.getPriceWithoutVatForB2B(price);
    }

    return this.getPriceWithVatForB2C(price);
  }

  getPriceWithVatForB2C(price: Price): number {
    return price?.priceWithVat > 0 ? price?.priceWithVat : price?.value;
  }

  getPriceWithoutVatForB2B(price: Price): number {
    return price?.priceWithVat > 0 ? price?.basePrice : price?.value;
  }

  getPrices(productCode: string): Observable<Prices> {
    if (!this.winRef.isBrowser()) {
      return EMPTY;
    }

    const cachedData: Observable<Prices> = this.cache.get(productCode);
    if (cachedData) {
      return cachedData;
    } else {
      return this.fetchPrices(productCode).pipe(
        tap((data: Prices): void => {
          this.cache.set(productCode, of(data));
          this.parsedData.set(productCode, data);
          this.saveToLocalStorage();
        }),
      );
    }
  }

  fetchPrices(productCode: string): Observable<Prices> {
    return this.userIdService.takeUserId().pipe(
      switchMap((userId: string) => {
        const url = this.occEndpoints.buildUrl(`users/${userId}/products/${productCode}/prices`, {
          queryParams: { fields: 'volumePrices(FULL),volumePricesMap(FULL),price(FULL),promotionEndDate' },
        });

        return this.http.get<Prices>(url).pipe(
          take(1),
          catchError(() => EMPTY),
        );
      }),
    );
  }

  setupLocalStorageCache() {
    this.setupLocalStorageCleanup();
    this.loadFromLocalStorage();
  }

  cleanUpPriceCache(): void {
    this.stopCleanup$.next();
    this.stopCleanup$.complete();
  }

  cleanPricesLocalStorage(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.cache.clear();
  }

  ngOnDestroy() {
    this.cleanUpPriceCache();
  }

  private saveToLocalStorage(): void {
    const parsedDataArray: [string, Prices][] = Array.from(this.parsedData.entries());
    localStorage.setItem(STORAGE_KEY, JSON.stringify(parsedDataArray));
  }

  private loadFromLocalStorage(): void {
    const storedData: string = localStorage.getItem(STORAGE_KEY);
    if (storedData) {
      const parsedDataArray = JSON.parse(storedData);
      this.parsedData = new Map(parsedDataArray);
      parsedDataArray.forEach(([key, value]) => {
        this.cache.set(key, of(value));
      });
    }
  }

  private setupLocalStorageCleanup(): void {
    timer(CLEANUP_TIME, CLEANUP_TIME)
      .pipe(
        tap((): void => {
          this.cleanPricesLocalStorage();
        }),
        takeUntil(this.stopCleanup$),
      )
      .subscribe();
  }
}
