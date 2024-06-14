/* eslint-disable @typescript-eslint/naming-convention */
import { BloomreachPurchaseOrderEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-order-event';
import { EventService, TranslationService, WindowRef, createFrom, Product } from '@spartacus/core';
import { TestBed } from '@angular/core/testing';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of } from 'rxjs';
import { EventHelper } from '@features/tracking/event-helper.service';
import { RouterTestingModule } from '@angular/router/testing';
import { take } from 'rxjs/operators';
import { BloomreachPurchaseItemEvent } from '@features/tracking/events/bloomreach/bloomreach-purchase-item-event';
import { GtmEventBuilder } from '@features/tracking/google-tag-manager/gtm-event-builder.service';
import { FactFinderEventBuilder } from '@features/tracking/fact-finder/fact-finder-event.builder';
import { BloomreachEventBuilder } from '@features/tracking/bloomreach/bloomreach-event-builder.service';
import { MOCK_SUBUSER_DATA } from '@features/mocks/mock-sub-user-data';
import { MOCK_SUBUSER_ORDER_EVENT } from '@features/mocks/mock-sub-user-event';
import { MOCK_SUBUSER_ITEM_EVENT } from '@features/mocks/mock-sub-user-item-event';
import { BloomreachPlpViewEvent } from './events/bloomreach/bloomreach-plp-view-event';
import { CheckoutEvent } from './events/checkout-event';
import { AnalyticsCustomerType } from './model/event-user-details';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { ProductClickEvent } from './events/ga4/product-click-event';

import { MOCK_CLICK_EVENT } from '@features/mocks/events/mock-click-event';
import { MOCK_PLP_VIEW_EVENT } from '@features/mocks/events/mock-plp-view-event';
import { MOCK_SALES_ORDER_EVENT, MOCK_SALES_ORDER_GUEST_EVENT } from '@features/mocks/events/mock-sales-order-event';
import { MOCK_SALES_ORDER__ITEM_EVENT } from '@features/mocks/events/mock-sales-order-item-event';
import { MOCK_GUEST_ORDER__ITEM_EVENT } from '@features/mocks/events/mock-guest-order-item-event';
import { MOCK_CHECKOUT_STEP_EVENT } from '@features/mocks/events/mock-checkout-step-event';

import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { MockSiteSettingsService } from '@features/mocks/mock-site-settings.service';
import { SiteSearchNoResultsEvent } from '@features/tracking/events/site-search-no-results-event';

import { MOCK_PRODUCT_SEARCH_PAGE } from '@features/mocks/mock-product-search-page';
import { MOCK_CMS_PAGE } from '@features/mocks/mock-cms-page';
import { MOCK_PRODUCT_DATA$ } from '@features/mocks/mock-product-data';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';
import { MOCK_GUEST_DATA } from '@features/mocks/mock-guest-data';
import { MOCK_NO_RESULTS_EVENT } from '@features/mocks/mock-no-results-page-event';
import { PageHelper } from '@helpers/page-helper';
import { mockPageHelper } from '@features/mocks/mock-page-helper';

const productData = [
  {
    id: '30216307',
    quantity: 1,
    brand: 'PeakTech',
    category: 'Insulation Testers',
    name: 'Insulation Tester, 200GOhm ... 2TOhm, ±5 %',
    price: '713',
    total: '713',
  },
];

const MockWindowRef = {
  nativeWindow: {
    location: {
      origin: 'https://distrelec-ch.local:4200',
      href: 'https://distrelec-ch.local:4200/en/checkout/orderConfirmation/4ca45324-9ab8-4c03-a858-85b0588b7c6a',
    },
  },
  isBrowser(): boolean {
    return true;
  },
};

describe('Service: Event Helper', () => {
  let actions$: Observable<any>;
  let eventService: EventService;
  let eventBuilder: GtmEventBuilder;
  let factFinderEventBuilder: FactFinderEventBuilder;
  let bloomreachEventBuilder: BloomreachEventBuilder;
  let eventHelper: EventHelper;
  let pageHelper: PageHelper;

  beforeEach(() => {
    eventBuilder = jasmine.createSpyObj('GtmEventBuilder', [
      'buildProductClickEvent',
      'buildCheckoutEvent',
      'buildSiteSearchNoResultsEvent',
    ]);
    factFinderEventBuilder = jasmine.createSpyObj('FactFinderEventBuilder', ['buildCheckoutEvent']);

    TestBed.configureTestingModule({
      imports: [CommonTestingModule, RouterTestingModule],
      providers: [
        provideMockActions(() => actions$),
        { provide: WindowRef, useValue: MockWindowRef },
        { provide: FactFinderEventBuilder, useValue: factFinderEventBuilder },
        { provide: GtmEventBuilder, useValue: eventBuilder },
        { provide: TranslationService, useValue: MockTranslationService },
        { provide: AllsitesettingsService, useClass: MockSiteSettingsService },
        { provide: PageHelper, useValue: mockPageHelper },
      ],
    });

    bloomreachEventBuilder = TestBed.inject(BloomreachEventBuilder);
    eventHelper = TestBed.inject(EventHelper);
    eventService = TestBed.inject(EventService);

    (factFinderEventBuilder.buildCheckoutEvent as jasmine.Spy)
      .withArgs(MOCK_ORDER_DATA)
      .and.returnValue(of('something'));

    (factFinderEventBuilder.buildCheckoutEvent as jasmine.Spy)
      .withArgs(MOCK_GUEST_DATA)
      .and.returnValue(of('something'));

    (factFinderEventBuilder.buildCheckoutEvent as jasmine.Spy)
      .withArgs(MOCK_SUBUSER_DATA)
      .and.returnValue(of('something'));

    (eventBuilder.buildSiteSearchNoResultsEvent as jasmine.Spy)
      .withArgs('lorem')
      .and.callFake((arg, arg2) => of(createFrom(SiteSearchNoResultsEvent, MOCK_NO_RESULTS_EVENT)));

    (mockPageHelper.isSearchPage as jasmine.Spy).and.returnValue(true);
  });

  it('Should trigger sales_order event', (done) => {
    let bloomreachPurchaseOrderEvent: BloomreachPurchaseOrderEvent | undefined;

    eventService
      .get(BloomreachPurchaseOrderEvent)
      .pipe(take(1))
      .subscribe((result) => {
        bloomreachPurchaseOrderEvent = result;

        const expected = createFrom(BloomreachPurchaseOrderEvent, MOCK_SALES_ORDER_EVENT);
        expect(bloomreachPurchaseOrderEvent).toEqual(jasmine.objectContaining({ ...expected }));
        done();
      });

    eventHelper.trackOrderConfirmationPage(MOCK_ORDER_DATA);
  });

  it('Should trigger sales_order event and confirm placement property to be sub user', () => {
    let bloomreachPurchaseOrderEvent: BloomreachPurchaseOrderEvent | undefined;

    eventService
      .get(BloomreachPurchaseOrderEvent)
      .pipe(take(1))
      .subscribe((result) => (bloomreachPurchaseOrderEvent = result));

    eventHelper.trackOrderConfirmationPage(MOCK_SUBUSER_DATA);

    const expected = createFrom(BloomreachPurchaseOrderEvent, MOCK_SUBUSER_ORDER_EVENT);
    expect(bloomreachPurchaseOrderEvent).toEqual(jasmine.objectContaining({ ...expected }));
    expect(bloomreachPurchaseOrderEvent.body).toEqual(jasmine.objectContaining({ placement: expected.body.placement }));
  });

  it('Should trigger sales_order_item event', () => {
    let bloomreachPurchaseItemEvent: BloomreachPurchaseItemEvent | undefined;

    eventService
      .get(BloomreachPurchaseItemEvent)
      .pipe(take(1))
      .subscribe((result) => (bloomreachPurchaseItemEvent = result));

    eventHelper.trackOrderConfirmationPage(MOCK_ORDER_DATA);

    const expected = createFrom(BloomreachPurchaseItemEvent, MOCK_SALES_ORDER__ITEM_EVENT);
    expect(bloomreachPurchaseItemEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('Should trigger sales_order_item event and confirm placement property to be sub user', () => {
    let bloomreachPurchaseItemEvent: BloomreachPurchaseItemEvent | undefined;

    eventService
      .get(BloomreachPurchaseItemEvent)
      .pipe(take(1))
      .subscribe((result) => (bloomreachPurchaseItemEvent = result));

    eventHelper.trackOrderConfirmationPage(MOCK_SUBUSER_DATA);

    const expected = createFrom(BloomreachPurchaseItemEvent, MOCK_SUBUSER_ITEM_EVENT);
    expect(bloomreachPurchaseItemEvent).toEqual(jasmine.objectContaining({ ...expected }));
    expect(bloomreachPurchaseItemEvent.body).toEqual(jasmine.objectContaining({ placement: expected.body.placement }));
  });

  it('Should trigger sales_order_item event as a guest user', () => {
    let bloomreachPurchaseItemEvent: BloomreachPurchaseItemEvent | undefined;

    eventService
      .get(BloomreachPurchaseItemEvent)
      .pipe(take(1))
      .subscribe((result) => {
        bloomreachPurchaseItemEvent = result;
      });

    eventHelper.trackOrderConfirmationPage(MOCK_GUEST_DATA);

    const expected = createFrom(BloomreachPurchaseItemEvent, MOCK_GUEST_ORDER__ITEM_EVENT);
    expect(bloomreachPurchaseItemEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('Should trigger a plp_view event', () => {
    let bloomreachPlpViewEvent: BloomreachPlpViewEvent | undefined;

    eventService
      .get(BloomreachPlpViewEvent)
      .pipe(take(1))
      .subscribe((result) => {
        bloomreachPlpViewEvent = result;
      });

    eventHelper.trackBloomreachPlpEvent(
      MOCK_PRODUCT_SEARCH_PAGE,
      MOCK_PRODUCT_DATA$ as unknown as Product[],
      MOCK_CMS_PAGE,
    );

    const expected = createFrom(BloomreachPlpViewEvent, MOCK_PLP_VIEW_EVENT);
    expect(bloomreachPlpViewEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('Should trigger a no search results event', () => {
    let noResultEvent: SiteSearchNoResultsEvent | undefined;

    eventService
      .get(SiteSearchNoResultsEvent)
      .pipe(take(1))
      .subscribe((result) => {
        noResultEvent = result;
      });

    eventHelper.trackFactFinderNoResultsEvent('lorem');

    const expected = createFrom(SiteSearchNoResultsEvent, MOCK_NO_RESULTS_EVENT);
    expect(noResultEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });
});
