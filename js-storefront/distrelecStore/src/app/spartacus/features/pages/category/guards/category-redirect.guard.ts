import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { CategoriesService } from '@services/categories.service';
import { map, startWith } from 'rxjs/operators';
import { ProductSearchService } from '@spartacus/core';
import { RedirectCountService } from '@services/redirect-count.service';

@Injectable({
  providedIn: 'root',
})
export class CategoryRedirectGuard {
  constructor(
    private categoryService: CategoriesService,
    private router: Router,
    private productSearchService: ProductSearchService,
    private redirectCount: RedirectCountService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    // Allow extra redirect in case content page redirects to category code URL
    if (this.redirectCount.exceeds(2)) {
      this.redirectCount.reset();
      return true;
    }

    const categoryCode = route.params.categoryCode;

    return combineLatest([
      this.productSearchService.getResults().pipe(startWith(null)),
      this.categoryService.getCategoryData(categoryCode),
    ]).pipe(
      map(([searchResults, categoryData]) => {
        const categorySeoUrl = categoryData.sourceCategory.url;
        const hasKeyWordRedirectFromSearch = !!searchResults?.keywordRedirectUrl;

        if (!state.url.includes(categorySeoUrl)) {
          this.router.navigate([categorySeoUrl], {
            queryParams: route.queryParams,
            replaceUrl: hasKeyWordRedirectFromSearch,
          });
          this.redirectCount.increment();
          return false;
        }

        this.redirectCount.reset();
        return true;
      }),
    );
  }
}
