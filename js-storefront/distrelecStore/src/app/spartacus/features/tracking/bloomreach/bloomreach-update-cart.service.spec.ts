/* tslint:disable:no-unused-variable */
/* eslint-disable @typescript-eslint/naming-convention */
import { TestBed } from '@angular/core/testing';
import { BloomreachUpdateCartTracking } from './bloomreach-update-cart.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { provideMockActions } from '@ngrx/effects/testing';
import { StoreModule } from '@ngrx/store';
import { CmsService, createFrom, EventService, Page, Price, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { BloomreachUpdateCartEvent } from '../events/bloomreach/bloomreach-update-cart-event';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { CartStoreService } from '@state/cartState.service';
import { MOCK_CART_ENTRY, MOCK_PRICE_OBJECT, MockCartStoreService } from '@features/mocks/mock-cart-store.service';
import { PriceService } from '@services/price.service';
import { Channel } from '@model/site-settings.model';

class MockWindowRef {
  nativeWindow = {
    location: {
      origin: 'https://distrelec-ch.local:4200',
    },
  };

  isBrowser(): boolean {
    return true;
  }
}

class MockCmsService {
  getCurrentPage(): Observable<Page> {
    return of(MOCK_CMS_PAGE);
  }
}

class MockAllSiteSettingsService {
  currentChannelData$ = new BehaviorSubject<any>(MOCK_CHANNEL_DATA);
}

class MockPriceService {
  price: Price;

  getProductPrice(): Observable<any> {
    return of(MOCK_PRICE_OBJECT);
  }

  setProductPrice(value: Price): void {
    this.price = value;
  }

  getPriceBasedOnChannel(price: Price, channel: Channel): number {
    if (channel === 'B2B') {
      return price.basePrice;
    }

    return price.priceWithVat;
  }
}

const MOCK_CMS_PAGE = {
  pageId: '',
};

const MOCK_CHANNEL_DATA = {
  channel: 'B2B',
  language: 'en',
  country: 'CH',
  currency: 'CHF',
  domain: 'https://pretest.distrelec.ch',
  mediaDomain: 'https://pretest.media.distrelec.com',
};

const MOCK_ADD_ENTRY_FROM_PDP_EVENT = {
  type: 'update_cart',
  body: {
    action: 'add',
    page_type: 'productDetails',
    web_store_url: 'https://distrelec-ch.local:4200',
    webshop_url_lang: 'https://distrelec-ch.local:4200/en/',
    language: 'en',
    local_currency: 'CHF',
    product_id: '30376587',
    category_id: 'cat-DNAV_PL_140903',
    brand: 'Teledyne LeCroy',
    price: 13917.75,
    currency: 'CHF',
    discount_percentage: 16,
    discount_value: 2058.98,
    original_price: 32943.75,
    product_list: '[{"product_id":"30376587","quantity":1}]',
    product_ids: '["30376587"]',
  },
};

const MOCK_EMPTY_CART_UPDATE_EVENT = {
  type: 'update_cart',
  body: {
    action: 'empty',
    language: 'en',
    local_currency: 'CHF',
    page_type: 'productDetails',
    web_store_url: 'https://distrelec-ch.local:4200',
    webshop_url_lang: 'https://distrelec-ch.local:4200/en/',
  },
};

const MOCK_MISSING_PRICES_UPDATE_EVENT = {
  type: 'update_cart',
  body: {
    action: 'add',
    page_type: 'productDetails',
    web_store_url: 'https://distrelec-ch.local:4200',
    webshop_url_lang: 'https://distrelec-ch.local:4200/en/',
    language: 'en',
    local_currency: 'CHF',
    product_id: '30376587',
    category_id: 'cat-DNAV_PL_140903',
    brand: 'Teledyne LeCroy',
    price: 13917.75,
    currency: 'CHF',
    discount_percentage: 16,
    discount_value: 2058.98,
    original_price: 32943.75,
    product_list: '[{"product_id":"30376587","quantity":1}]',
    product_ids: '["30376587"]',
  },
};

const MOCK_DISCOUNTED_VOLUMEPRICEMAP = [
  {
    basePrice: 20,
    currencyIso: 'CHF',
    formattedValue: '20,00',
    minQuantity: 1,
    originalValue: 23.7,
    pricePerX: 0,
    pricePerXBaseQty: 0,
    pricePerXUOM: 'PC',
    pricePerXUOMDesc: 'piece',
    pricePerXUOMQty: 0,
    priceType: 'BUY',
    priceWithVat: 21.54,
    saving: 16,
    value: 20,
    vatPercentage: 7.7,
    vatValue: 1.54,
  },
];

describe('Service: BloomreachUpdateCart', () => {
  let actions$: Observable<any>;
  let eventService: EventService;
  let priceService: PriceService;
  let bloomreachUpdateCartTracking;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule, StoreModule.forRoot({})],
      providers: [
        provideMockActions(() => actions$),
        { provide: WindowRef, useClass: MockWindowRef },
        { provide: AllsitesettingsService, useClass: MockAllSiteSettingsService },
        { provide: CmsService, useClass: MockCmsService },
        { provide: CartStoreService, useClass: MockCartStoreService },
        { provide: PriceService, useClass: MockPriceService },
      ],
    });

    bloomreachUpdateCartTracking = TestBed.inject(BloomreachUpdateCartTracking);
    eventService = TestBed.inject(EventService);
  });

  it('should send update_cart event when user adds product from cart page', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;
    MOCK_CMS_PAGE.pageId = 'cartPage';

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_CART_ENTRY);

    const MOCK_ADD_ENTRY_FROM_CART_EVENT = {
      ...MOCK_ADD_ENTRY_FROM_PDP_EVENT,
      body: { ...MOCK_ADD_ENTRY_FROM_PDP_EVENT.body, page_type: 'cartPage' },
    };

    const expected = createFrom(BloomreachUpdateCartEvent, MOCK_ADD_ENTRY_FROM_CART_EVENT);
    expect(addProductToCartEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('should send update_cart event when user adds product from PDP', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;
    MOCK_CMS_PAGE.pageId = 'productDetails';

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_CART_ENTRY);

    const expected = createFrom(BloomreachUpdateCartEvent, MOCK_ADD_ENTRY_FROM_PDP_EVENT);

    expect(addProductToCartEvent.body.price).toEqual(13917.75);
    expect(addProductToCartEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('should send update_cart with correct pricing depending on quantity in params', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_CART_ENTRY, 26);

    expect(addProductToCartEvent.body.price).toEqual(13917.75);
    expect(addProductToCartEvent.body.original_price).toEqual(32943.75);
  });

  it('should send update_cart with correct pricing depending on quantity in entry', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;
    const MOCK_HIGHTER_QUANTITY_ENTRY = { ...MOCK_CART_ENTRY, quantity: 51 };

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_HIGHTER_QUANTITY_ENTRY);

    expect(addProductToCartEvent.body.price).toEqual(13917.75);
    expect(addProductToCartEvent.body.original_price).toEqual(32943.75);
  });

  it('should send update_cart with correct discount pricing when product is discounted', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;

    const MOCK_DISCOUNTED_ENTRY = {
      ...MOCK_CART_ENTRY,
      product: {
        ...MOCK_CART_ENTRY.product,
        volumePrices: MOCK_DISCOUNTED_VOLUMEPRICEMAP,
      },
    };

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_DISCOUNTED_ENTRY);

    expect(addProductToCartEvent.body.discount_percentage).toEqual(16);
    expect(addProductToCartEvent.body.discount_value).toEqual(2058.98);
    expect(addProductToCartEvent.body.price).toEqual(13917.75);
    expect(addProductToCartEvent.body.original_price).toEqual(32943.75);
  });

  it('should send update_cart with no price', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;
    MOCK_CMS_PAGE.pageId = 'productDetails';

    const MOCK_MISSING_VOLUMEPRICEMAP_ENTRY = {
      ...MOCK_CART_ENTRY,
      product: {
        ...MOCK_CART_ENTRY.product,
        volumePrices: [],
      },
    };

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('add', MOCK_MISSING_VOLUMEPRICEMAP_ENTRY);

    const expected = createFrom(BloomreachUpdateCartEvent, MOCK_MISSING_PRICES_UPDATE_EVENT);
    expect(addProductToCartEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('should send update_cart with remove action', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackAddOrRemoveCartEvent('remove', MOCK_CART_ENTRY);

    expect(addProductToCartEvent.body.action).toEqual('remove');
  });

  it('should send update_cart with empty action', () => {
    let addProductToCartEvent: BloomreachUpdateCartEvent | undefined;
    MOCK_CMS_PAGE.pageId = 'productDetails';

    eventService.get(BloomreachUpdateCartEvent).subscribe((result) => (addProductToCartEvent = result));

    bloomreachUpdateCartTracking.trackEmptyProductCartEvent();

    const expected = createFrom(BloomreachUpdateCartEvent, MOCK_EMPTY_CART_UPDATE_EVENT);
    expect(addProductToCartEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });
});
