import { Component, Input } from '@angular/core';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import {
  AppliedFilter,
  ProductListFilterService,
} from '@features/pages/product/core/services/product-list-filter.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-product-list-filters-selected',
  templateUrl: './product-list-filters-selected.component.html',
  styleUrls: ['./product-list-filters-selected.component.scss'],
})
export class ProductListFiltersSelectedComponent {
  showApplyFilters = this.plpFilterService.showApplyFilters;
  activeFilteredCount$ = this.plpFilterService.activeFilteredCount$;
  haveFiltersChanged: BehaviorSubject<boolean>;
  appliedFilters$: Observable<AppliedFilter[]> = this.plpFilterService.appliedFilters$;

  constructor(
    private plpFilterService: ProductListFilterService,
    private productListService: DistProductListComponentService,
  ) {
    this.haveFiltersChanged = this.plpFilterService.haveFiltersChanged;
  }

  get totalCount(): Observable<number> {
    return this.productListService.pagination$.pipe(map((pagination) => pagination.totalResults));
  }

  get chips(): Observable<AppliedFilter[]> {
    return this.appliedFilters$.pipe(map((filters) => filters.filter((group) => group.count > 0)));
  }

  applyFilters(): void {
    this.plpFilterService.applyFilters();
  }
}
