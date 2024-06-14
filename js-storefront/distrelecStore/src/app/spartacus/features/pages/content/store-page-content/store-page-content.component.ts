import { Component } from '@angular/core';
import { PageLayoutService } from '@spartacus/storefront';
import { Observable, ReplaySubject } from 'rxjs';
import { tap, filter, map, mapTo, switchMap, take, takeUntil } from 'rxjs/operators';
import { Router, Scroll } from '@angular/router';
import { Page, PaginationModel, WindowRef } from '@spartacus/core';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { PageHelper } from '@helpers/page-helper';
import { ViewportScroller } from '@angular/common';

@Component({
  selector: 'app-store-page-content',
  templateUrl: './store-page-content.component.html',
  styleUrls: ['./store-page-content.component.scss'],
})
export class StorePageContentComponent {
  readonly templateName$: Observable<string> = this.pageLayoutService.templateName$;
  readonly page$: Observable<Page> = this.pageLayoutService.page$;
  readonly slots$: Observable<string[]> = this.pageLayoutService.getSlots();

  pagination$: Observable<PaginationModel | null>;
  isNewProductsPage$ = false;
  isBrowser = false;
  firstProductNumber = 1;
  lastProductNumber = 50;
  isClearancePage = this.pageHelper.isClearancePage();

  private readonly destroyed$ = new ReplaySubject<void>();

  constructor(
    protected pageLayoutService: PageLayoutService,
    private plpService: DistProductListComponentService,
    private router: Router,
    private winRef: WindowRef,
    private pageHelper: PageHelper,
    private viewportScroller: ViewportScroller,
  ) {
    this.isBrowser = this.winRef.isBrowser();
    this.isNewProductsPage$ = this.router.url.lastIndexOf('/new') !== -1;
    this.pagination$ = this.plpService.pagination$;
    this.pagination$.pipe(
      tap((pagination) => {
        const isLastPage = pagination.currentPage === pagination.totalPages;
        const lastProductNumber = pagination.currentPage * pagination.pageSize;
        this.lastProductNumber = isLastPage ? pagination.totalResults : lastProductNumber;
        this.firstProductNumber = this.lastProductNumber - pagination.pageSize + 1;
      }),
    );

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
        switchMap((position) => this.plpService.searchResults$.pipe(take(1), mapTo(position))),
        takeUntil(this.destroyed$),
      )
      .subscribe((position) => {
        setTimeout(() => this.viewportScroller.scrollToPosition(position || [0, 0]), 1);
      });
  }
}
