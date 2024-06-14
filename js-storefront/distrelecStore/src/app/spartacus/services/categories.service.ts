import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseSiteService, Category, OccEndpointsService } from '@spartacus/core';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { catchError, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { CategoryPageData, DistCategoryIndexResponse, RelatedData } from '@model/category.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class CategoriesService {
  protected categoryData$: BehaviorSubject<CategoryPageData> = new BehaviorSubject<CategoryPageData>(null);
  protected relatedData$: BehaviorSubject<RelatedData[]> = new BehaviorSubject<RelatedData[]>(null);

  private cache: { [key: string]: Observable<CategoryPageData> } = {};

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private router: Router,
    private baseSiteService: BaseSiteService,
  ) {}

  getCategoryData(categoryCode?: string): Observable<CategoryPageData> {
    let searchCategoryCode = of(categoryCode);
    if (!categoryCode) {
      if (this.router.routerState.snapshot.root.queryParams.cmsTicketId) {
        searchCategoryCode = this.baseSiteService.get().pipe(
          map((baseSite) => {
            return baseSite.defaultPreviewCategoryCode;
          }),
        );
      }
    }
    return searchCategoryCode.pipe(
      switchMap((code) => {
        if (!this.cache[code]) {
          this.cache[code] = this.http.get<any>(this.getCategoryEndpoint(code, true)).pipe(
            tap((data) => this.setCurrentCategory(data)),
            catchError(() => {
              this.router.navigateByUrl('/not-found');
              return EMPTY;
            }),
            shareReplay({ bufferSize: 1, refCount: true }),
          );
        }

        const categoryData$ = this.cache[code];
        return categoryData$.pipe(tap((data) => this.setCurrentCategory(data)));
      }),
    );
  }

  getCategoryIndex(): Observable<Category[]> {
    return this.http
      .get<DistCategoryIndexResponse>(this.occEndpoints.buildUrl('/categories/index'))
      .pipe(map((data) => data.categories));
  }

  getCurrentCategoryData(): Observable<CategoryPageData> {
    return this.categoryData$.asObservable();
  }

  getRelatedData(): Observable<RelatedData[]> {
    return this.relatedData$.asObservable();
  }

  protected getCategoryEndpoint(categoryCode: string, fullFields?: boolean): string {
    return fullFields
      ? this.occEndpoints.buildUrl('/categorypage/' + categoryCode, { queryParams: { fields: 'FULL' } })
      : this.occEndpoints.buildUrl('/categorypage/' + categoryCode);
  }

  private setCurrentCategory(data: CategoryPageData) {
    this.categoryData$.next(data);
    this.relatedData$.next(data.sourceCategory.relatedData);
  }
}
