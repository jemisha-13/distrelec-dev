import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { faCheck } from '@fortawesome/free-solid-svg-icons';
import { ActivatedRoute, Router } from '@angular/router';
import {
  PaginationQueryKey,
  SelectOptions,
  SORT_OPTIONS,
} from '@features/pages/product/plp/product-list/product-list-main/product-list-main-options';

@Component({
  selector: 'app-product-list-sort-mobile',
  templateUrl: './product-list-sort-mobile.component.html',
  styleUrls: ['./product-list-sort-mobile.component.scss'],
})
export class ProductListSortMobileComponent implements OnInit {
  faCheck = faCheck;
  @Output() closeEmitter = new EventEmitter();

  selectedOption: string;
  options: SelectOptions[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.options = SORT_OPTIONS.options;
    this.selectedOption = this.route.snapshot.queryParams[PaginationQueryKey.SORT] ?? (this.options[0].value as string);
  }

  ngOnInit(): void {}

  setOption(val: string): void {
    this.selectedOption = val;
  }

  filterList(queryKey: string, queryValue: any): void {
    const filterParam = { [queryKey]: queryValue };
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { ...this.route.snapshot.queryParams, ...filterParam },
    });
    this.closeEmitter.emit();
  }
}
