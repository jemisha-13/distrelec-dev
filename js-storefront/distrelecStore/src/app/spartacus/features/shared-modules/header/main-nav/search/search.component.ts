import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { merge, Observable, of, Subject, Subscription } from 'rxjs';
import { faCaretDown } from '@fortawesome/free-solid-svg-icons';
import { TopCategoriesService } from 'src/app/spartacus/services/top-categories.service';
import { Router } from '@angular/router';
import {
  Category,
  CmsService,
  createFrom,
  EventService,
  OccEndpointsService,
  Page,
  Suggestion,
  TranslationService,
  WindowRef,
} from '@spartacus/core';
import { debounceTime, filter, first, map, switchMap, take, tap } from 'rxjs/operators';
import { SiteConfigService } from '@services/siteConfig.service';
import { BannerClickEvent } from '@features/shared-modules/banners/simple-banner/banner-click-event';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { BackDropModuleService } from '@features/shared-modules/backdrop-module/backdrop-module.service';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { SearchExperienceService } from '@features/pages/product/core/services/search-experience.service';
import { ViewItemListEvent } from '@features/tracking/events/view-item-list-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { SearchEvent } from '@features/tracking/events/search-event';
import { SearchQuery } from '@model/product-search.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss'],
})
export class SearchComponent implements OnInit, OnDestroy {
  static count = 0;

  @ViewChild('searchForm') searchFormRef;
  faCaretDown = faCaretDown;
  showEmptySearchPlaceholder = false;
  selectedCategoryName = '';
  searchCategories$: Observable<Category[]> = this.topCategoriesService
    .getCategories()
    .pipe(map((item) => item?.searchbarCategories));
  schema?;
  baseUrl: string;

  pageType = this.siteConfigService.getCurrentPageTemplate();
  logoComponentUid: string;
  isSearchExecuted = false;

  inputSearchHtmlId: string;
  selectSearchHtmlId: string;
  buttonSearchHtmlId: string;

  sessionId: string | null;

  private subscriptions: Subscription = new Subscription();
  private modelChanged = new Subject<string>();
  private isSearchResultsOpen = false;

  constructor(
    private topCategoriesService: TopCategoriesService,
    private changeDetection: ChangeDetectorRef,
    private router: Router,
    private occEndpoints: OccEndpointsService,
    private cmsService: CmsService,
    private eventService: EventService,
    private siteConfigService: SiteConfigService,
    private slideDrawerService: SlideDrawerService,
    private backDropModuleService: BackDropModuleService,
    private winRef: WindowRef,
    private translationService: TranslationService,
    private distSearchBoxService: DistSearchBoxService,
    private sessionService: SessionService,
    private experienceService: SearchExperienceService,
  ) {
    this.baseUrl = this.occEndpoints.getBaseUrl({ baseUrl: true, baseSite: false, prefix: false });
    this.logoComponentUid = 'LogoComponent';
    this.inputSearchHtmlId = `metahd-search-${SearchComponent.count}`;
    this.selectSearchHtmlId = `header_search_select_${SearchComponent.count}`;
    this.buttonSearchHtmlId = `header_search_button_${SearchComponent.count}`;
    SearchComponent.count++;
  }

  get selectedCategoryCode(): string {
    return this.distSearchBoxService.categoryCode;
  }

  set selectedCategoryCode(code: string) {
    this.distSearchBoxService.categoryCode = code;
  }

  ngOnInit(): void {
    this.searchCategoryPageSubscriptions();

    this.subscriptions.add(this.sessionService.getSessionId().subscribe((sid) => (this.sessionId = sid)));

    this.resetSubscriptions();
    this.getDefaultCategoryTranslation();

    this.subscriptions.add(
      merge(
        this.backDropModuleService.overlayClickSignal_.pipe(filter((val) => val === true)),
        this.slideDrawerService.hasEscapedSearch$.pipe(filter((val) => val === true)),
        this.slideDrawerService.onRouteChange$,
      ).subscribe(() => {
        this.searchFormRef.value.query = '';
        this.isSearchResultsOpen = false;
        this.changeDetection.detectChanges();
        this.distSearchBoxService.clearResults();
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  searchCategoryPageSubscriptions() {
    this.subscriptions.add(
      this.modelChanged
        .pipe(
          debounceTime(300),
          switchMap((searchTerm: string) => {
            if (searchTerm.length >= 3) {
              this.distSearchBoxService.searchSuggestions(searchTerm, {});
              this.isSearchExecuted = true;
              return this.distSearchBoxService.getSuggestResults().pipe(
                filter((results) => Object.keys(results).length > 0),
                take(1),
                tap((data) => {
                  if (data.products.length) {
                    this.eventService.dispatch(
                      createFrom(ViewItemListEvent, {
                        products: data.products,
                        listType: ItemListEntity.SUGGESTED_SEARCH,
                      }),
                    );
                  }
                }),
              );
            }

            return of(null);
          }),
        )
        .subscribe((res: Suggestion | null) => {
          if (!res || !res.products?.length) {
            this.winRef.document.body.classList.remove('is-locked');
            this.slideDrawerService.closePanel();
            this.isSearchResultsOpen = false;
            this.distSearchBoxService.clearResults();
          } else {
            if (!this.isSearchResultsOpen) {
              this.slideDrawerService.openPanel(new Event('default'), 'search_results', 'FROM_SEARCH');
              this.isSearchResultsOpen = true;
            }
          }
        }),
    );
  }

  resetCategoryValues() {
    this.selectedCategoryCode = '';
    this.getDefaultCategoryTranslation();
  }

  resetSubscriptions() {
    const pageChange$ = this.cmsService
      .getCurrentPage()
      .pipe(filter<Page>((event) => event && event.template !== 'SearchResultsListPageTemplate'));

    const logoClick$ = this.eventService
      .get(BannerClickEvent)
      .pipe(filter((event) => event.componentUID === this.logoComponentUid));

    this.subscriptions.add(merge(pageChange$, logoClick$).subscribe(() => this.resetCategoryValues()));
  }

  onSubmit(submitValue: SearchQuery, isFromAutosuggest?: boolean): SearchQuery {
    if (this.searchFormRef.value.query) {
      this.setSearchSchema(submitValue.query);

      const query: string = this.searchFormRef.value.query.trim();
      const category = this.selectedCategoryCode;
      // eslint-disable-next-line @typescript-eslint/naming-convention
      this.eventService.dispatch(createFrom(SearchEvent, { search_term: query, search_category: category }));

      this.router
        .navigate(['/search'], {
          queryParams: Object.assign(
            {},
            { q: query },
            this.sessionId && { sid: this.sessionId },
            category && { [this.experienceService.getCategoryParam()]: category },
          ),
        })
        .then(() => {
          this.searchFormRef.value.query = '';
          this.modelChanged.next('');
        });

      this.showEmptySearchPlaceholder = false;
      return submitValue;
    } else {
      this.showEmptySearchPlaceholder = true;
    }
    this.changeDetection.detectChanges();
  }

  onModelChange(searchTerm: string): void {
    this.distSearchBoxService.clearResults();
    this.modelChanged.next(searchTerm);
  }

  onSelectChange(selectEvent): void {
    this.selectedCategoryCode = selectEvent.target.value;
    this.selectedCategoryName = selectEvent.target.options[selectEvent.target.selectedIndex].title;
  }

  private setSearchSchema(submitValue: string): void {
    this.schema = {
      // eslint-disable-next-line @typescript-eslint/naming-convention
      '@context': 'http://schema.org',
      // eslint-disable-next-line @typescript-eslint/naming-convention
      '@type': 'WebSite',
      url: this.baseUrl,
      potentialAction: {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        '@type': 'SearchAction',
        target: `${this.baseUrl}/search?q=${submitValue}`,
        queryInput: `required name=${submitValue}`,
      },
    };
  }

  private getDefaultCategoryTranslation(): void {
    this.translationService
      .translate('form.search_all')
      .pipe(first())
      .subscribe((value: string) => (this.selectedCategoryName = value));
  }
}
