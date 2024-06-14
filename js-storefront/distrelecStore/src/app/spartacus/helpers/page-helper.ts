import { Injectable, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CmsService, Page } from '@spartacus/core';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Injectable({
  providedIn: 'root',
})
export class PageHelper implements OnDestroy {
  private currentPageTemplate = '';
  private previousPageTemplate = '';
  private pageSubscription: Subscription;

  constructor(
    private cmsService: CmsService,
    private router: Router,
  ) {
    this.pageSubscription = this.cmsService
      .getCurrentPage()
      .pipe(filter<Page>(Boolean))
      .subscribe((page) => {
        this.previousPageTemplate = this.currentPageTemplate;
        this.currentPageTemplate = page.template;
      });
  }

  ngOnDestroy() {
    this.pageSubscription.unsubscribe();
  }

  getCurrentPageTemplate(): string {
    return this.currentPageTemplate;
  }

  getPreviousPageTemplate(): string {
    return this.previousPageTemplate;
  }

  isManufacturerDetailPage(): boolean {
    return this.currentPageTemplate === 'ManufacturerStoreDetailPageTemplate';
  }

  isProductFamilyPage(): boolean {
    return this.currentPageTemplate === 'ProductFamilyPageTemplate';
  }

  isCategoryPage(): boolean {
    return this.currentPageTemplate === 'CategoryPageTemplate';
  }

  isSearchPage(): boolean {
    return (
      this.currentPageTemplate === 'SearchResultsListPageTemplate' ||
      this.currentPageTemplate === 'productCategorySearchPageWsDTO' ||
      (this.currentPageTemplate === 'ContentPage' && this.router.url.lastIndexOf('/search') !== -1)
    );
  }

  isNewProductsPage(): boolean {
    return this.currentPageTemplate === 'StorePageTemplate' && this.router.url.lastIndexOf('/new') !== -1;
  }

  isClearancePage(): boolean {
    return this.currentPageTemplate === 'StorePageTemplate' && this.router.url.lastIndexOf('/clearance') !== -1;
  }

  isProductListPage(): boolean {
    return (
      this.isSearchPage() ||
      this.isManufacturerDetailPage() ||
      this.isProductFamilyPage() ||
      this.isClearancePage() ||
      this.isCategoryPage() ||
      this.isNewProductsPage()
    );
  }

  getListPageEntityType(): ItemListEntity {
    if (this.isSearchPage()) {
      return ItemListEntity.SEARCH;
    } else if (this.isCategoryPage()) {
      return ItemListEntity.CATEGORY;
    } else if (this.isManufacturerDetailPage()) {
      return ItemListEntity.MANUFACTURER;
    } else if (this.isProductFamilyPage()) {
      return ItemListEntity.PFP;
    }
    return null;
  }
}
