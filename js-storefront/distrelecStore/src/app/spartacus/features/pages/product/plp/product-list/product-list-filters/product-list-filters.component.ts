import { Component } from '@angular/core';
import { merge, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { WindowRef } from '@spartacus/core';
import { DistBreakpointService } from '@services/breakpoint.service';
import { DistProductListComponentService } from '../../../core/services/dist-product-list-component.service';
import {
  AppliedFilter,
  ProductListFilterService,
} from '@features/pages/product/core/services/product-list-filter.service';
import { ProductListViewService } from '@features/pages/product/core/services/product-list-view.service';

@Component({
  selector: 'app-product-list-filters',
  templateUrl: './product-list-filters.component.html',
  styleUrls: ['./product-list-filters.component.scss'],
})
export class ProductListFiltersComponent {
  filtersHidden = this.plpViewService.filtersHidden$;

  mFiltersOpen = false;
  mSortOpen = false;

  filters$ = this.plpFilterService.filters$;
  categories$ = this.plpComponentService.categoryFilters$;
  isPlpActive$ = this.plpComponentService.isPlpActive$;
  haveFiltersChanged = this.plpFilterService.haveFiltersChanged;

  isPLPLoading_ = this.plpComponentService.isLoading$;
  showApplyFilters = this.plpFilterService.showApplyFilters;
  activeFilteredCount$ = this.plpFilterService.activeFilteredCount$;

  constructor(
    private plpFilterService: ProductListFilterService,
    private plpViewService: ProductListViewService,
    private plpComponentService: DistProductListComponentService,
    private _winRef: WindowRef,
    private breakpointService: DistBreakpointService,
  ) {}

  get isMobileBreakpoint(): Observable<boolean> {
    return this.breakpointService.isMobileBreakpoint();
  }

  get totalCount(): Observable<number | null> {
    return this.plpComponentService.pagination$.pipe(map((pagination) => pagination.totalResults));
  }

  get loading(): Observable<boolean> {
    return this.plpFilterService.loading$;
  }

  get appliedFilters(): Observable<AppliedFilter[]> {
    return this.plpFilterService.appliedFilters$;
  }

  get totalAppliedFilters(): Observable<number> {
    return this.appliedFilters.pipe(map((filters) => filters.filter((groups) => groups.count > 0).length));
  }

  toggleSortVisibility(): void {
    if (this._winRef.isBrowser()) {
      const headerClassList = this._winRef.document.querySelector('header').classList;
      headerClassList.toggle('d-none');
    }
    this.mSortOpen = !this.mSortOpen;
    this.toggleScrolling(this.mSortOpen);
  }

  toggleMobileFilters() {
    if (this._winRef.isBrowser()) {
      const headerClassList = this._winRef.document.querySelector('header').classList;
      headerClassList.toggle('d-none');
    }
    this.mFiltersOpen = !this.mFiltersOpen;
    this.toggleScrolling(this.mFiltersOpen);
  }

  toggleScrolling(value: boolean): void {
    document.body.classList.toggle('overflow-hidden', value);
  }

  applyFilters(): void {
    this.plpFilterService.applyFilters();
  }

  onFilterChange() {
    this.plpFilterService.onFilterChange();
  }

  setHideFilters(value: boolean) {
    this.plpViewService.setHideFilters(value);
  }
}
