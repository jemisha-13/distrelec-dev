import { Injectable } from '@angular/core';
import { SearchExperienceFactory } from '@features/pages/product/core/dynamic/search-experience.factory';
import { DistProductSearchAdapter } from '@features/pages/product/core/adapters/dist-product-search.adapter';
import { ProductSearchPage, SearchConfig, Suggestion } from '@spartacus/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ProductEnhancementsService } from '@features/pages/product/core/services/product-enhancements.service';

@Injectable()
export class DynamicProductSearchAdapter implements DistProductSearchAdapter {
  private adapter: DistProductSearchAdapter;

  constructor(
    private factory: SearchExperienceFactory,
    private productEnhancements: ProductEnhancementsService,
  ) {}

  search(query: string, searchConfig?: SearchConfig): Observable<ProductSearchPage> {
    if (!this.adapter) {
      this.createAdapter();
    }
    return this.adapter.search(query, searchConfig).pipe(
      tap((searchResult) => {
        if (searchResult?.products.length) {
          const productCodes = searchResult.products.map((product) => product.code);
          this.productEnhancements.load(productCodes);
        }
      }),
    );
  }

  getSearchResultCount(query: string, searchConfig?: SearchConfig): Observable<number> {
    if (!this.adapter) {
      this.createAdapter();
    }
    return this.adapter.getSearchResultCount(query, searchConfig);
  }

  loadSuggestions(term: string, pageSize?: number): Observable<Suggestion[]> {
    if (!this.adapter) {
      this.createAdapter();
    }
    return this.adapter.loadSuggestions(term, pageSize);
  }

  private createAdapter(): void {
    this.adapter = this.factory.createSearchAdapter();
  }
}
