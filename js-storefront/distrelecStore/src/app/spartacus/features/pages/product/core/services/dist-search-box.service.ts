import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { SearchSuggestionEvent } from '@features/tracking/events/search-suggestion-event';
import {
  createFrom,
  EventService,
  SearchboxService,
  SearchConfig,
  StateWithProduct,
  Suggestion,
} from '@spartacus/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DistSearchBoxService extends SearchboxService {
  private selectedCategory: string;

  constructor(
    protected store: Store<StateWithProduct>,
    private eventService: EventService,
  ) {
    super(store);
  }

  override searchSuggestions(query: string, searchConfig?: SearchConfig): void {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    this.eventService.dispatch(
      createFrom(SearchSuggestionEvent, { search_term: query, search_category: this.categoryCode }),
    );
    super.searchSuggestions(query, searchConfig);
  }

  //We cannot override the return type of base class method as types are different technically its now {} not []
  //We could write our own method for this but the effects and selectors are not exported
  //This is used as a wrapper to achieve correct return types
  getSuggestResults(): Observable<Suggestion> {
    return super.getSuggestionResults() as unknown as Observable<Suggestion>;
  }

  set categoryCode(categoryCode: string) {
    this.selectedCategory = categoryCode;
  }

  get categoryCode() {
    return this.selectedCategory ?? '';
  }
}
