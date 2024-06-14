import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { LocalStorageService } from '@services/local-storage.service';

const PLP_PRODUCT_LIST_HIDE_FILTERS = 'PlpProductListHideFilters';


@Injectable({
  providedIn: 'root',
})
export class ProductListViewService {
  filtersHidden$: BehaviorSubject<boolean>;

  constructor(
    private localStorage: LocalStorageService,
  ) {
    this.filtersHidden$ = new BehaviorSubject<boolean>(this.getHideFilters());
  }

  getHideFilters(): boolean {
    return this.localStorage.getItem(PLP_PRODUCT_LIST_HIDE_FILTERS) ?? false;
  }

  setHideFilters(value: boolean): void {
    this.filtersHidden$.next(value);
    this.localStorage.setItem(PLP_PRODUCT_LIST_HIDE_FILTERS, value);
  }
}
