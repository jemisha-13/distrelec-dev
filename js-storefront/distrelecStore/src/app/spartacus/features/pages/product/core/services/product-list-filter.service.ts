import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, merge, Observable } from 'rxjs';
import { distinctUntilChanged, filter, first, map, pairwise, shareReplay, startWith, take, tap } from 'rxjs/operators';
import { PageHelper } from '@helpers/page-helper';
import { Params, Router } from '@angular/router';
import { pickBy } from 'lodash-es';
import { Facet, FacetValue, RoutingService } from '@spartacus/core';
import { DistProductSearchService } from '@features/pages/product/core/services/dist-product-search.service';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { SearchQueryService } from '@features/pages/product/core/services/abstract-product-search-query.service';
import { SearchExperienceService } from './search-experience.service';
import { FILTER_RANGE_PARAM_SUFFIX, FILTER_RANGE_SEPARATOR } from '@helpers/constants';

type FacetItem = { name: string; count: number; queryFilter: string };
type FilterParams = { [key: string]: Params };
export type AppliedFilter = { name: string; code: string; count: number; facetItems: FacetItem[] };

@Injectable({
  providedIn: 'root',
})
export class ProductListFilterService {
  // Last emitted filters from filters$ used by applyFilters and onFilterChange callbacks
  private filters: Facet[];

  /* eslint-disable @typescript-eslint/member-ordering */
  showApplyFilters: Observable<boolean>;
  haveFiltersChanged: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  mappedFacets$: Observable<Facet[]> = this.productListService.searchResults$.pipe(
    tap(() => this.selectedFilterResultCount$.next(null)),
    filter((results) => !!results?.facets),
    map((results) =>
      results.facets.map((facet) => ({
        ...facet,
        open: false,
        // Map `selected` to `checked` because `selected` is read-only and can't be changed by the UI
        values: facet.values.map((value) => ({ ...value, checked: value.selected })),
      })),
    ),
  );

  filters$: Observable<Facet[]> = this.mappedFacets$.pipe(
    map((facets) => this.filterOutCategoryFacet(facets)),
    tap((facets) => (this.filters = facets)),
    shareReplay(1),
  );

  selectedFilters$: BehaviorSubject<AppliedFilter[]> = new BehaviorSubject<AppliedFilter[]>([]);

  selectedFilterResultCount$: BehaviorSubject<number> = new BehaviorSubject<number | null>(null);

  isResultCountLoading$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  activeFilteredCount$: Observable<number> = merge(
    this.productListService.pagination$.pipe(map((pagination) => pagination.totalResults)),
    this.selectedFilterResultCount$.pipe(filter((selectedFilterCount) => selectedFilterCount !== null)),
  );

  appliedFilters$: Observable<AppliedFilter[]> = this.mappedFacets$.pipe(
    map((facets) =>
      facets
        .filter((facet) => !this.isPermanentFacetCode(facet.code))
        .map((facet) => this.mapFacetToAppliedFilter(facet)),
    ),
  );

  filtersAsParams$: Observable<Params> = this.filters$.pipe(
    map((facets) => this.convertSelectedFacetsToParams(facets)),
  );

  routeQueryParams$: Observable<Params> = this.routing.getRouterState().pipe(
    distinctUntilChanged((x, y) => x.state.url === y.state.url),
    map((routerState) => routerState.state.queryParams),
  );

  routeQueryParamsWithoutFilters$: Observable<Params> = this.routeQueryParams$.pipe(
    map((queryParams) =>
      Object.keys(queryParams).reduce((acc, key) => {
        if (!key.includes('filter_')) {
          acc[key] = queryParams[key];
        }
        return acc;
      }, {}),
    ),
  );

  routeQueryParamsPermanentFilters$: Observable<Params> = this.routeQueryParams$.pipe(
    map((routeQueryParams) => pickBy(routeQueryParams, (value, key) => this.isPermanentFacetCode(key))),
  );

  selectedCategoryQueryParam$: Observable<Params> = this.routeQueryParams$.pipe(
    map((routeQueryParams) => pickBy(routeQueryParams, (value, key) => key.includes(this.CATEGORY_FILTER_KEY))),
  );

  readonly CATEGORY_FILTER_KEY = this.searchExperience.getCategoryKey();
  readonly MANUFACTURER_FILTER_KEY = 'manufacturerCode';
  readonly PRODUCT_FAMILY_FILTER_KEY = 'productFamilyCode';
  /* eslint-enable @typescript-eslint/member-ordering */

  constructor(
    private pageHelper: PageHelper,
    private router: Router,
    private routing: RoutingService,
    private productListService: DistProductListComponentService,
    private productSearchService: DistProductSearchService,
    private searchQueryService: SearchQueryService,
    private searchExperience: SearchExperienceService,
  ) {
    this.showApplyFilters = combineLatest([
      this.countFiltersSelected.pipe(startWith([0, 0])),
      this.loading$.pipe(startWith(false)),
      this.isFilterApplied(),
    ]).pipe(
      map(([countPair, isLoading, isFilterApplied]) => {
        if (isFilterApplied) {
          return true;
        }
        if (countPair[1] === 0) {
          return false;
        }
        if (isLoading) {
          return countPair[0] >= 1;
        }
        return true;
      }),
    );
  }

  get loading$(): Observable<boolean> {
    return this.isResultCountLoading$.asObservable();
  }

  get countFiltersSelected(): Observable<[number, number]> {
    return this.selectedFilters$
      .pipe(
        map((groups) => groups?.reduceRight((acc, curr) => acc + curr.count, 0)),
        map((count) => count ?? 0),
      )
      .pipe(pairwise());
  }

  setSelectedFilters(facets: Facet[] = []): void {
    const selectedFilters = facets.map((facet) => this.mapFacetToAppliedFilter(facet));

    this.selectedFilters$.next(selectedFilters);
  }

  applyFilters(): void {
    this.haveFiltersChanged.next(false);

    const filtersAsParams = this.convertSelectedFacetsToParams(this.filters);

    combineLatest([
      this.routeQueryParamsWithoutFilters$,
      this.routeQueryParamsPermanentFilters$,
      this.selectedCategoryQueryParam$,
    ])
      .pipe(
        take(1),
        tap(([routeQueryParams, routeQueryParamsPermanentFilters, selectedCategoryQueryParam]) => {
          const { currentPage, ...otherQueryParams } = routeQueryParams;
          const queryParams = {
            ...otherQueryParams,
            ...filtersAsParams,
            ...routeQueryParamsPermanentFilters,
            ...selectedCategoryQueryParam,
          };

          return this.router.navigate([], {
            relativeTo: this.router.routerState.root,
            queryParams,
          });
        }),
      )
      .subscribe();
  }

  removeFilter(removedParam) {
    combineLatest([
      this.selectedCategoryQueryParam$,
      this.routeQueryParamsWithoutFilters$,
      this.routeQueryParamsPermanentFilters$,
      this.appliedFilters$.pipe(map((filters) => filters.filter((group) => group.count > 0))),
    ])
      .pipe(
        take(1),
        tap(
          ([
            selectedCategoryQueryParam,
            routeQueryParamsWithoutFilters,
            routeQueryParamsPermanentFilters,
            appliedFilters,
          ]) => {
            const queryFilters = appliedFilters.flatMap((facet) => facet.facetItems.map((item) => item.queryFilter));
            const groupedAppliedFilterValues = this.groupFilterValuesByFilterName(queryFilters);
            const filterName = removedParam.split('=')[0];

            const currentFilters: FilterParams = {
              ...groupedAppliedFilterValues,
              ...selectedCategoryQueryParam,
            };

            if (removedParam.includes(this.CATEGORY_FILTER_KEY)) {
              this.deleteCurrentFiltersByGroup(currentFilters, this.CATEGORY_FILTER_KEY);
            }

            if (removedParam.includes(FILTER_RANGE_PARAM_SUFFIX)) {
              this.deleteCurrentFiltersByGroup(currentFilters, filterName);
            }

            removedParam.split('&').forEach((param) => {
              const [key, value] = param.split('=');

              if (Array.isArray(currentFilters[key])) {
                const findParamValue: number = currentFilters[key].indexOf(value);
                if (findParamValue > -1) {
                  currentFilters[key].splice(findParamValue, 1);

                  if (currentFilters[key].length === 0) {
                    delete currentFilters[key];
                  }
                }
              }
            });

            const queryParams = {
              ...routeQueryParamsWithoutFilters,
              ...routeQueryParamsPermanentFilters,
              ...currentFilters,
              currentPage: 1,
            };

            this.router.navigate([], {
              relativeTo: this.router.routerState.root,
              queryParams,
            });
          },
        ),
      )
      .subscribe();
  }

  clearFilters() {
    this.haveFiltersChanged.next(false);
    this.routeQueryParamsWithoutFilters$
      .pipe(
        take(1),
        tap((queryParams) => {
          this.router.navigate([], {
            relativeTo: this.router.routerState.root,
            queryParams,
          });
        }),
      )
      .subscribe();
  }

  onFilterChange() {
    this.haveFiltersChanged.next(true);
    this.setSelectedFilters(this.filters);
    const filtersAsParams = this.convertSelectedFacetsToParams(this.filters);

    combineLatest([
      this.routeQueryParamsWithoutFilters$,
      this.routeQueryParamsPermanentFilters$,
      this.selectedCategoryQueryParam$,
    ])
      .pipe(
        take(1),
        tap(([routeQueryParams, routeQueryParamsPermanentFilters, selectedCategoryQueryParam]) => {
          const queryParams = {
            ...routeQueryParams,
            ...filtersAsParams,
            ...routeQueryParamsPermanentFilters,
            ...selectedCategoryQueryParam,
          };

          this.updateSelectedFilterResultCount(queryParams);
        }),
      )
      .subscribe();
  }

  private updateSelectedFilterResultCount(queryParams): void {
    const { query, ...rest } = this.searchQueryService.buildSearchCriteria(queryParams);

    this.isResultCountLoading$.next(true);
    this.productSearchService
      .getSearchResultCount(query, rest)
      .pipe(
        first(),
        tap((count) => {
          this.selectedFilterResultCount$.next(count);
          this.isResultCountLoading$.next(false);
        }),
      )
      .subscribe();
  }

  private deleteCurrentFiltersByGroup(currentFilters: FilterParams, selector: string): void {
    Object.entries(currentFilters)
      .filter(([key]) => key.includes(selector))
      .map(([key]) => key)
      .forEach((key) => delete currentFilters[key]);
  }

  private isPermanentFacetCode(code: string): boolean {
    return code.includes(this.MANUFACTURER_FILTER_KEY) || code.includes(this.PRODUCT_FAMILY_FILTER_KEY);
  }

  private groupFilterValuesByFilterName(queryFilters: string[]): Record<string, string[]> {
    return queryFilters.reduce((acc, queryFilter) => {
      const keyValuePairs = queryFilter.split('&');

      keyValuePairs.forEach((pair) => {
        const [key, value] = pair.split('=');

        if (acc[key]) {
          acc[key].push(value);
        } else {
          acc[key] = [value];
        }
      });

      return acc;
    }, {});
  }

  private mapFacetToAppliedFilter(facet: Facet): AppliedFilter {
    let activeFilters = this.populateActiveFilters(facet);
    let selectedCount = facet.values.filter((value) => value.checked).length;

    if (facet.code.includes(this.CATEGORY_FILTER_KEY)) {
      activeFilters = activeFilters
        .map((facetItem) => ({
          ...facetItem,
          name: facet.name,
          queryFilter: facet.code,
        }))
        .filter((val, index) => index === 0);
    }

    if (this.pageHelper.isManufacturerDetailPage() && facet.code === 'Manufacturer') {
      // One facet is selected by default, but we shouldn't count it.
      // If we clear all manufacturer filters, the default will be applied again from the code in the URL.
      selectedCount -= 1;
    }

    return {
      name: facet.name,
      code: facet.code,
      count: selectedCount,
      facetItems: activeFilters,
    };
  }

  private populateActiveFilters(facet: Facet): FacetItem[] {
    const ranges = [];
    const exact = [];
    const currentRange = [];

    for (const item of facet.values) {
      if (item.checked) {
        currentRange.push(item);
      } else {
        this.processCurrentRange(currentRange, ranges, exact, facet);
      }
    }

    this.processCurrentRange(currentRange, ranges, exact, facet);
    const rangeItems = this.formatRange(ranges);

    return [...rangeItems, ...exact];
  }

  private processCurrentRange(currentRange: FacetItem[], ranges: FacetItem[][], exact: FacetItem[], facet: Facet) {
    if (currentRange.length > 0) {
      const canApplyRange = currentRange.length > 3 && facet.hasMinMaxFilters;

      if (canApplyRange) {
        ranges.push([...currentRange]);
      } else {
        exact.push(...currentRange);
      }

      currentRange.length = 0;
    }
  }

  private formatRange(ranges: FacetItem[][]): FacetItem[] {
    return ranges.map((range) => {
      const name = `${range[0].name} ... ${range[range.length - 1].name}`;
      const count = range.reduce((total, item) => total + item.count, 0);
      const queryFilter = range.map((item) => item.queryFilter).join('&');

      return { name, count, queryFilter };
    });
  }

  private filterOutCategoryFacet(facets: Facet[]) {
    return facets?.filter(({ code }) => !code.includes(this.CATEGORY_FILTER_KEY)) ?? [];
  }

  private isFilterApplied(): Observable<boolean> {
    return this.appliedFilters$.pipe(map((filters) => filters.some((appliedFilter) => appliedFilter.count > 0)));
  }

  private convertSelectedFacetsToParams(facets: Facet[]): Params {
    const filters: Params = {};

    facets.forEach((facet) => {
      if (this.searchExperience.isRangeQuerySupported() && facet.minValue && facet.maxValue) {
        filters[`${facet.code}${FILTER_RANGE_PARAM_SUFFIX}`] =
          `${facet.minValue}${FILTER_RANGE_SEPARATOR}${facet.maxValue}`;
      } else {
        const selected = facet.values
          .filter((facetValue: FacetValue) => facetValue.checked)
          .map((facetValue) => facetValue.queryFilter.split('='));

        selected.forEach(([key, value]) => (filters[key] ? filters[key].push(value) : (filters[key] = [value])));
      }
    });

    return filters;
  }
}
