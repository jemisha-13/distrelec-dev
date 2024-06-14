import { HttpClient } from '@angular/common/http';
import { Injectable, makeStateKey, TransferState } from '@angular/core';

import { OccEndpointsService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { shareReplay, tap } from 'rxjs/operators';
import { TopCategories } from '@model/category.model';

@Injectable({
  providedIn: 'root',
})
export class TopCategoriesService {
  protected topCategoriesData$: BehaviorSubject<TopCategories> = new BehaviorSubject<TopCategories>(null);
  private TOPCATS_KEY = makeStateKey<TopCategories>('top-cats');

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private winRef: WindowRef,
    private transferState: TransferState,
  ) {
    if (this.transferState.hasKey(this.TOPCATS_KEY)) {
      this.topCategoriesData$.next(this.transferState.get<TopCategories>(this.TOPCATS_KEY, null));
    }
  }

  getCategories(): Observable<TopCategories> {
    if (this.topCategoriesData$.value) {
      return this.topCategoriesData$.asObservable();
    }

    return this.http.get<TopCategories>(this.occEndpoints.buildUrl('/topcats')).pipe(
      tap((data) => {
        this.topCategoriesData$.next(data);
        if (!this.winRef.isBrowser()) {
          this.transferState.set<TopCategories>(this.TOPCATS_KEY, data);
        }
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  getCurrentTopCategoriesData(): Observable<TopCategories> {
    return this.topCategoriesData$.asObservable();
  }
}
