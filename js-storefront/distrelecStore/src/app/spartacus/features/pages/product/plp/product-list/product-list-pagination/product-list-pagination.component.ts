import { Component, Input } from '@angular/core';
import { PaginationModel, ProductSearchPage } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PaginationQueryKey } from '@features/pages/product/plp/product-list/product-list-main/product-list-main-options';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';
import { filter, map, take, tap } from 'rxjs/operators';
import { arrowHorizontal } from '@assets/icons/icon-index';
import { DistIcon } from '@model/icon.model';

@Component({
  selector: 'app-product-list-pagination',
  templateUrl: './product-list-pagination.component.html',
  styleUrls: ['./product-list-pagination.component.scss'],
})
export class ProductListPaginationComponent {
  @Input() type = 'top';

  public arrow: DistIcon = arrowHorizontal;
  public isPlpActive$: Observable<boolean> = this.productListService.isPlpActive$;
  public paginationModel$: Observable<PaginationModel> = this.productListService.searchResults$.pipe(
    filter((model) => !!model.pagination),
    map((model: ProductSearchPage) => ({
      ...model.pagination,
      [PaginationQueryKey.PAGE]: this.getPageFromRoute() ?? model.pagination[PaginationQueryKey.PAGE],
    })),
  );

  private contentLength = 0;

  constructor(
    private productListService: DistProductListComponentService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  setCurrentPage(page: number): void {
    const { pageSize, ...otherParams } = this.route.snapshot.queryParams;
    const queryParams = {
      ...otherParams,
      [PaginationQueryKey.PAGE]: page,
      [PaginationQueryKey.PAGE_SIZE]: pageSize ?? 50,
    };

    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
    });
  }

  public getEllipseType(index: number): string {
    return index <= this.contentLength / 2 ? 'backwards' : 'forwards';
  }

  public get paginationContent$(): Observable<Array<number | '...'>> {
    return this.paginationModel$.pipe(
      take(1),
      map((pagination: PaginationModel) => {
        const currentPage = pagination.currentPage;
        const total = pagination.totalPages;
        const showAllLimit = 6;

        if (total <= showAllLimit) {
          return new Array(total).fill(0).map((_value: 0, index: number) => index + 1);
        }

        const truncateEnd = currentPage <= total / 2;
        let content: Array<number | '...'> = [];

        if (currentPage <= 2 || currentPage >= total - 1) {
          content = truncateEnd ? [1, 2, 3, '...', total] : [1, '...', total - 2, total - 1, total];
        } else if (currentPage === 3 || currentPage === total - 2) {
          content = truncateEnd ? [1, 2, 3, 4, '...', total] : [1, '...', total - 3, total - 2, total - 1, total];
        } else {
          content = [1, '...', currentPage - 1, currentPage, currentPage + 1, '...', total];
        }

        this.contentLength = content.length;
        return content;
      }),
    );
  }

  private getPageFromRoute(): number | undefined {
    return Number(this.route.snapshot.queryParams[PaginationQueryKey.PAGE]) || undefined;
  }
}
