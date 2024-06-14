import { ProductSearchAdapter, SearchConfig } from '@spartacus/core';
import { Observable } from 'rxjs';

export abstract class DistProductSearchAdapter extends ProductSearchAdapter {
  abstract getSearchResultCount(query: string, searchConfig?: SearchConfig): Observable<number>;
}
