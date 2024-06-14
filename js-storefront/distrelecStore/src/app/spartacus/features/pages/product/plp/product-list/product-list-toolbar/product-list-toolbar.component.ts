import { Component, HostListener, OnDestroy, ViewChild } from '@angular/core';
import { filter, first, map, tap } from 'rxjs/operators';
import { CmsService, Page, PaginationModel } from '@spartacus/core';
import { ViewConfig } from '@spartacus/storefront';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { LinkService } from '@services/link.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {
  ListFilters,
  PaginationQueryKey,
  SORT_OPTIONS,
} from '@features/pages/product/plp/product-list/product-list-main/product-list-main-options';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { ProductListViewService } from '@features/pages/product/core/services/product-list-view.service';
import { arrowDown, arrowUp } from '@assets/icons/icon-index';

@Component({
  selector: 'app-product-list-toolbar',
  templateUrl: './product-list-toolbar.component.html',
  styleUrls: ['./product-list-toolbar.component.scss'],
})
export class ProductListToolbarComponent implements OnDestroy {

  public sort: string;
  arrowUp = arrowUp;
  arrowDown = arrowDown;

  readonly pagination$: Observable<PaginationModel>;
  readonly sortOptions: ListFilters = SORT_OPTIONS;

  private pageSize = this.config.view.defaultPageSize;
  private currentPage = 1;
  private routeQueryParams: Params;

  constructor(
    private plpViewService: ProductListViewService,
    private plpService: DistProductListComponentService,
    private config: ViewConfig,
    private allSiteSettings: AllsitesettingsService,
    private linkService: LinkService,
    private cmsService: CmsService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.routeQueryParams = this.route.snapshot.queryParams;
    this.sort = this.routeQueryParams[PaginationQueryKey.SORT] ?? (this.sortOptions.options[0].value as string);

    this.pagination$ = this.plpService.searchResults$.pipe(
      map((model) => model.pagination),
      tap((pagination) => {
        this.setSeoPaginationLinks(pagination);
        this.pageSize = pagination.pageSize;
        this.currentPage = pagination.currentPage;
      }),
    );
  }

  ngOnDestroy(): void {
    this.removeSeoPaginationLinks();
  }

  filterList(queryKey: string, queryValue: unknown): void {
    const params: Params = {
      currentPage: this.currentPage,
      pageSize: this.pageSize,
    };

    if (queryKey !== 'pageSize') {
      params[queryKey] = queryValue;
    } else {
      const startIndex: number = this.currentPage * this.pageSize - this.pageSize;
      const newCurrentPage: number = Math.floor(startIndex / (queryValue as number)) + 1;
      params.currentPage = newCurrentPage;
      params.pageSize = queryValue;
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { ...this.routeQueryParams, ...params },
    });
  }

  private setSeoPaginationLinks(pagination: PaginationModel) {
    this.removeSeoPaginationLinks();
    this.cmsService
      .getCurrentPage()
      .pipe(filter<Page>(Boolean), first())
      .subscribe((page) => {
        const canonicalUrl = page?.properties?.seo?.canonicalUrl;
        const currentUrl = this.router.url.split('?')[0];

        if (canonicalUrl && !canonicalUrl.includes(currentUrl)) {
          this.cmsService.refreshLatestPage();
          return;
        }
        if (page.type === 'CategoryPage') {
          this.addSeoPaginationLinks(pagination);
        }
      });
  }

  private addSeoPaginationLinks(pagination: PaginationModel) {
    this.allSiteSettings
      .getDomain()
      .pipe(first())
      .subscribe((domain) => {
        const url = this.router.parseUrl(this.router.url);

        if (pagination.currentPage > 1) {
          if (pagination.currentPage !== 2) {
            url.queryParams.page = pagination.currentPage - 1;
          } else {
            delete url.queryParams.page;
          }
          this.linkService.addLink({
            rel: 'prev',
            href: `${domain}${url.toString()}`,
          });
        }

        if (pagination.currentPage < pagination.totalPages) {
          url.queryParams.page = pagination.currentPage + 1;
          this.linkService.addLink({
            rel: 'next',
            href: `${domain}${url.toString()}`,
          });
        }
      });
  }

  private removeSeoPaginationLinks() {
    this.linkService.removeLink('rel="prev"');
    this.linkService.removeLink('rel="next"');
  }
}
