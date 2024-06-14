import { Component } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { faChevronDown } from '@fortawesome/free-solid-svg-icons';
import { CategoryFilter } from '@model/index';
import { ProductListFilterService } from '@features/pages/product/core/services/product-list-filter.service';
import { ProductListViewService } from '@features/pages/product/core/services/product-list-view.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { DistProductListComponentService } from '../../../core/services/dist-product-list-component.service';
import { arrowDown, arrowUp } from '@assets/icons/icon-index';
import { HeaderService } from '@features/shared-modules/header/header.service';

@Component({
  selector: 'app-product-list-sidebar',
  templateUrl: './product-list-sidebar.component.html',
  styleUrls: ['./product-list-sidebar.component.scss'],
})
export class ProductListSidebarComponent {
  pageType = '';
  isCategoriesVisible: boolean;
  filters$ = this.plpFilterService.filters$;
  arrowUp = arrowUp;
  arrowDown = arrowDown;
  showStickyHeader$ = this.headerService.showStickyHeader.asObservable();
  headerWarningsActive$: Observable<boolean> = this.headerService.hasActiveWarnings$;

  isVisible$: Observable<boolean> = combineLatest([
    this.productListComponentService.isPlpActive$,
    this.breakpointService.isMobileBreakpoint(),
  ]).pipe(
    map(
      ([isPlpActive, isMobileBreakpoint]) =>
        isPlpActive && !isMobileBreakpoint,
    ),
  );

  constructor(
    private productListComponentService: DistProductListComponentService,
    private plpFilterService: ProductListFilterService,
    private plpViewService: ProductListViewService,
    private siteConfigService: SiteConfigService,
    private breakpointService: DistBreakpointService,
    private headerService: HeaderService,
  ) {
    this.isCategoriesVisible = true;
    this.pageType = this.siteConfigService.getCurrentPageTemplate();
  }

  get totalCount(): Observable<number> {
    return this.productListComponentService.pagination$.pipe(map((pagination) => pagination.totalResults));
  }

  get loading(): Observable<boolean> {
    return this.plpFilterService.loading$;
  }

  get categories$(): Observable<CategoryFilter[]> {
    return this.productListComponentService.categoryFilters$;
  }

  applyFilters(): void {
    this.plpFilterService.applyFilters();
  }
}
