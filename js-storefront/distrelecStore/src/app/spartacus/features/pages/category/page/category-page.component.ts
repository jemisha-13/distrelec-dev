import { Component, OnDestroy, OnInit } from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { ActivatedRoute, Router, Scroll } from '@angular/router';
import { CmsService, WindowRef } from '@spartacus/core';
import { PageLayoutService } from '@spartacus/storefront';
import { Observable, ReplaySubject } from 'rxjs';
import { filter, map, mapTo, switchMap, take, takeUntil } from 'rxjs/operators';

import { CategoryPageData } from '@model/category.model';
import { CategoriesService } from '@services/categories.service';
import { SessionStorageService } from '@services/session-storage.service';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';

const VISITED_CATEGORY_SESSION_STORAGE_KEY = 'Dist_visitedCategories';

@Component({
  selector: 'app-category-page',
  templateUrl: './category-page.component.html',
  styleUrls: [],
})
export class CategoryPageComponent implements OnInit, OnDestroy {
  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();

  isProductListView$ = this.plpService.isPlpActive$;

  categoryData$: Observable<CategoryPageData> = this.activatedRoute.params.pipe(
    switchMap((params) => this.categoriesService.getCategoryData(params.categoryCode)),
    filter<CategoryPageData>(Boolean),
  );

  private destroyed$ = new ReplaySubject<void>();

  constructor(
    protected pageLayoutService: PageLayoutService,
    private plpService: DistProductListComponentService,
    private activatedRoute: ActivatedRoute,
    private categoriesService: CategoriesService,
    private cmsService: CmsService,
    private sessionStorage: SessionStorageService,
    private router: Router,
    private viewportScroller: ViewportScroller,
    private winRef: WindowRef,
  ) {
    this.restoreScrollPosition();
  }

  ngOnInit(): void {
    // Spartacus only stores the most recently loaded category page data in the CMS state.
    // We need to manually refresh the data if the user navigates back to a previously visited category page.
    if (this.hasCategoryBeenVisited()) {
      this.cmsService.refreshLatestPage();
    } else {
      this.markCategoryAsVisited();
    }
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  private get categoryCode(): string {
    return this.activatedRoute.snapshot.params.categoryCode;
  }

  private hasCategoryBeenVisited(): boolean {
    const visitedCategories = this.sessionStorage.getItem(VISITED_CATEGORY_SESSION_STORAGE_KEY) || [];
    return visitedCategories.includes(this.categoryCode);
  }

  private markCategoryAsVisited(): void {
    const visitedCategoriesSet = new Set(this.sessionStorage.getItem(VISITED_CATEGORY_SESSION_STORAGE_KEY) || []);
    visitedCategoriesSet.add(this.categoryCode);
    this.sessionStorage.setItem(VISITED_CATEGORY_SESSION_STORAGE_KEY, Array.from(visitedCategoriesSet));
  }

  private restoreScrollPosition(): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    this.router.events
      .pipe(
        filter((event) => event instanceof Scroll),
        map((event: Scroll) => event.position),
        switchMap((position) => this.plpService.searchResults$.pipe(take(1), mapTo(position))),
        takeUntil(this.destroyed$),
      )
      .subscribe((position) => {
        // Wait for products to load before scrolling.
        setTimeout(() => this.viewportScroller.scrollToPosition(position || [0, 0]), 1);
      });
  }
}
