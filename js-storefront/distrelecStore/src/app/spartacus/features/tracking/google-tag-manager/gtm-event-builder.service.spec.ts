import {
  CmsService,
  createFrom,
  EventService,
  Page,
  PageType,
  WindowRef,
} from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { GtmEventBuilder } from '@features/tracking/google-tag-manager/gtm-event-builder.service';
import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { provideMockActions } from '@ngrx/effects/testing';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { GtmEventCollectorService } from '@features/tracking/google-tag-manager/gtm-event-collector.service';
import { CategoriesService } from '@services/categories.service';
import { MockSiteSettingsService } from '@features/mocks/mock-site-settings.service';
import { NavigationEvent } from '@spartacus/storefront';
import { take } from 'rxjs/operators';
import { MOCK_PAGE_VIEW_EVENT } from '@features/mocks/events/mock-page-view-event';
import { mockProductCategoryService } from '@features/mocks/events/mock-product-category';
import { MockWindowRef } from '@features/mocks/events/mock-window-ref';
import { MOCK_GA4_VIEW_CART_EVENT } from '@features/mocks/events/mock-ga4-view-cart-event';
import { CartAddEntrySuccessEvent, CartRemoveEntrySuccessEvent } from '@spartacus/cart/base/root';
import { PageViewEvent } from '@features/tracking/events/ga4/page-view-event';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { Ga4CartEvent } from '@features/tracking/events/ga4/ga4-cart-event';
import { Ga4HeaderInteractionEvent } from '../events/ga4/ga4-header-interaction-event';
import { MOCK_HEADER_INTERACTION } from '@features/mocks/events/mock-header-interaction';
import { Ga4AddToWishlistEvent } from '../events/ga4/ga4-add-to-wishlist-event';
import { Ga4SelectItem } from '../events/ga4/ga4-select-item-event';
import { MOCK_WISHLIST_EVENT } from '@features/mocks/events/mock-add-to-wishlist-event';
import { MOCK_GA4_SELECT_ITEM } from '@features/mocks/events/mock-ga4-select-item';
import { Ga4ViewItemListEvent } from '../events/ga4/ga4-view-item-list-event';
import { MOCK_VIEW_ITEM_LIST_EVENT } from '@features/mocks/events/mock-view-item-list';
import { Ga4PurchaseEvent } from '../events/ga4/ga4-purchase-event';
import { MOCK_PURCHASE_DATA, MOCK_PURCHASE_EVENT } from '@features/mocks/events/mock-purchase';
import { MOCK_ADD_TO_CART_EVENT } from '@features/mocks/events/mock-add-to-cart';
import { MOCK_REMOVE_FROM_CART } from '@features/mocks/events/mock-remove-from-cart';
import { CartViewEvent } from '../events/view-event';
import { MOCK_PAGE_DETAILS, MOCK_PAGE_STATE } from '@features/mocks/events/mock-page-details';
import { MOCK_USER_DETAILS } from '@features/mocks/events/mock-user-details';
import { MOCK_ADD_ENTRY_SUCCESS_EVENT } from '@features/mocks/events/mock-add-entry-success';
import { MOCK_REMOVE_ENTRY_SUCCESS } from '@features/mocks/events/mock-remove-entry-success';
import { MOCK_CART_VIEW_EVENT } from '@features/mocks/events/mock-cart-view';
import { MOCK_PRODUCT_CLICK_DATA } from '@features/mocks/events/mock-product-click';
import { HeaderInteractionEvent } from '../events/header-interaction-event';
import { Ga4HeaderInteractionEventType } from '../model/event-ga-header-types';
import { AddToShoppingListEvent } from '../events/add-to-shopping-list-event';
import { MOCK_WISHLIST_DATA } from '@features/mocks/events/mock-add-to-wishlist';
import { PriceService } from '@services/price.service';
import { ViewItemListEvent } from '../events/view-item-list-event';
import { MOCK_VIEW_ITEM_LIST_DATA } from '@features/mocks/events/mock-view-item';
import { PurchaseEvent } from '../events/purchase-event';
import { provideMockStore } from '@ngrx/store/testing';
import { CATEGORY_PAGE_STATE } from '@testing/mocks/data/category-page-state';
import { Ga4SearchEvent } from '../events/ga4/ga4-search-event';
import { SearchEvent } from '../events/search-event';
import { Ga4SearchSuggestionEvent } from '../events/ga4/ga4-search-suggestion-event';
import { SearchSuggestionEvent } from '../events/search-suggestion-event';
import {
  MOCK_SEARCH_DATA,
  MOCK_SEARCH_EVENT,
  MOCK_SEARCH_SUGGEST_DATA,
  MOCK_SEARCH_SUGGEST_EVENT,
} from '@features/mocks/events/mock-search-data';
import { Ga4RegistrationStartEvent } from '@features/tracking/events/ga4/ga4-registration-start-event';
import { RegistrationStartEvent } from '@features/tracking/events/registration-start-event';
import { MOCK_REGISTRATION_START_EVENT } from '@features/mocks/events/mock-registration-start-event';
import { DownloadPDFEvent, Ga4DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { MOCK_PRODUCT_DATA } from '@features/mocks/mock-product-data';
import { PDFType } from '@features/tracking/model/pdf-types';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { MOCK_DOWNLOAD_PDF_EVENT } from '@features/mocks/mock-download-pdf-event';
import { Ga4PrintPageEvent, PrintPageEvent } from '@features/tracking/events/print-page-event';
import { MOCK_PRINT_PAGE_EVENT } from '@features/mocks/mock-print-page-event';
import { PromotionClickEvent } from '@features/tracking/events/ga4/promotion-click-event';
import { MOCK_PROMOTIONAL_CLICK_DATA } from '@features/mocks/events/mock-promotional-click-data';
import { MOCK_PROMOTION_CLICK_EVENT } from '@features/mocks/events/mock-promotional-click-event';
import { Ga4ViewPdpReviewsEvent } from '@features/tracking/events/ga4/ga4-pdp-review-event';
import { ViewPdpReviewsEvent } from '@features/tracking/events/ga4/pdp-review-event';
import { MOCK_VIEW_PDP_REVIEWS_EVENT } from '@features/mocks/events/mock-view-pdp-reviews-event';
import { Ga4Error404Event, Ga4Error404EventType } from '@features/tracking/events/ga4/ga4-error-404-event';
import { Error404Event } from '@features/tracking/events/ga4/error-404-event';
import { AnalyticsCustomerType } from '@features/tracking/model/event-user-details';
import { MOCK_404_ERROR_EVENT } from '@features/mocks/events/mock-error-page-view-event';

const MOCK_CMS_PAGE = {
  pageId: 'homepage',
};

class MockCmsService {
  getCurrentPage(): Observable<Page> {
    return of(MOCK_CMS_PAGE);
  }

  getPageState() {
    return of(MOCK_PAGE_STATE)
  }

  getPriceWithVatForB2C() {
    return 5000;
  }
}

class MockPriceService {
  getPrices() {
    return of({
      price: {
        basePrice: 7278,
        currencyIso: "CHF",
        formattedPriceWithVat: "7’867.52",
        minQuantity: 1,
        priceType: "BUY",
        priceWithVat: 7867.518,
        value: 7867.518,
        vatPercentage: 8.1,
        vatValue: 589.518
      },
      volumePrices: [
        {
          basePrice: 7278,
          currencyIso: "CHF",
          minQuantity: 1,
          pricePerX: 0,
          pricePerXBaseQty: 0,
          pricePerXUOM: "PC",
          pricePerXUOMDesc: "piece",
          pricePerXUOMQty: 0,
          priceType: "BUY",
          priceWithVat: 7867.518,
          value: 7867.518,
          vatPercentage: 8.1,
          vatValue: 589.518
        }
      ],
      volumePricesMap: [
        {
          key: 1,
          value: [
            {
              key: "list",
              value: {
                basePrice: 7278,
                currencyIso: "CHF",
                formattedValue: "7’867.52",
                minQuantity: 1,
                pricePerX: 0,
                pricePerXBaseQty: 0,
                pricePerXUOM: "PC",
                pricePerXUOMDesc: "piece",
                pricePerXUOMQty: 0,
                priceType: "BUY",
                priceWithVat: 7867.518,
                value: 7867.518,
                vatPercentage: 8.1,
                vatValue: 589.518
              }
            }
          ]
        }
      ]
    });
  }

  getCurrencyFromPrice() {
    return 'CHF';
  }

  getPriceForQuantity() {
    return 50000;
  }

  getPriceWithoutVatForB2B() {
    return 2000;
  }

  getPriceWithVatForB2C() {
    return 5000;
  }
}

const mockGtmEventCollector = {
  pushEvent: jasmine.createSpy('pushEvent'),
};

describe('GTM Event Builder Service', () => {
  let actions$: Observable<any>;
  let eventService: EventService;
  let eventBuilder: GtmEventBuilder;
  let priceService: PriceService;
  let cmsPage: CmsService;

  beforeEach(() => {
    priceService = jasmine.createSpyObj('PriceService', ['getPriceWithoutVatForB2B', 'getPriceWithVatForB2C']);

    TestBed.configureTestingModule({
      imports: [CommonTestingModule, RouterModule],
      providers: [
        provideMockActions(() => actions$),
        { provide: WindowRef, useValue: MockWindowRef },
        { provide: CmsService, useClass: MockCmsService },
        { provide: AllsitesettingsService, useClass: MockSiteSettingsService },
        { provide: GtmEventCollectorService, useValue: mockGtmEventCollector },
        { provide: CategoriesService, useValue: mockProductCategoryService },
        { provide: PriceService, useClass: MockPriceService },
        provideMockStore({ initialState: CATEGORY_PAGE_STATE }),
      ],
    });
    eventService = TestBed.inject(EventService);
    eventBuilder = TestBed.inject(GtmEventBuilder);
    cmsPage = TestBed.inject(CmsService);

    eventBuilder.getPageObservable = () => of(MOCK_PAGE_DETAILS);
    eventBuilder.getUserObservable = () => of(MOCK_USER_DETAILS);

    eventBuilder.registerEvents();
  });

  it('Should trigger page view event as a b2c user', (done) => {
    eventBuilder.getPageObservable = () => of({
      document_title: "RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland",
      url: "https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart",
      category: "homepage",
      market: "CH"
    });

    MockWindowRef.nativeWindow.location = {
      "href": "https://distrelec-ch.local:4200/en/",
      "origin": "https://distrelec-ch.local:4200",
      "pathname": "/en/",
      "search": ""
    };

    let pageViewEvent: PageViewEvent | undefined;

    const navigationEvent: NavigationEvent = createFrom(NavigationEvent, {
      context: { id: '__HOMEPAGE__', type: PageType.CONTENT_PAGE },
      semanticRoute: 'home',
      url: 'en/',
      params: {},
    });

    eventService
      .get(PageViewEvent)
      .pipe(take(1))
      .subscribe((result) => {
        pageViewEvent = result;

        const expected = createFrom(PageViewEvent, MOCK_PAGE_VIEW_EVENT);
        expect(pageViewEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(navigationEvent);
  });

  it('Should trigger addToCart event as a b2c user', (done) => {
    let addToCartEvent: Ga4CartEvent | undefined;

    eventService
      .get(Ga4CartEvent)
      .pipe(take(1))
      .subscribe((result) => {
        addToCartEvent = result;

        const expected = createFrom(Ga4CartEvent, MOCK_ADD_TO_CART_EVENT);
        expect(addToCartEvent).toEqual(expected);
        done();
      });
    eventService.dispatch(createFrom(CartAddEntrySuccessEvent, MOCK_ADD_ENTRY_SUCCESS_EVENT) as CartAddEntrySuccessEvent);
  });

  it('Should trigger removeFromCart event as a b2c user', (done) => {
    let removeCartEvent: Ga4CartEvent | undefined;

    eventService
      .get(Ga4CartEvent)
      .pipe(take(1))
      .subscribe((result) => {
        removeCartEvent = result;

        const expected = createFrom(Ga4CartEvent, MOCK_REMOVE_FROM_CART);
        expect(removeCartEvent).toEqual(expected);
        done();
      });
    eventService.dispatch(createFrom(CartRemoveEntrySuccessEvent, MOCK_REMOVE_ENTRY_SUCCESS) as CartRemoveEntrySuccessEvent);
  });

  it('Should trigger viewCartEvent event as a b2c user', (done) => {
    let viewCartEvent: Ga4CartEvent | undefined;

    eventService
      .get(Ga4CartEvent)
      .pipe(take(1))
      .subscribe((result) => {
        viewCartEvent = result;

        const expected = createFrom(Ga4CartEvent, MOCK_GA4_VIEW_CART_EVENT);
        expect(viewCartEvent).toEqual(expected);
        done();
      });
    eventService.dispatch(createFrom(CartViewEvent, MOCK_CART_VIEW_EVENT) as CartViewEvent);
  });

  it('Should trigger select item event as a b2c user', (done) => {
    let ga4SelectItem: Ga4SelectItem;

    eventService
      .get(Ga4SelectItem)
      .pipe(take(1))
      .subscribe((result) => {
        ga4SelectItem = result;

        const expected = createFrom(Ga4SelectItem, MOCK_GA4_SELECT_ITEM);
        expect(ga4SelectItem).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(ProductClickEvent, MOCK_PRODUCT_CLICK_DATA) as ProductClickEvent);
  });

  it('Should trigger header interaction event as a b2c user', (done) => {
    let headerInteractionEvent: Ga4HeaderInteractionEvent;

    eventService
      .get(Ga4HeaderInteractionEvent)
      .pipe(take(1))
      .subscribe((result) => {
        headerInteractionEvent = result;

        const expected = createFrom(Ga4HeaderInteractionEvent, MOCK_HEADER_INTERACTION);
        expect(headerInteractionEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(HeaderInteractionEvent, {type: Ga4HeaderInteractionEventType.HEADER_CLICK_SETTINGS}) as HeaderInteractionEvent);
  });

  it('Should trigger an add to wishlist event as a b2c user', (done) => {
    let addToWishlistEvent: Ga4AddToWishlistEvent | undefined;

    eventService.get(Ga4AddToWishlistEvent)
      .pipe(take(1))
      .subscribe((result) => {
        addToWishlistEvent = result;

        const expected = createFrom(Ga4AddToWishlistEvent, MOCK_WISHLIST_EVENT);
        expect(addToWishlistEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(AddToShoppingListEvent, MOCK_WISHLIST_DATA) as AddToShoppingListEvent);
  });

  it('Should trigger a viem item list event as a b2c user', (done) => {
    let viewItemListEvent: Ga4ViewItemListEvent | undefined;

    eventService.get(Ga4ViewItemListEvent)
      .pipe(take(1))
      .subscribe((result) => {
        viewItemListEvent = result;

        const expected = createFrom(Ga4ViewItemListEvent, MOCK_VIEW_ITEM_LIST_EVENT);
        expect(viewItemListEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(ViewItemListEvent, MOCK_VIEW_ITEM_LIST_DATA) as ViewItemListEvent);
  });

  it('Should trigger a purchase event as a b2c user', (done) => {
    let purchaseEvent: Ga4PurchaseEvent | undefined;

    eventService.get(Ga4PurchaseEvent)
      .pipe(take(1))
      .subscribe((result) => {
        purchaseEvent = result;

        const expected = createFrom(Ga4PurchaseEvent, MOCK_PURCHASE_EVENT);
        expect(purchaseEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(PurchaseEvent, MOCK_PURCHASE_DATA) as PurchaseEvent);
    });

  it('Should trigger a search event as a b2c user', (done) => {
    let searchEvent: Ga4SearchEvent | undefined;

    eventService.get(Ga4SearchEvent)
      .pipe(take(1))
      .subscribe((result) => {
        searchEvent = result;

        const expected = createFrom(Ga4SearchEvent, MOCK_SEARCH_EVENT);
        expect(searchEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(SearchEvent, MOCK_SEARCH_DATA) as SearchEvent);
  });

  it('Should trigger a search suggestion event as a b2c user', (done) => {
    let searchSuggestionEvent: Ga4SearchSuggestionEvent | undefined;

    eventService.get(Ga4SearchSuggestionEvent)
      .pipe(take(1))
      .subscribe((result) => {
        searchSuggestionEvent = result;

        const expected = createFrom(Ga4SearchSuggestionEvent, MOCK_SEARCH_SUGGEST_EVENT);
        expect(searchSuggestionEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(SearchSuggestionEvent, MOCK_SEARCH_SUGGEST_DATA) as SearchSuggestionEvent);
  });

  it('Should trigger a registration start event as a b2c user', (done) => {
    let searchSuggestionEvent: Ga4RegistrationStartEvent | undefined;

    eventService.get(Ga4RegistrationStartEvent)
      .pipe(take(1))
      .subscribe((result) => {
        searchSuggestionEvent = result;

        const expected = createFrom(Ga4RegistrationStartEvent, MOCK_REGISTRATION_START_EVENT);
        expect(searchSuggestionEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(RegistrationStartEvent, { type: 'registration_start' }) as RegistrationStartEvent);
  });

  it('Should trigger a Ga4DownloadPDFEvent event as a b2c user', (done) => {
    let downloadPDFEvent: Ga4DownloadPDFEvent | undefined;

    eventService.get(Ga4DownloadPDFEvent)
      .pipe(take(1))
      .subscribe((result) => {
        downloadPDFEvent = result;

        const expected = createFrom(Ga4DownloadPDFEvent, MOCK_DOWNLOAD_PDF_EVENT);
        expect(downloadPDFEvent).toEqual(expected);
        done();
      });

    eventService.dispatch(createFrom(DownloadPDFEvent, { context: { pageType: ItemListEntity.BACKORDER, PDF_type: PDFType.BACKORDER, product: MOCK_PRODUCT_DATA } }) as DownloadPDFEvent);
  });

  it('Should trigger a Ga4PrintPageEvent event as a b2c user', (done) => {
    let printPageEvent: Ga4PrintPageEvent | undefined;

    eventService.get(Ga4PrintPageEvent)
      .pipe(take(1))
      .subscribe((result) => {
        printPageEvent = result;

        const expected = createFrom(Ga4PrintPageEvent, MOCK_PRINT_PAGE_EVENT);
        expect(printPageEvent).toEqual(expected);
        done();
      })
    eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.INVOICE } }) as PrintPageEvent);
  })

  it('Should trigger a PromotionClickEvent event as a b2c user', (done) => {
    let promotionClickEvent: PromotionClickEvent | undefined;

    eventService.get(PromotionClickEvent)
      .pipe(take(1))
      .subscribe((result) => {
        promotionClickEvent = result;

        const expected = createFrom(PromotionClickEvent, MOCK_PROMOTION_CLICK_EVENT);
        expect(promotionClickEvent).toEqual(expected);
        done();
      })
    eventService.dispatch(createFrom(PromotionClickEvent, MOCK_PROMOTIONAL_CLICK_DATA) as PromotionClickEvent);
  })

  it('Should trigger a Ga4ViewPdpReviewsEvent event as a b2c user', (done) => {
    let viewPdpReviewsEvent: Ga4ViewPdpReviewsEvent | undefined;

    eventService.get(Ga4ViewPdpReviewsEvent)
      .pipe(take(1))
      .subscribe((result) => {
        viewPdpReviewsEvent = result;

        const expected = createFrom(Ga4ViewPdpReviewsEvent, MOCK_VIEW_PDP_REVIEWS_EVENT);
        expect(viewPdpReviewsEvent).toEqual(expected);
        done();
      })
    eventService.dispatch(createFrom(ViewPdpReviewsEvent, { event: 'view_pdp_reviews' }) as ViewPdpReviewsEvent);
  })

  it('Should trigger a GA4Error404Event event as a b2c user', (done) => {
    let error404Event: Ga4Error404Event | undefined;

    eventService.get(Ga4Error404Event)
    .pipe(take(1))
    .subscribe((result) => {
      error404Event = result;

      const expected = createFrom(Ga4Error404Event, MOCK_404_ERROR_EVENT);
      expect(error404Event).toEqual(expected);
      done();
    })

    eventService.dispatch(createFrom(Error404Event, { event: Ga4Error404EventType.ERROR_404 }) as Error404Event);
  });
});
