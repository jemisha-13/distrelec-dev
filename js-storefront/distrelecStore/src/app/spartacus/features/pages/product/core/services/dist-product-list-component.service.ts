/* eslint-disable @typescript-eslint/member-ordering */
import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import {
  ActivatedRouterStateSnapshot,
  CmsService,
  createFrom,
  CurrencyService,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  LanguageService,
  Page,
  PageType,
  PaginationModel,
  Product,
  ProductSearchPage,
  ProductSearchService,
  RouterState,
  RoutingService,
} from '@spartacus/core';
import { ProductListComponentService, ProductListRouteParams, SearchCriteria, ViewConfig } from '@spartacus/storefront';
import { combineLatest, Observable, of, ReplaySubject, using } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  first,
  map,
  shareReplay,
  switchMap,
  take,
  takeUntil,
  tap,
} from 'rxjs/operators';

import { PageHelper } from '@helpers/page-helper';
import { ProductSearchStatusService } from '@features/pages/product/core/services/product-search-status.service';
import { SearchQueryService } from './abstract-product-search-query.service';
import { CategoryFilter } from '../../../../../model';
import { decode } from '@helpers/encoding';
import { CategoryPageData } from '@model/category.model';
import { CategoriesService } from '@services/categories.service';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { EventHelper } from '@features/tracking/event-helper.service';
import { DistrelecUserService } from '@services/user.service';
import { SearchNavigationEvent } from '@features/tracking/events/search-navigation-event';
import { DistSearchBoxService } from './dist-search-box.service';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';

const PRODUCT_LIST_PAGE_TEMPLATES = [
  'ProductListPageTemplate',
  'ManufacturerStoreDetailPageTemplate',
  'ProductFamilyPageTemplate',
  'StorePageTemplate',
  'SearchResultsListPageTemplate',
];

@Injectable({
  providedIn: 'root',
})
export class DistProductListComponentService extends ProductListComponentService implements OnDestroy {
  private readonly destroyed$ = new ReplaySubject<boolean>(1);

  override readonly searchResults$: Observable<ProductSearchPage> = this.productSearchService
    .getResults()
    .pipe(filter((searchResult) => Object.keys(searchResult).length > 0));

  override readonly model$: Observable<ProductSearchPage> = using(
    () => this.searchByRouting$.subscribe(),
    () => this.searchResults$,
  ).pipe(
    tap((model) => {
      const redirected = this.redirectIfNeeded(model);
      if (!redirected) {
        if (this.pageHelper.isSearchPage()) {
          // Dispatch with force: true to create PageView event for search page
          this.eventService.dispatch(
            createFrom(SearchNavigationEvent, {
              context: { id: 'search', type: PageType.CONTENT_PAGE },
              params: {},
              semanticRoute: 'search',
              url: this.router.url,
              force: true,
            }),
          );
        }

        this.searchStatus.setLoading(false);
        this.notifyPunchedOutProducts(model);
      }
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  protected override searchByRouting$: Observable<ActivatedRouterStateSnapshot> = combineLatest([
    this.routing.getRouterState().pipe(distinctUntilChanged((x, y) => x.state.url === y.state.url)),
    ...this.distSiteContext,
    this.userService.getUserDetails(),
  ]).pipe(
    debounceTime(0),
    map(([routerState, ..._context]) => (routerState as RouterState).state),
    tap((state: ActivatedRouterStateSnapshot) => {
      const criteria = this.getCriteriaFromRoute(state.params, state.queryParams);
      this.search(criteria);
    }),
  );

  private get distSiteContext(): Observable<string>[] {
    return [
      this.languageService.getActive().pipe(distinctUntilChanged()),
      this.currencyService.getActive().pipe(distinctUntilChanged()),
      this.channelService.getActive().pipe(distinctUntilChanged()),
    ];
  }

  readonly isPlpActive$: Observable<boolean> = this.routing.getRouterState().pipe(
    filter((routerState: RouterState) => !routerState.nextState),
    distinctUntilChanged((prev, curr) => prev.state.url === curr.state.url),

    switchMap((routerState) =>
      this.cmsService.getCurrentPage().pipe(
        first(),
        map((page) => [routerState, page]),
      ),
    ),

    switchMap(([routerState, cmsPage]: [_: RouterState, _: Page]) => {
      const { id, type } = routerState.state.context;

      if (type === PageType.CATEGORY_PAGE) {
        return this.categoryService.getCategoryData(id).pipe(map((category) => [routerState, cmsPage, category]));
      }
      return of([routerState, cmsPage]);
    }),

    map(([routerState, cmsPage, categoryData]: [_: RouterState, _: Page, _?: CategoryPageData]) => {
      if (
        this.pageHelper.isSearchPage() ||
        (cmsPage?.type === PageType.CATEGORY_PAGE &&
          (!categoryData?.showCategoriesOnly || routerState.state.queryParams.useTechnicalView === 'true'))
      ) {
        this.dispatchEvents(cmsPage, categoryData);
      }

      if (cmsPage?.type === PageType.CATEGORY_PAGE) {
        return !categoryData.showCategoriesOnly || routerState.state.queryParams.useTechnicalView === 'true';
      }
      return PRODUCT_LIST_PAGE_TEMPLATES.includes(cmsPage.template);
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
    takeUntil(this.destroyed$),
  );

  readonly products$: Observable<Product[]> = this.searchResults$.pipe(map((model) => model.products));
  readonly categoryFilters$: Observable<CategoryFilter[]> = this.searchResults$.pipe(
    map((model) => model.categoryFilters),
  );
  readonly pagination$: Observable<PaginationModel> = this.searchResults$.pipe(map((model) => model.pagination));
  readonly title$: Observable<string> = this.searchResults$.pipe(
    map((data) => (data?.freeTextSearch === '*' ? '' : decode(data?.freeTextSearch ?? ''))),
  );

  readonly searchQuery$: ReplaySubject<string> = new ReplaySubject<string>(1);
  readonly isLoading$: Observable<boolean> = this.searchStatus.isLoading$;

  constructor(
    productSearchService: ProductSearchService,
    routing: RoutingService,
    activatedRoute: ActivatedRoute,
    currencyService: CurrencyService,
    languageService: LanguageService,
    router: Router,
    config: ViewConfig,
    private pageHelper: PageHelper,
    private messageService: GlobalMessageService,
    private searchStatus: ProductSearchStatusService,
    private searchQueryService: SearchQueryService,
    private cmsService: CmsService,
    private categoryService: CategoriesService,
    private channelService: ChannelService,
    private eventHelperService: EventHelper,
    private userService: DistrelecUserService,
    private eventService: EventService,
    private distSearchBoxService: DistSearchBoxService,
  ) {
    super(productSearchService, routing, activatedRoute, currencyService, languageService, router, config);
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  resetPage(): void {
    this.productSearchService.clearResults();
    this.searchStatus.setLoading(true);
  }

  protected override search(criteria: SearchCriteria): void {
    const { query, ...searchConfig } = criteria;

    this.searchStatus.setLoading(true);

    this.productSearchService.search(query, searchConfig);
  }

  protected override getCriteriaFromRoute(
    routeParams: ProductListRouteParams,
    queryParams: SearchCriteria | Params,
  ): SearchCriteria {
    const query = (queryParams as Params).q;
    if (query) {
      this.searchQuery$.next(decode(query));
    }
    return this.searchQueryService.buildSearchCriteria(queryParams);
  }

  protected override getQueryFromRouteParams({ query, categoryCode, brandCode }: ProductListRouteParams) {
    if (query) {
      return query;
    }

    if (categoryCode) {
      return categoryCode;
    }

    if (brandCode) {
      return brandCode;
    }
  }

  private notifyPunchedOutProducts(model: ProductSearchPage): void {
    if (model.punchedOutProducts?.length || model.punchedOut) {
      const punchedOutCodes: number[] = model.punchedOutProducts?.map((product: any) => product.code) || [];
      const errorMessageKey = model.punchedOutProducts?.length
        ? 'search.product.error.punchout'
        : 'search.product.no.result.error.punchout';

      this.messageService.add(
        {
          key: errorMessageKey,
          /* eslint-disable @typescript-eslint/naming-convention */
          params: { 0: punchedOutCodes.toString() },
        },
        GlobalMessageType.MSG_TYPE_ERROR,
      );
    }
  }

  private redirectIfNeeded(model: ProductSearchPage): boolean {
    const currentQuery = model.currentQuery?.query?.value ?? '';
    const shouldRedirectRules =
      !this.distSearchBoxService.categoryCode ||
      (this.distSearchBoxService.categoryCode && model.products.length === 0);

    if (model.keywordRedirectUrl && shouldRedirectRules) {
      const separator = model.keywordRedirectUrl.includes('?') ? '&' : '?';
      this.router.navigateByUrl(`${model.keywordRedirectUrl}${separator}redirectQuery=${currentQuery}`, {
        replaceUrl: true,
      });
      return true;
    }

    if (model.products?.length === 1 && !currentQuery.includes('filter_') && this.pageHelper.isSearchPage()) {
      const productUrl = model.products[0].url;
      const separator = productUrl.includes('?') ? '&' : '?';
      this.router.navigateByUrl(`${productUrl}${separator}redirectQuery=${currentQuery}`, { replaceUrl: true });
      return true;
    }

    if (this.pageHelper.isManufacturerDetailPage() && model.punchedOut) {
      this.router.navigate(['/'], { replaceUrl: true }).then(() => {
        this.messageService.add({ key: 'manufacturer.product.error.punchout' }, GlobalMessageType.MSG_TYPE_ERROR);
      });
      return true;
    }

    if (this.pageHelper.isManufacturerDetailPage() && !model.products) {
      this.router.navigate(['/manufacturer-stores/cms/manufacturer'], { replaceUrl: true });
      return true;
    }

    if (this.pageHelper.isProductFamilyPage() && (model.notFound === true || model.products?.length === 0)) {
      this.router.navigateByUrl('/not-found', { replaceUrl: true });
      return true;
    }

    if (
      this.pageHelper.isProductFamilyPage() &&
      model.notFound === false &&
      !model.products &&
      model.productFamilyCategoryCode
    ) {
      this.router.navigateByUrl('/c/' + model.productFamilyCategoryCode, { replaceUrl: true });
      return true;
    }

    return false;
  }

  private dispatchEvents(cmsPageData: Page, categoryData?: CategoryPageData): void {
    combineLatest([this.searchResults$, this.products$])
      .pipe(take(1))
      .subscribe(([results, products]) => {
        this.eventHelperService.trackBloomreachPlpEvent(results, products, cmsPageData, categoryData);

        this.eventService.dispatch(
          createFrom(ViewItemListEvent, {
            listType: this.pageHelper.getListPageEntityType(),
            products: products,
            totalResults: results.pagination.totalResults,
            page: cmsPageData,
          }),
        );
      });
  }
}
