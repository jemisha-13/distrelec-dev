import { Injectable, Signal, WritableSignal, signal } from '@angular/core';
import { Price, ProductService } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, ReplaySubject } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { Prices, VolumePriceMap } from '@model/price.model';
import { ICustomProduct } from '@model/product.model';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { PriceService } from '@services/price.service';

@Injectable({ providedIn: 'root' })
export class ProductDataService {
  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  prices$ = new ReplaySubject<Prices>(1);

  activePrice$ = combineLatest([this.prices$, this.getFormQuantity().statusChanges]).pipe(
    filter(([prices, _]) => !!prices?.volumePricesMap),
    map(([prices, _]) => {
      let price: Price = prices.price;
      const selectedQuantity = this.getFormQuantity().value;

      prices.volumePricesMap.forEach((volumePriceMap: VolumePriceMap) => {
        if (selectedQuantity >= volumePriceMap.key) {
          price = this.priceService.getPriceFromVolumePrices(prices.volumePricesMap, volumePriceMap.value);
        }
      });

      return price;
    }),
  );

  private _selectedMedia: WritableSignal<string> = signal('');

  protected categoryCode$: BehaviorSubject<string> = new BehaviorSubject<string>(null);

  constructor(
    private productService: ProductService,
    private priceService: PriceService,
  ) {}

  // We set up as promise, so the behaviour subjects can be assigned data
  // And used for summary and breadcrumb modules
  getProductData(productCode: string): Observable<ICustomProduct> {
    return this.productService.get(productCode).pipe(
      filter(Boolean),
      tap((data) => {
        this.categoryCode$.next(data?.categories[0]?.code);
      }),
    ) as Observable<ICustomProduct>;
  }

  isLoading(productCode: string): Observable<boolean> {
    return this.productService.isLoading(productCode);
  }

  getCategoryCode(): Observable<string> {
    return this.categoryCode$.asObservable();
  }

  getFormQuantity(): UntypedFormControl {
    return this.addToCartForm.get('quantity') as UntypedFormControl;
  }


  get sharedMediaSignal(): Signal<string> {
    return this._selectedMedia;
  }

  updateMediaSignal(newValue: string): void {
    this._selectedMedia.set(newValue);
  }
}
