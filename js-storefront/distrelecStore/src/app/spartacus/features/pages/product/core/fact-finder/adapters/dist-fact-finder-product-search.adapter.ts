import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFactFinderResults } from '@model/factfinder.model';
import { FactFinderService } from '@features/pages/product/core/fact-finder/services/fact-finder.service';
import {
  ConverterService,
  OccEndpointsService,
  OccProductSearchAdapter,
  PRODUCT_SUGGESTION_NORMALIZER,
  ProductSearchPage,
  SearchConfig,
  Suggestion,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { DistProductSearchAdapter } from '@features/pages/product/core/adapters/dist-product-search.adapter';
import { DistSearchBoxService } from '../../services/dist-search-box.service';

@Injectable()
export class DistFactFinderProductSearchAdapter extends OccProductSearchAdapter implements DistProductSearchAdapter {
  constructor(
    httpClient: HttpClient,
    occEndpoints: OccEndpointsService,
    converter: ConverterService,
    private factFinderService: FactFinderService,
    private distSearchBoxService: DistSearchBoxService,
  ) {
    super(httpClient, occEndpoints, converter);
  }

  override search(query: string, searchConfig?: SearchConfig): Observable<ProductSearchPage> {
    return this.factFinderService
      .getSessionId()
      .pipe(switchMap((sessionId) => super.search(query, this.addSid(searchConfig, sessionId))));
  }

  getSearchResultCount(query: string, searchConfig?: SearchConfig): Observable<number> {
    const endpoint = this.occEndpoints.buildUrl('/products/search');

    return this.factFinderService.getSessionId().pipe(
      switchMap((sessionId) =>
        this.http.head<void>(endpoint, {
          observe: 'response' as 'body', // So we can get the headers from the response
          params: { query, ...this.addSid(searchConfig, sessionId) },
        }),
      ),
      // response is of type HttpResponse<void> but types are broken due to override `observe` option above
      map((response: any) => {
        const countHeader = response.headers.get('x-total-count');
        if (!countHeader) {
          console.warn('No X-Total-Count header found in response');
        }
        return +countHeader || 0;
      }),
    );
  }

  //We cannot override the return type of base class method as types are different technically its now {} not []
  //Should we change normalizer to return an array to get around the type mismatch ?
  override loadSuggestions(query: string): Observable<any> {
    return this.resolveSuggestionEndpoint(query).pipe(
      switchMap((url) => this.http.get<IFactFinderResults>(url)),
      this.converter.pipeable<IFactFinderResults, Suggestion>(PRODUCT_SUGGESTION_NORMALIZER),
    ) as Observable<Suggestion>;
  }

  protected resolveSuggestionEndpoint(query: string): Observable<string> {
    const categoryCode = this.distSearchBoxService.categoryCode ?? '';
    const formattedQuery = this.factFinderService.getFormattedTerm(query);

    return combineLatest([
      this.factFinderService.getSessionId(),
      this.factFinderService.getFactFinderEnvironment(),
    ]).pipe(
      map(
        ([sessionId, env]) =>
          `${env.ffsuggestUrl}?query=${formattedQuery}&filtercategoryCodePathROOT=${categoryCode}&channel=${env.ffsearchChannel}&queryFromSuggest=true&userInput=${query}&format=json&sid=${sessionId}`,
      ),
    );
  }

  private addSid(config: SearchConfig, sessionId: string): SearchConfig {
    return {
      ...config,
      sid: config.sid ?? sessionId,
    };
  }
}
