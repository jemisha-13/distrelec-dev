import { Injectable } from '@angular/core';
import { ProductSearchAdapter, ProductSearchService, SearchConfig, StateWithProduct } from '@spartacus/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { DistProductSearchAdapter } from '@features/pages/product/core/adapters/dist-product-search.adapter';

@Injectable({
  providedIn: 'root',
})
export class DistProductSearchService extends ProductSearchService {
  constructor(
    protected store: Store<StateWithProduct>,
    protected searchAdapter: ProductSearchAdapter,
  ) {
    super(store);
  }

  getSearchResultCount(query: string, searchConfig?: SearchConfig): Observable<number> {
    // Use the adapter directly to avoid creating the state structure.
    // Count doesn't need to be transferred from SSR as the full model has count on the pagination object
    const adapter = this.searchAdapter as DistProductSearchAdapter;
    return adapter.getSearchResultCount(query, searchConfig);
  }
}
