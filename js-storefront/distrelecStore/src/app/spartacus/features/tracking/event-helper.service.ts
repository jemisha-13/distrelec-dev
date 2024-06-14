import { Injectable, Type } from '@angular/core';
import { EventService, Page, Product, ProductSearchPage } from '@spartacus/core';
import { first, take } from 'rxjs/operators';
import { Order } from '@spartacus/order/root/model';
import { GtmEventBuilder } from '@features/tracking/google-tag-manager/gtm-event-builder.service';
import { FactFinderEventBuilder } from '@features/tracking/fact-finder/fact-finder-event.builder';
import { FactFinderProductParameters } from '@model/factfinder.model';
import { BloomreachEventBuilder } from '@features/tracking/bloomreach/bloomreach-event-builder.service';
import { CategoryPageData } from '@model/category.model';
import { combineLatest, Observable } from 'rxjs';
import { BloomreachPasswordPageViewEvent } from './events/bloomreach/bloomreach-rs-password-page-view-event';
import { BloomreachAccountActivationEvent } from '@features/tracking/events/bloomreach-account-activation-event';
import { PageHelper } from '@helpers/page-helper';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class EventHelper {
  constructor(
    private eventService: EventService,
    private eventBuilder: GtmEventBuilder,
    private factFinderEventBuilder: FactFinderEventBuilder,
    private bloomreachEventBuilder: BloomreachEventBuilder,
    private pageHelper: PageHelper,
    private router: Router,
  ) {}

  trackImageClick(code: string): void {
    this.eventBuilder
      .buildImageClickEvent(code)
      .pipe(first())
      .subscribe((event) => this.eventService.dispatch(event));
  }

  trackCompareListEvent(compareProduct: Product): void {
    this.eventBuilder
      .buildCompareListEvent(compareProduct)
      .pipe(first())
      .subscribe((event) => this.eventService.dispatch(event));
  }

  trackHotBarCompareEvent(): void {
    this.eventBuilder
      .buildHotBarCompareEvent()
      .pipe(first())
      .subscribe((event) => this.eventService.dispatch(event));
  }

  trackHotBarClearAllEvent(): void {
    this.eventBuilder
      .buildHotBarClearAllEvent()
      .pipe(first())
      .subscribe((event) => this.eventService.dispatch(event));
  }

  trackOrderConfirmationPage(orderData: Order) {
    this.factFinderEventBuilder
      .buildCheckoutEvent(orderData)
      .pipe(first())
      .subscribe((event) => {
        this.eventService.dispatch(event);
      });

    const isSubUser = orderData.b2bCustomerData.approvers.length > 0;

    this.eventService.dispatch(this.bloomreachEventBuilder.buildPurchaseOrderEvent(orderData, isSubUser));
    const userType = orderData?.customerType !== 'GUEST' ? undefined : 'guest_purchase_item';
    orderData.entries.map((entry) =>
      this.eventService.dispatch(
        this.bloomreachEventBuilder.buildPurchaseItemEvent(orderData, entry, userType, isSubUser),
      ),
    );
  }

  trackFactFinderCartEvent(productQueryParams: FactFinderProductParameters) {
    this.factFinderEventBuilder
      .buildCartEvent(productQueryParams)
      .pipe(first())
      .subscribe((event) => {
        this.eventService.dispatch(event);
      });
  }

  trackFactFinderNoResultsEvent(searchTerm: string) {
    this.eventBuilder
      .buildSiteSearchNoResultsEvent(searchTerm)
      .pipe(first())
      .subscribe((event) => {
        this.eventService.dispatch(event);
      });
  }

  trackBloomreachRSRegistrationEvent(erpId: string) {
    this.bloomreachEventBuilder
      .buildAccountActivationEvent(erpId)
      .pipe(first())
      .subscribe((event: BloomreachAccountActivationEvent) => {
        this.eventService.dispatch(event);
      })
      .unsubscribe();
  }

  trackBloomreachSetPasswordEvent(email: string): void {
    this.bloomreachEventBuilder
      .buildSetPasswordEvent(email)
      .pipe(first())
      .subscribe((event: BloomreachPasswordPageViewEvent) => {
        this.eventService.dispatch(event);
      });
  }

  trackBloomreachPlpEvent(
    results: ProductSearchPage,
    products: Product[],
    cmsPageData: Page,
    categoryData?: CategoryPageData,
  ) {
    const isRedirected =
      products.length === 1 &&
      !results.currentQuery.query.value.includes('filter_') &&
      this.pageHelper.isSearchPage() && !this.router.url.includes('currentPage');
        
    if (!isRedirected) {
      const event = this.bloomreachEventBuilder.buildPlpViewEvent(categoryData, cmsPageData, results);
      this.eventService.dispatch(event);        
    }
  }

  trackBloomreachPasswordPageViewEvent(email: string): void {
    this.bloomreachEventBuilder
      .buildPasswordPageviewEvent(email)
      .pipe(first())
      .subscribe((event: BloomreachPasswordPageViewEvent) => {
        this.eventService.dispatch(event);
      });
  }
}
