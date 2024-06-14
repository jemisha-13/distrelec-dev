import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService, UserIdService, WindowRef } from '@spartacus/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, map, takeUntil } from 'rxjs/operators';
import { ProductEnhancement } from '@model/product.model';

type Cache<T> = Record<string, Subject<T>>;

type ProductEnhancementResponse = {
  productEnhancements: ProductEnhancement[];
};

@Injectable({
  providedIn: 'root',
})
export class ProductEnhancementsService implements OnDestroy {
  private cache: Cache<ProductEnhancement> = {};
  private destroyed$ = new ReplaySubject<void>(1);

  constructor(
    private occEndpoints: OccEndpointsService,
    private http: HttpClient,
    private winRef: WindowRef,
    private userIdService: UserIdService,
  ) {
    this.userIdService
      .getUserId()
      .pipe(takeUntil(this.destroyed$), distinctUntilChanged())
      .subscribe(() => this.reset());
  }

  ngOnDestroy() {
    this.reset();
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  reset(): void {
    Object.values(this.cache).forEach((subject) => subject.complete());
    this.cache = {};
  }

  load(productCodes: string[]): void {
    if (!this.winRef.isBrowser()) {
      return;
    }
    this.createIfNotCached(productCodes);
    this.fetchEnhancements(productCodes).subscribe((enhancements) => {
      enhancements.forEach((enhancement) => this.addToCache(enhancement));
    });
  }

  get(productCode: string, forceRefresh = false): Observable<ProductEnhancement> {
    let shouldLoad = false;

    if (!this.isCached(productCode)) {
      this.createCacheEntry(productCode);
      shouldLoad = true;
    }

    if (forceRefresh || shouldLoad) {
      this.load([productCode]);
    }

    return this.cache[productCode].asObservable();
  }

  isInShoppingList(productCode: string): Observable<boolean> {
    return this.get(productCode).pipe(map((enhancement) => Boolean(enhancement?.inShoppingList)));
  }

  isPunchedOut(productCode: string): Observable<boolean> {
    return this.get(productCode).pipe(map((enhancement) => Boolean(enhancement?.exclusionReason)));
  }

  getPunchOutReason(productCode: string): Observable<string | null> {
    return this.get(productCode).pipe(map((enhancement) => enhancement?.exclusionReason ?? null));
  }

  private fetchEnhancements(productCodes: string[]): Observable<ProductEnhancement[]> {
    const url = this.occEndpoints.buildUrl(`/products/enhance`, {
      queryParams: { productCodes: productCodes.join(',') },
    });
    return this.http.get<ProductEnhancementResponse>(url, { headers: { 'Cache-Control': 'no-cache' } }).pipe(
      map((response) => response?.productEnhancements ?? []),
      catchError(() => {
        console.warn('Failed to fetch product enhancements');
        return [];
      }),
    );
  }

  private isCached(productCode: string): boolean {
    return !!this.cache[productCode];
  }

  private createCacheEntry(productCode: string): void {
    this.cache[productCode] = new ReplaySubject<ProductEnhancement>(1);
  }

  private createIfNotCached(productCodes: string[]): void {
    productCodes.forEach((productCode) => {
      if (!this.isCached(productCode)) {
        this.createCacheEntry(productCode);
      }
    });
  }

  private addToCache(enhancement: ProductEnhancement): void {
    if (!this.isCached(enhancement.productCode)) {
      this.createCacheEntry(enhancement.productCode);
    }
    this.cache[enhancement.productCode].next(enhancement);
  }
}
