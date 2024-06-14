import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { Router, Scroll } from '@angular/router';
import { combineLatest, Observable, ReplaySubject } from 'rxjs';
import { filter, map, mapTo, switchMap, take, takeUntil } from 'rxjs/operators';
import { WindowRef } from '@spartacus/core';
import { PageLayoutService } from '@spartacus/storefront';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';

type ListPageTemplate = 'SearchResultsEmptyPageTemplate' | 'SearchResultsListPageTemplate';

@Component({
  selector: 'app-product-list-page',
  templateUrl: './product-list-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductListPageComponent implements OnDestroy {
  isLoading = true;
  isEmpty = false;

  readonly model$ = this.productListService.model$;
  readonly isLoading$ = this.productListService.isLoading$;
  readonly isEmpty$: Observable<boolean> = this.model$.pipe(
    map((model) => Boolean(!model.products?.length) || Boolean(model.emptyPageType)),
  );

  readonly data$ = combineLatest([this.isLoading$, this.isEmpty$]).pipe(
    map(([isLoading, isEmpty]) => ({ isLoading, isEmpty })),
  );

  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();

  private readonly destroyed$ = new ReplaySubject<void>();

  constructor(
    protected pageLayoutService: PageLayoutService,
    private productListService: DistProductListComponentService,
    private router: Router,
    private viewportScroller: ViewportScroller,
    private winRef: WindowRef,
  ) {
    // Async piping this in the template causes the loading skeleton to load too slowly.
    this.data$.pipe(takeUntil(this.destroyed$)).subscribe((data) => {
      this.isLoading = data.isLoading;
      this.isEmpty = data.isEmpty;
    });

    this.restoreScrollPosition();
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  private restoreScrollPosition(): void {
    if (!this.winRef.isBrowser()) {
      return;
    }

    this.router.events
      .pipe(
        filter((event) => event instanceof Scroll),
        map((event: Scroll) => event.position),
        switchMap((position) => this.productListService.searchResults$.pipe(take(1), mapTo(position))),
        takeUntil(this.destroyed$),
      )
      .subscribe((position) => {
        // Wait for products to load before scrolling.
        setTimeout(() => this.viewportScroller.scrollToPosition(position || [0, 0]), 1);
      });
  }
}
