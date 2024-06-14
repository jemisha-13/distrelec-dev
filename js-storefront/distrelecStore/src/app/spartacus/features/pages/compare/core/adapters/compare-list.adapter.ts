import { Injectable } from '@angular/core';
import {
  CompareList,
  CompareProductAddedResponse,
  CompareProductRemovedResponse,
  CompareShortData,
} from '@model/compare.model';
import { OCC_USER_ID_ANONYMOUS, OCC_USER_ID_CURRENT } from '@spartacus/core';
import { DistCookieService } from '@services/dist-cookie.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { OccCompareListAdapter } from './occ-compare-list.adapter';

const initialState: CompareList = {
  uniqueId: '',
  totalUnitCount: 0,
  products: [],
};

// This would be an adapter that makes calls to OCC adapter
// And holds the state of product list data
@Injectable({
  providedIn: 'root',
})
export class CompareListAdapterService {
  private readonly store$ = new BehaviorSubject<CompareList>(initialState);

  constructor(
    private occCompareListAdapterService: OccCompareListAdapter,
    private cookieService: DistCookieService,
  ) {}

  loadCompareListForCurrent(): Observable<CompareList> {
    return this.occCompareListAdapterService.loadCompareListShortData().pipe(
      switchMap((shortData: CompareShortData) =>
        this.occCompareListAdapterService.loadCompareProductList(OCC_USER_ID_CURRENT, shortData.uniqueId).pipe(
          map((compareList) => ({
            uniqueId: shortData.uniqueId,
            totalUnitCount: shortData.totalUnitCount,
            products: compareList.products,
          })),
          tap((compareList: CompareList) => this.setCompareListState(compareList)),
        ),
      ),
    );
  }

  loadCompareListForAnonymous(): Observable<CompareList> {
    const arrayOfCodes = this.cookieService.get('compareProductCodes')
      ? JSON.parse(this.cookieService.get('compareProductCodes'))
      : [];

    if (arrayOfCodes.length > 0) {
      return this.occCompareListAdapterService
        .loadCompareProductList(OCC_USER_ID_ANONYMOUS)
        .pipe(tap((compareList) => this.setCompareListState(compareList)));
    }

    this.setCompareListState(initialState);
    return this.getCompareListState();
  }

  addCompareProductForCurrent(productCode: string): Observable<CompareProductAddedResponse> {
    return this.occCompareListAdapterService.saveCompareProduct(productCode).pipe(
      tap((compareList) =>
        this.setCompareListState({
          ...this.store$.value,
          totalUnitCount: compareList.products.length,
          products: compareList.products,
        }),
      ),
    );
  }

  addCompareProductForAnonymous(productCode): Observable<CompareList> {
    const arrayOfCodes = this.cookieService.get('compareProductCodes')
      ? JSON.parse(this.cookieService.get('compareProductCodes'))
      : [];
    if (arrayOfCodes.indexOf(productCode) < 0) {
      arrayOfCodes.push(productCode);
      this.cookieService.set('compareProductCodes', JSON.stringify(arrayOfCodes), 7, '/');
    }

    return this.occCompareListAdapterService.loadCompareProductList(OCC_USER_ID_ANONYMOUS).pipe(
      tap((compareList) => {
        this.store$.next({
          ...this.store$.value,
          totalUnitCount: compareList.totalUnitCount,
          products: compareList.products,
        });
      }),
    );
  }

  removeCompareProductForCurrent(productCodes: string[]): Observable<CompareProductRemovedResponse> {
    return this.occCompareListAdapterService.removeCompareProduct(productCodes, this.store$.value?.uniqueId);
  }

  removeCompareProductForAnonymous(productCodes: string[]): Observable<CompareProductRemovedResponse> {
    const cookieValue = JSON.parse(this.cookieService.get('compareProductCodes'));
    productCodes.forEach((productCode) => {
      const indexOfProduct = cookieValue.indexOf(productCode);
      if (indexOfProduct !== -1) {
        cookieValue.splice(indexOfProduct, 1);
      }
    });
    this.cookieService.set('compareProductCodes', JSON.stringify(cookieValue), 7, '/');

    return of({ status: true });
  }

  getCompareListState(): Observable<CompareList> {
    return this.store$.asObservable();
  }

  private setCompareListState(compareList: CompareList): void {
    this.store$.next(compareList);
  }
}
