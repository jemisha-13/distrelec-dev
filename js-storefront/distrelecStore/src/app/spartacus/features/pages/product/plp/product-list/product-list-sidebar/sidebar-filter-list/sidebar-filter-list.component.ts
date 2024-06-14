import { Component } from '@angular/core';
import { faChevronDown } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { ProductListFilterService } from '@features/pages/product/core/services/product-list-filter.service';
import { arrowDown, arrowUp } from '@assets/icons/icon-index';

@Component({
  selector: 'app-sidebar-filter-list',
  templateUrl: './sidebar-filter-list.component.html',
  styleUrls: ['./sidebar-filter-list.component.scss'],
})
export class SidebarFilterListComponent {
  filters$ = this.plpFilterService.filters$;
  arrowUp = arrowUp;
  arrowDown = arrowDown;

  protected readonly faChevronDown = faChevronDown;

  constructor(
    private productListComponentService: DistProductListComponentService,
    private plpFilterService: ProductListFilterService,
  ) {}

  get totalCount(): Observable<number> {
    return this.productListComponentService.pagination$.pipe(map((pagination) => pagination.totalResults));
  }
}
