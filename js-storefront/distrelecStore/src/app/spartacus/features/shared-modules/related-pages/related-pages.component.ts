import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { first, switchMap, tap } from 'rxjs/operators';

import { RelatedData, RelatedDataType } from '@model/category.model';
import { CategoriesService } from '@services/categories.service';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { SiteConfigService } from '@services/siteConfig.service';
import { RoutingService } from '@spartacus/core';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';

@Component({
  selector: 'app-related-pages',
  templateUrl: './related-pages.component.html',
  styleUrls: ['./related-pages.component.scss'],
})
export class RelatedPagesComponent implements OnInit {
  RelatedDataType = RelatedDataType;
  categoryData$: Observable<RelatedData[]>;
  isTechnicalView$: BehaviorSubject<boolean>;
  pageTemplate: string;
  isPlpActive$: Observable<boolean>;

  constructor(
    private _productDataService: ProductDataService,
    private _categoriesService: CategoriesService,
    private _SiteConfigService: SiteConfigService,
    private _routingService: RoutingService,
    private _plpService: DistProductListComponentService,
  ) {
    this.isTechnicalView$ = new BehaviorSubject(true);
    this.isPlpActive$ = this._plpService.isPlpActive$;
  }

  ngOnInit() {
    this.pageTemplate = this._SiteConfigService.getCurrentPageTemplate();
    if (this.pageTemplate === 'ProductDetailsPageTemplate') {
      this.categoryData$ = this._productDataService.getCategoryCode().pipe(
        tap((data) => {
          if (data) {
            this._categoriesService.getCategoryData(data).pipe(first()).subscribe();
          }
        }),
        switchMap(() => this._categoriesService.getRelatedData()),
      );
    } else if (this.pageTemplate === 'CategoryPageTemplate') {
      this.categoryData$ = this._routingService.getRouterState().pipe(
        tap((routerState) => {
          this.isTechnicalView$.next(routerState.state.queryParams?.useTechnicalView === 'true');
        }),
        switchMap(() => this._categoriesService.getRelatedData()),
      );
    } else {
      this.categoryData$ = this._categoriesService.getRelatedData();
    }
  }
}
