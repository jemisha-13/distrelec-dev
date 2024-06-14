import { Component, ElementRef, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { Facet, FacetValue } from '@spartacus/core';
import {
  minMaxSelection,
  resetMinMaxSelection,
} from '@features/pages/product/plp/product-list/min-max-selection/min-max-selection';
import { TRANSLATED_FACETS } from '@features/pages/product/plp/product-list/product-list-filters/translated-facets';
import { ProductListFilterService } from '@features/pages/product/core/services/product-list-filter.service';
import { search } from '@assets/icons/definitions/search';

@Component({
  selector: 'app-product-list-filter-values',
  templateUrl: './product-list-filter-values.component.html',
  styleUrls: ['./product-list-filter-values.component.scss'],
})
export class ProductListFilterValuesComponent implements OnInit {
  @Input() facet: Facet;
  @Input() index: number;
  @Input() expandable = false;

  readonly faSearch = faSearch;
  hasLocalizedName = false;
  isExpanded = false;
  iconSearch = search;

  private minSelect?: HTMLSelectElement;
  private maxSelect?: HTMLSelectElement;

  constructor(
    private plpFilterService: ProductListFilterService,
    private renderer: Renderer2,
  ) {}

  ngOnInit(): void {
    this.hasLocalizedName = TRANSLATED_FACETS.includes(this.facet.code);
  }

  @ViewChild('selectMin')
  private set selectMin(content: ElementRef) {
    if (content) {
      this.minSelect = content.nativeElement;
    }
  }
  @ViewChild('selectMax')
  private set selectMax(content: ElementRef) {
    if (content) {
      this.maxSelect = content.nativeElement;
    }
  }

  isResetBtnDisabled(facet: Facet): boolean {
    return !facet.values.some((filter) => filter.checked);
  }

  onCheckboxChange(event: Event, filter: FacetValue, facet: Facet): void {
    filter.checked = (event.target as HTMLInputElement).checked;
    this.onFilterChange(facet);
  }

  onFilterChange(facet: Facet, resetCheckboxes = false): void {
    resetMinMaxSelection(facet, resetCheckboxes, [this.minSelect, this.maxSelect]);

    if (this.facet.hasMinMaxFilters) {
      (this.minSelect.children[0] as HTMLOptionElement).selected = true;
      (this.maxSelect.children[0] as HTMLOptionElement).selected = true;
    }

    this.plpFilterService.onFilterChange();
  }

  onSelectChange(facet: Facet, boundary: 'min' | 'max'): void {
    minMaxSelection(facet, boundary, this.minSelect, this.maxSelect);
    this.plpFilterService.onFilterChange();
  }

  onResetClick(facet: Facet): void {
    this.onFilterChange(facet, true);
  }

  showFilterToggle(toggleElement: HTMLUListElement) {
    this.isExpanded = !this.isExpanded;
    this.renderer.setStyle(toggleElement, 'max-height', this.isExpanded ? '240px' : '150px');
  }
}
