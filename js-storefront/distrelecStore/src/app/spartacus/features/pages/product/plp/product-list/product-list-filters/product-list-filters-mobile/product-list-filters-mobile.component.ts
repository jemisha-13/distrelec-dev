import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { faAngleDown, faAngleUp } from '@fortawesome/free-solid-svg-icons';
import { TRANSLATED_FACETS } from '@features/pages/product/plp/product-list/product-list-filters/translated-facets';
import {
  AppliedFilter,
  ProductListFilterService,
} from '@features/pages/product/core/services/product-list-filter.service';
import { Facet, FacetValue } from '@spartacus/core';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-product-list-filters-mobile',
  templateUrl: './product-list-filters-mobile.component.html',
  styleUrls: ['./product-list-filters-mobile.component.scss'],
})
export class ProductListFiltersMobileComponent {
  @Input() filters$: Observable<Facet[]>;
  @Input() totalResults: number;
  @Input() loading: boolean;
  @Input() filterDisplayState: boolean;
  @Output() closeEmitter = new EventEmitter();
  @Output() filterChange = new EventEmitter<Facet>();
  @Output() applyFiltersEmitter = new EventEmitter();

  translatedFacets: string[];
  checkBoxChecked: BehaviorSubject<string> = new BehaviorSubject<string>('All');
  haveFiltersChanged = this.filterService.haveFiltersChanged;
  faAngleUp = faAngleUp;
  faAngleDown = faAngleDown;
  filterOpen?: number; // determines which filter is open
  searchTerm = '';

  constructor(private filterService: ProductListFilterService) {
    this.translatedFacets = TRANSLATED_FACETS;
  }
  get appliedFilters(): Observable<AppliedFilter[]> {
    return this.filterService.appliedFilters$;
  }

  applyFilters(removedParam?: string): void {
    if (removedParam) {
      this.filterService.removeFilter(removedParam);
    } else {
      this.filterService.applyFilters();
    }
  }

  openFilterValues(itemIndex: number): void {
    this.setCheckBoxChecked(itemIndex);
    this.searchTerm = '';

    if (this.filterOpen === itemIndex) {
      this.filterOpen = undefined;
    } else {
      this.filterOpen = itemIndex;
    }
  }

  updateCheckBoxCount(): void {
    this.setCheckBoxChecked(this.filterOpen as number);
  }

  setCheckBoxChecked(index: number): void {
    this.filters$.pipe(first()).subscribe((filters) => {
      const getCheckedCheckBoxCount = filters[index].values.filter((v: FacetValue) => v.selected).length;

      this.checkBoxChecked.next(getCheckedCheckBoxCount !== 0 ? `(${getCheckedCheckBoxCount})` : 'All');
    });
  }

  closeModal() {
    this.closeEmitter.emit();
    this.filterOpen = undefined;
    this.searchTerm = '';
  }

  clearAllFilters() {
    this.filterService.clearFilters();
    this.closeModal();
  }
}
