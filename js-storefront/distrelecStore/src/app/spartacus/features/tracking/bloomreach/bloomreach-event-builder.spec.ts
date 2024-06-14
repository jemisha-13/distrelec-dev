/* eslint-disable @typescript-eslint/naming-convention */
import { TestBed } from '@angular/core/testing';
import { StoreModule } from '@ngrx/store';
import {
  AuthService,
  CmsService,
  createFrom,
  EventService,
  Occ,
  PageMeta,
  PageMetaService,
  PageType,
  ProductService,
  TranslationService,
  UserIdService,
  WindowRef,
} from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { NavigationEvent } from '@spartacus/storefront';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { provideMockActions } from '@ngrx/effects/testing';
import { take } from 'rxjs/operators';
import { BloomreachEventBuilder } from './bloomreach-event-builder.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BloomreachProductFamilyViewEvent } from '../events/bloomreach/bloomreach-product-family-view-event';
import { BloomreachManufacturerViewEvent } from '../events/bloomreach/bloomreach-manufacturer-view-event';
import { ManufactureService } from '@services/manufacture.service';
import { MockManufacturerService } from '@features/mocks/mock-manufacturer.service';
import { DistrelecUserService } from '@services/user.service';
import { BloomreachCustomerEvent } from '@features/tracking/events/bloomreach/bloomreach-customer-event';
import { MockIdentifyUserService } from '@features/mocks/mock-identify-user.service';
import { BloomreachCategoryViewEvent } from '@features/tracking/events/bloomreach/bloomreach-category-view-event';
import { CategoriesService } from '@services/categories.service';
import { MockCategoryService } from '@features/mocks/mock-category.service';
import { BloomreachLogoutEvent } from '../events/bloomreach/bloomreach-logout-event';
import { ProductFamilyService } from '@features/pages/product/core/services/product-family.service';
import { BloomreachPlpViewEvent } from '../events/bloomreach/bloomreach-plp-view-event';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { PriceService } from '@services/price.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { BloomreachPdpViewEvent } from '../events/bloomreach/bloomreach-pdp-view-event';
import { MOCK_VIEW_ITEM_EVENT } from '@features/mocks/mock-view-item-event';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { ViewItemEvent } from '@features/tracking/events/view-item-event';
import { BLOOMREACH_DATA } from '@features/mocks/events/mock-bloomreach-data';

const MOCK_MANUFACTURER_VIEW_EVENT: BloomreachManufacturerViewEvent = {
  type: 'manufacturer_view',
  body: {
    manufacturer_url: 'https://distrelec-ch.local:4200/en/manufacturer/tektronix/man_tek',
    manufacturer_id: '/manufacturer/tektronix/man_tek',
    manufacturer_brand: 'man_tek',
  },
};

const MOCK_PRODUCT_FAMILY_VIEW_EVENT: BloomreachProductFamilyViewEvent = {
  type: 'product_family_view',
  body: {
    product_family_url: 'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701',
    product_family_id: '1624701',
    product_family_manufacturer: 'InfiniiVision 1000 X‑Series Oscilloscopes',
  },
};

const MOCK_PRODUCT_FAMILY_DATA = {
  name: 'InfiniiVision 1000 X‑Series Oscilloscopes',
  code: '1624701',
};

const MOCK_IDENTIFY_USER_EVENT: BloomreachCustomerEvent = {
  type: 'customer',
  email_id: 'test@test.com',
  erp_contact_id: '544545',
};

const MOCK_LOGOUT_EVENT: BloomreachLogoutEvent = {
  type: 'logout',
};

const MockAuthService = {
  logoutInProgress$: new BehaviorSubject<boolean>(true),
};

const MOCK_CATEGORY_VIEW_EVENT: BloomreachCategoryViewEvent = {
  type: 'category',
  body: {
    category_name: 'Automation',
    category_url: 'https://distrelec-ch.local:4200/en/manufacturer/tektronix/man_tek',
    category_id: 'cat-L1D_379516',
    category_path: 'Home Automation',
  },
};

const MOCK_CATEGORY_PLP: BloomreachCategoryViewEvent = {
  type: 'category',
  body: {
    category_name: 'Automation',
    category_url: 'https://distrelec-ch.local:4200/en/optoelectronics/leds/c/cat-L3D_525297?useTechnicalView=true',
    category_id: 'cat-L1D_379516',
    category_path: 'Home Automation',
  },
};

const MockProductFamilyService = {
  familyData$: new BehaviorSubject<any>(MOCK_PRODUCT_FAMILY_DATA),

  getCurrentFamilyData(): Observable<any> {
    return of(MOCK_PRODUCT_FAMILY_DATA);
  },
};

const MockWindowRef = {
  nativeWindow: {
    location: { href: 'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701' },
  },
  isBrowser(): boolean {
    return true;
  },
};

const mockPageMeta = {
  title: 'Automation Online Shop - Distrelec Switzerland',
  breadcrumbs: [
    {
      label: 'Home',
      link: '/',
    },
    {
      label: 'Automation',
      link: '/automation/c/cat-L1D_379516',
    },
  ],
  description:
    'Automation order online from Distrelec Switzerland: ✓ Free shipping from 50€ ✓ over 150,000 products in stock ✓ Pay per invoice.',
  image: '/Web/WebShopImages/portrait_medium/ti/on/L1D_379516_automation.jpg',
  robots: ['INDEX', 'FOLLOW'],
  canonicalUrl: 'https://pretest.distrelec.ch/en/automation/c/cat-L1D_379516',
};

const MockPageMetaService = {
  getMeta(): Observable<any | null> {
    return of(mockPageMeta);
  },
};

const MockTranslationService = {
  translate(key: string): Observable<string> {
    return of('Home');
  },
};

class MockProductService {
  get(): Observable<any> {
    return of(MOCK_PRODUCT_DATA);
  }
}

class MockUserIdService {
  takeUserId(loggedIn?: boolean): Observable<string> {
    return of('false');
  }
}

class MockPriceService {
  getPrices() {
    return of({
      price: { basePrice: 200 },
      volumePrices: [],
      volumePricesMap: [],
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

const MOCK_PRODUCT_DATA = {
  details: '',
  code: '30099461',
  distManufacturer: {
    name: 'RND Lab',
  },
  typeName: 'RND 510-00003',
  name: 'Key Ring Torch, LED, 1x AAA, 20lm, 25m, IPX4, Black',
  price: {
    basePrice: 7.05,
    currencyIso: 'CHF',
    formattedValue: '7,05',
    priceWithVat: 7.593,
    value: 7.05,
    vatValue: 0.543,
  },
  stock: {
    isValueRounded: false,
    stockLevel: 0,
    stockLevelStatus: 'inStock',
  },
  images: [
    {},
    {},
    {
      format: 'landscape_small',
      url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/1-/01/RND%20510-00003_30099461-01.jpg',
    },
  ],
};

const MOCK_PRODUCT_AVAILABILITY = {
  backorderQuantity: 18,
  deliveryTimeBackorder: '> 10 d',
  detailInfo: true,
  leadTimeErp: 2,
  productCode: '30099461',
  requestedQuantity: 1,
  statusLabel: '> 10 d',
  stockLevelPickup: [
    {
      stockLevel: 0,
      warehouseCode: '7374',
      warehouseName: 'Distrelec Schweiz AG',
    },
  ],
  stockLevelTotal: 0,
  stockLevels: [
    {
      available: 0,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'SC',
      replenishmentDeliveryTime: '3 ',
      replenishmentDeliveryTime2: '220 ',
      waldom: false,
      warehouseId: '7371',
    },
    {
      available: 0,
      deliveryTime: '',
      external: false,
      fast: false,
      mview: 'SC',
      replenishmentDeliveryTime: '1 ',
      replenishmentDeliveryTime2: '0',
      waldom: false,
      warehouseId: '7374',
    },
  ],
};

const MOCK_CHANNEL_DATA: CurrentSiteSettings = {
  channel: 'B2B',
  //@ts-ignore
  country: 'ch',
  currency: 'CH',
  language: 'en',
  domain: 'distrelec_CH',
  mediaDomain: 'distrelec_CH',
};

const MOCK_PDP_VIEW_EVENT: BloomreachPdpViewEvent = {
  type: 'pdp_view',
  body: {
    product_id: '30099461',
    pdp_url: 'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701',
    pdp_brand: 'RND Lab',
    pdp_mpn: 'RND 510-00003',
    pdp_product_title:
      'RND 510-00003-Industrial Ethernet Cable and Network Tester, LinkIQ, 10Gbps, RJ45 / USB-C,RND Lab',
    pdp_price: 50000,
    pdp_currency: 'CHF',
    pdp_image_url: { url: { url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/1-/01/RND%20510-00003_30099461-01.jpg'} },
    pdp_stock: 0,
  },
};

describe('BloomreachEventBuilder', () => {
  let actions$: Observable<any>;
  let eventService: EventService;
  let bloomreachEventBuilder: BloomreachEventBuilder;
  let identifyCustomerEvent: BloomreachCustomerEvent | undefined;
  let manufacturerEvent: BloomreachManufacturerViewEvent | undefined;
  let logoutEvent: BloomreachLogoutEvent | undefined;
  let categoryEvent: BloomreachCategoryViewEvent | BloomreachPlpViewEvent | undefined;
  let allSiteSettingsService: AllsitesettingsService;
  let priceService: PriceService;
  let productAvailabilityService: ProductAvailabilityService;

  beforeEach(() => {
    allSiteSettingsService = jasmine.createSpyObj('AllsitesettingsService', ['getCurrentChannelData']);
    priceService = jasmine.createSpyObj('PriceService', ['getPriceWithoutVatForB2B', 'getPriceWithVatForB2C']);
    productAvailabilityService = jasmine.createSpyObj('ProductAvailabilityService', ['getAvailability']);

    (productAvailabilityService.getAvailability as jasmine.Spy).and.returnValue(of(MOCK_PRODUCT_AVAILABILITY));

    TestBed.configureTestingModule({
      imports: [CommonTestingModule, HttpClientTestingModule, StoreModule.forRoot({})],
      providers: [
        provideMockActions(() => actions$),
        { provide: WindowRef, useValue: MockWindowRef },
        { provide: ProductFamilyService, useValue: MockProductFamilyService },
        { provide: ManufactureService, useValue: MockManufacturerService },
        { provide: DistrelecUserService, useValue: MockIdentifyUserService },
        { provide: AuthService, useValue: MockAuthService },
        { provide: CategoriesService, useValue: MockCategoryService },
        { provide: PageMetaService, useValue: MockPageMetaService },
        { provide: TranslationService, useValue: MockTranslationService },
        { provide: ProductService, useClass: MockProductService },
        { provide: AllsitesettingsService, useValue: allSiteSettingsService },
        { provide: ProductAvailabilityService, useValue: productAvailabilityService },
        { provide: UserIdService, useClass: MockUserIdService },
        { provide: PriceService, useClass: MockPriceService },
      ],
    });

    bloomreachEventBuilder = TestBed.inject(BloomreachEventBuilder);
    eventService = TestBed.inject(EventService);

    bloomreachEventBuilder.registerEvents();
  });

  it('should send productFamily view event on product family page', () => {
    let productFamilyEvent: BloomreachProductFamilyViewEvent | undefined;

    const navigationEvent: NavigationEvent = createFrom(NavigationEvent, {
      context: { id: '/infiniivision-1000-series-oscilloscopes/pf/1624701', type: PageType.CONTENT_PAGE },
      params: { productCode: '30099461' },
      semanticRoute: 'productFamily',
      url: 'infiniivision-1000-series-oscilloscopes',
    });

    MockWindowRef.nativeWindow.location.href =
      'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701';

    bloomreachEventBuilder
      .buildProductFamilyViewEvent()
      .pipe(take(1))
      .subscribe((result) => (productFamilyEvent = result));

    eventService.dispatch(navigationEvent);

    const expected = createFrom(BloomreachProductFamilyViewEvent, MOCK_PRODUCT_FAMILY_VIEW_EVENT);
    expect(productFamilyEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('should send manufacturer view event on manufacturer page', () => {
    const navigationEvent: NavigationEvent = createFrom(NavigationEvent, {
      context: { id: '/manufacturer/tektronix/man_tek', type: PageType.CONTENT_PAGE },
      params: { manufacturerCode: '/manufacturer/tektronix/man_tek' },
      semanticRoute: 'manufacturer',
      url: 'en/manufacturer/tektronix/man_tek',
    });

    MockWindowRef.nativeWindow.location.href = 'https://distrelec-ch.local:4200/en/manufacturer/tektronix/man_tek';

    bloomreachEventBuilder
      .buildManufacturerPageViewEvent()
      .pipe(take(1))
      .subscribe((value) => (manufacturerEvent = value));

    eventService.dispatch(navigationEvent);
    const expected = createFrom(BloomreachManufacturerViewEvent, MOCK_MANUFACTURER_VIEW_EVENT);
    expect(manufacturerEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('Should send category view event', () => {
    const navigationEvent: NavigationEvent = createFrom(NavigationEvent, {
      context: { id: 'cat-L1D_379516', type: PageType.CATEGORY_PAGE },
      params: { categoryCode: 'cat-L1D_379516', param0: 'automation' },
      semanticRoute: 'category',
      url: 'en/automation/c/cat-L1D_379516',
    });

    MockWindowRef.nativeWindow.location.href = 'https://distrelec-ch.local:4200/en/manufacturer/tektronix/man_tek';

    bloomreachEventBuilder
      .buildCategoryViewEvent()
      .pipe(take(1))
      .subscribe((value) => (categoryEvent = value));

    eventService.dispatch(navigationEvent);

    const expected = createFrom(BloomreachCategoryViewEvent, MOCK_CATEGORY_VIEW_EVENT);
    expect(categoryEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('Should not send category view event if it is a PLP page', () => {
    const plpNavigationEvent: NavigationEvent = createFrom(NavigationEvent, {
      context: { id: 'cat-L3D_525297', type: PageType.CATEGORY_PAGE },
      params: { categoryCode: 'cat-L3D_525297', param0: 'automation' },
      semanticRoute: 'plp',
      url: 'en/optoelectronics/leds/c/cat-L3D_525297?useTechnicalView=true',
    });

    MockWindowRef.nativeWindow.location.href =
      'https://distrelec-ch.local:4200/en/optoelectronics/leds/c/cat-L3D_525297?useTechnicalView=true';

    bloomreachEventBuilder
      .buildCategoryViewEvent()
      .pipe(take(1))
      .subscribe((value) => (categoryEvent = value));

    eventService.dispatch(plpNavigationEvent);

    expect(categoryEvent).toEqual(undefined);
  });

  it('Should send bloomreach identify user event', () => {
    bloomreachEventBuilder
      .buildIdenityCustomerEvent()
      .pipe(take(1))
      .subscribe((result) => (identifyCustomerEvent = result));

    expect(identifyCustomerEvent).toEqual(jasmine.objectContaining({ ...MOCK_IDENTIFY_USER_EVENT }));
  });

  it('Should send bloomreach logout event', () => {
    const expected = createFrom(BloomreachLogoutEvent, MOCK_LOGOUT_EVENT);

    bloomreachEventBuilder
      .buildLogoutEvent()
      .pipe(take(1))
      .subscribe((result) => (logoutEvent = result));

    expect(logoutEvent).toEqual(jasmine.objectContaining({ ...expected }));
  });

  let pdpEventResult: BloomreachPdpViewEvent | undefined;

  const productNavigationEvent: NavigationEvent = createFrom(NavigationEvent, {
    context: { id: '30099461', type: PageType.PRODUCT_PAGE },
    params: { productCode: '30099461' },
    semanticRoute: 'product',
    url: 'en/p/30099461',
  });

  it('should display price without vat BloomreachPdpViewEvent when user is B2B', (done) => {
    let pdpEventResult: BloomreachPdpViewEvent | undefined;

    const viewItemEvent: ViewItemEvent = createFrom(ViewItemEvent, {
      product: BLOOMREACH_DATA,
    });

    MockWindowRef.nativeWindow.location.href =
      'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701';

    (allSiteSettingsService.getCurrentChannelData as jasmine.Spy).and.returnValue(of(MOCK_CHANNEL_DATA));
    (priceService.getPriceWithVatForB2C as jasmine.Spy).and.returnValue(7.593);
    (priceService.getPriceWithoutVatForB2B as jasmine.Spy).and.returnValue(7.05);

    MockWindowRef.nativeWindow.location.href =
      'https://pretest.distrelec.ch/en/key-ring-torch-led-1x-aaa-20lm-25m-ipx4-black-rnd-lab-rnd-510-00003/p/30099461';

    bloomreachEventBuilder
      .buildPdpViewEvent()
      .pipe(take(1))
      .subscribe((value) => {
        pdpEventResult = value;

        const expected = createFrom(BloomreachPdpViewEvent, MOCK_PDP_VIEW_EVENT);
        expect(pdpEventResult).toEqual(jasmine.objectContaining({ ...expected }));
        done();
      });

    eventService.dispatch(viewItemEvent);
  });

  it('should display price with vat for BloomreachPdpViewEvent when user is B2C', (done) => {
    let pdpEventResult: BloomreachPdpViewEvent | undefined;

    const viewItemEvent: ViewItemEvent = createFrom(ViewItemEvent, {
      product: BLOOMREACH_DATA,
    });

    MockWindowRef.nativeWindow.location.href =
      'https://distrelec-ch.local:4200/en/infiniivision-1000-series-oscilloscopes/pf/1624701';

    const MOCK_CHANNEL_DATA_B2C = { ...MOCK_CHANNEL_DATA, channel: 'B2C' };
    (allSiteSettingsService.getCurrentChannelData as jasmine.Spy).and.returnValue(of(MOCK_CHANNEL_DATA_B2C));
    (priceService.getPriceWithVatForB2C as jasmine.Spy).and.returnValue(7.593);
    (priceService.getPriceWithoutVatForB2B as jasmine.Spy).and.returnValue(7.05);

    MockWindowRef.nativeWindow.location.href =
      'https://pretest.distrelec.ch/en/key-ring-torch-led-1x-aaa-20lm-25m-ipx4-black-rnd-lab-rnd-510-00003/p/30099461';

    bloomreachEventBuilder
      .buildPdpViewEvent()
      .pipe(take(1))
      .subscribe((value) => {
        pdpEventResult = value;

        const MOCK_PDP_VIEW_EVENT_B2C = {
          type: MOCK_PDP_VIEW_EVENT.type,
          body: { ...MOCK_PDP_VIEW_EVENT.body, pdp_price: 50000 },
        };
        const expected = createFrom(BloomreachPdpViewEvent, MOCK_PDP_VIEW_EVENT_B2C);
        expect(pdpEventResult).toEqual(jasmine.objectContaining({ ...expected }));
        done();
      });

    eventService.dispatch(viewItemEvent);
  });

  it('should send BloomreachPdpViewEvent when image is missing', () => {
    (allSiteSettingsService.getCurrentChannelData as jasmine.Spy).and.returnValue(of(MOCK_CHANNEL_DATA));
    (priceService.getPriceWithVatForB2C as jasmine.Spy).and.returnValue(7.593);
    (priceService.getPriceWithoutVatForB2B as jasmine.Spy).and.returnValue(7.05);
    MOCK_PRODUCT_DATA.images = [];

    MockWindowRef.nativeWindow.location.href =
      'https://pretest.distrelec.ch/en/key-ring-torch-led-1x-aaa-20lm-25m-ipx4-black-rnd-lab-rnd-510-00003/p/30099461';

    bloomreachEventBuilder
      .buildPdpViewEvent()
      .pipe(take(1))
      .subscribe((value) => (pdpEventResult = value));

    const vieItemNoImage = createFrom(ViewItemEvent, {
      ...MOCK_VIEW_ITEM_EVENT,
      //@ts-ignore
      product: { ...MOCK_VIEW_ITEM_EVENT.product, images: [] },
    });
    eventService.dispatch(vieItemNoImage);

    const expected = createFrom(BloomreachPdpViewEvent, MOCK_PDP_VIEW_EVENT);
    expected.body.pdp_image_url = undefined;
    expect(pdpEventResult).toEqual(jasmine.objectContaining({ ...expected }));
  });

  it('should not send Bloomreach event on other pages than product', () => {
    let resultUndefined: BloomreachPdpViewEvent | undefined;

    (allSiteSettingsService.getCurrentChannelData as jasmine.Spy).and.returnValue(of(MOCK_CHANNEL_DATA));
    (priceService.getPriceWithVatForB2C as jasmine.Spy).and.returnValue(7.593);
    (priceService.getPriceWithoutVatForB2B as jasmine.Spy).and.returnValue(7.05);

    const navigationEventCategories = { ...productNavigationEvent, semanticRoute: 'categories' };

    bloomreachEventBuilder
      .buildPdpViewEvent()
      .pipe(take(1))
      .subscribe((value) => (resultUndefined = value));

    eventService.dispatch(navigationEventCategories);
    expect(resultUndefined).toEqual(undefined);
  });
});
