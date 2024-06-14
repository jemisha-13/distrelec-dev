import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DistSearchBoxService } from '@features/pages/product/core/services/dist-search-box.service';
import {
  ConverterService,
  HttpParamsURIEncoder,
  OccEndpointsService,
  OccProductSearchAdapter,
  PRODUCT_SEARCH_PAGE_NORMALIZER,
  PRODUCT_SUGGESTION_NORMALIZER,
  ProductSearchPage,
  SearchConfig,
  Suggestion,
} from '@spartacus/core';
import { combineLatest, EMPTY, Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap, tap } from 'rxjs/operators';
import { FusionSuggestion } from 'src/app/spartacus/model/fusion-suggestions.model';
import { DistProductSearchAdapter } from '../../adapters/dist-product-search.adapter';
import { FusionSuggestionQueryService } from '../services/fusion-suggestion-query.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { FusionSiteContextParams } from '../model/site-context-params.model';
import { FusionConfig } from '../model/fusion-config.model';
import {
  FusionCustomerRestrictedData,
  FusionProductSearch,
  FusionSearchCriteria,
} from '@model/fusion-product-search.model';
import { SessionService } from '@features/pages/product/core/services/abstract-session.service';
import { SearchCriteria } from '@spartacus/storefront';
import { Router } from '@angular/router';

@Injectable()
export class DistFusionProductSearchAdapter extends OccProductSearchAdapter implements DistProductSearchAdapter {
  private fusionConfig$: Observable<FusionConfig> = this.allSiteSettingsService.getAllSiteSettings$().pipe(
    filter((siteSettings) => Object.keys(siteSettings?.fusionSearchExpose).length > 0),
    map((siteSettings) => siteSettings.fusionSearchExpose),
  );

  private buildSuggestionParams$: Observable<FusionSiteContextParams> =
    this.fusionSuggestionQueryService.typeaheadQueryParams();

  constructor(
    httpClient: HttpClient,
    occEndpoints: OccEndpointsService,
    converter: ConverterService,
    private distSearchBoxService: DistSearchBoxService,
    private fusionSuggestionQueryService: FusionSuggestionQueryService,
    private allSiteSettingsService: AllsitesettingsService,
    private sessionService: SessionService,
    private router: Router,
  ) {
    super(httpClient, occEndpoints, converter);
  }

  getSearchResultCount(query: string, searchConfig?: SearchConfig): Observable<number> {
    return this.fusionConfig$
      .pipe(
        switchMap((config) =>
          this.callFusionSearch(config, { ...searchConfig, rows: 0 } as FusionSearchCriteria, query),
        ),
      )
      .pipe(map((res) => res.pagination.totalResults));
  }

  override loadSuggestions(query: string): Observable<any> {
    return combineLatest([this.buildSuggestionParams$, this.fusionConfig$]).pipe(
      switchMap(([params, config]) => this.callFusionTypeAhead(config, params, query)),
    ) as Observable<Suggestion>;
  }

  override search(query: string, searchConfig?: SearchConfig): Observable<any> {
    return this.fusionConfig$.pipe(
      switchMap((config) => this.callFusionSearch(config, searchConfig, query)),
    ) as Observable<ProductSearchPage>;
  }

  private callFusionCustomerRestricted(params): Observable<FusionCustomerRestrictedData> {
    return this.fusionConfig$.pipe(
      switchMap((config) =>
        this.http
          .get(this.createFusionURL(config, '/is-customer-restricted'), {
            headers: this.setHeadersFusionApi(config),
            params: { ...params },
          })
          .pipe(
            catchError(() => {
              console.warn('Error fetching customer restricted data');
              return of({});
            }),
          ),
      ),
    );
  }

  private callFusionTypeAhead(config: FusionConfig, params: any, query: string): Observable<Suggestion> {
    const headers = this.setHeadersFusionApi(config);

    return this.http
      .get<FusionSuggestion>(this.createFusionURL(config, '/typeahead'), {
        headers,
        params: { q: query, ...params },
      })
      .pipe(
        tap(this.setSessionId),
        this.converter.pipeable(PRODUCT_SUGGESTION_NORMALIZER),
        catchError(() => []),
      );
  }

  private callFusionSearch(
    config: FusionConfig,
    searchConfig: SearchCriteria,
    query: string,
  ): Observable<ProductSearchPage> {
    const headers = this.setHeadersFusionApi(config);
    const params: Partial<FusionSearchCriteria> = { q: query, ...searchConfig };

    let formattedParams = new HttpParams({ encoder: new HttpParamsURIEncoder() });

    Object.entries(params).forEach(([paramKey, paramValues]) => {
      if (Array.isArray(paramValues)) {
        paramValues.forEach((value) => (formattedParams = formattedParams.append(paramKey, value)));
      } else {
        formattedParams = formattedParams.append(paramKey, paramValues);
      }
    });

    return this.http
      .get<FusionProductSearch>(this.createFusionURL(config, '/search'), {
        headers,
        params: formattedParams,
      })
      .pipe(
        tap(this.setSessionId),
        switchMap((fusionData: FusionProductSearch) => {
          if (!this.shouldFetchCustomerRestricted(fusionData)) {
            return of(fusionData);
          }

          return this.callFusionCustomerRestricted({ ...params }).pipe(
            map((customerRestricted) => ({ ...fusionData, customerRestricted })),
          );
        }),
        map((res) => ({ ...res, request: params })),
        this.converter.pipeable(PRODUCT_SEARCH_PAGE_NORMALIZER),
        catchError((error) => {
          if (error.status !== 0) {
            // Cancelled requests can be triggered when switching channel and the page is refreshing. We don't want to show an error in this case.
            this.router.navigateByUrl('/error');
          }
          return EMPTY;
        }),
      );
  }

  private shouldFetchCustomerRestricted(fusionData: FusionProductSearch): boolean {
    return fusionData?.response?.docs?.length === 0;
  }

  private createFusionURL(config: FusionConfig, endpoint: string): string {
    let fusionUrl = config.fusionBaseUrl + endpoint;
    if (config.fusionProfileSuffix) {
      fusionUrl += '_' + config.fusionProfileSuffix;
    }
    return fusionUrl;
  }

  private setHeadersFusionApi(config: FusionConfig): HttpHeaders {
    return new HttpHeaders({
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'x-api-key': config.fusionSearchAPIKey,
    });
  }

  private setSessionId = (res) => {
    const queryId = res.response?.fusionQueryId;
    if (queryId) {
      this.sessionService.setSessionId(queryId);
    }
  };
}
