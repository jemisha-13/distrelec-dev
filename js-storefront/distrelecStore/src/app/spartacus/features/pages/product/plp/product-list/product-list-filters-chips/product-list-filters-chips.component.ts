import { Component, EventEmitter, Input, Output } from '@angular/core';
import { faXmark } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import {
  AppliedFilter,
  ProductListFilterService,
} from '@features/pages/product/core/services/product-list-filter.service';
import { Facet } from '@spartacus/core';

@Component({
  selector: 'app-product-list-filters-chips',
  templateUrl: './product-list-filters-chips.component.html',
  styleUrls: ['./product-list-filters-chips.component.scss'],
})
export class ProductListFiltersChipsComponent {
  @Input() mode: 'top' | 'sidebar' | 'mobile' = 'top';
  @Input() filters$: Observable<Facet[] | null>;

  @Output() removeOverflowHidden = new EventEmitter<void>();

  faXMark = faXmark;
  appliedFilters$: Observable<AppliedFilter[]> = this.plpFilterService.appliedFilters$;
  categoryFilterKey = this.plpFilterService.CATEGORY_FILTER_KEY;

  constructor(private plpFilterService: ProductListFilterService) {}

  get chips(): Observable<AppliedFilter[]> {
    return this.appliedFilters$.pipe(
      map((filters) => filters.filter((group) => group.count > 0)),
      map((filters) => this.transformFilterName(filters)),
    );
  }

  clearAll(): void {
    this.filters$.pipe(first()).subscribe((filters) => {
      filters?.map((group) => group.values?.forEach((filter) => (filter.checked = false)));

      this.plpFilterService.haveFiltersChanged.next(true);
      this.plpFilterService.clearFilters();
      this.removeOverflowHidden.emit();
    });
  }

  clearGroup(groupCode: string): void {
    let removedParam = groupCode;
    this.filters$.pipe(first()).subscribe((filters) => {
      filters
        ?.find((group) => group.code === groupCode)
        ?.values?.forEach((filter) => {
          if (filter.checked) {
            removedParam = filter.queryFilter.split('=')[0];
          }
          filter.checked = false;
        });
      if (groupCode.includes(this.plpFilterService.CATEGORY_FILTER_KEY)) {
        removedParam = 'filter_' + this.plpFilterService.CATEGORY_FILTER_KEY;
      }

      this.plpFilterService.removeFilter(removedParam);
    });
  }

  private transformFilterName(filters: AppliedFilter[]): AppliedFilter[] {
    return filters.map((filter) => {
      const isCategoryFilter =
        filter.code === this.categoryFilterKey || filter.code === 'filter_' + this.categoryFilterKey;

      return {
        ...filter,
        facetItems: filter.facetItems.map((facetItem) => ({
          ...facetItem,
          name: isCategoryFilter ? filter.name : `${facetItem.name} (${facetItem.count})`,
        })),
      };
    });
  }
}
