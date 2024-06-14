/* eslint-disable @typescript-eslint/no-unused-vars */
import { TestBed } from '@angular/core/testing';
import { CartProductAvailabilityComponent } from './cart-product-availability.component';
import { DistCartService } from '@services/cart.service';
import { SiteIdEnum } from '@model/site-settings.model';
import { ProductAvailability, StockLevel, StockLevelPickup } from '@model/product-availability.model';
import { OrderEntry } from '@spartacus/cart/base/root';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('CartProductAvailabilityComponent', () => {
  let component;

  class MockDistCartService {
    activeSiteId = SiteIdEnum.CH;
  }

  const stockLevel: StockLevel = {
    available: 9,
    deliveryTime: '> 10d',
    external: false,
    fast: false,
    mview: 'BTR2',
    replenishmentDeliveryTime: null,
    replenishmentDeliveryTime2: null,
    waldom: false,
    warehouseId: '7371',
    leadTime: '1',
  };

  const pickupStock: StockLevelPickup = {
    stockLevel: 1,
    warehouseCode: '7371',
    warehouseName: 'Nanikon',
  };

  const availability: ProductAvailability = {
    backorderDeliveryDate: null,
    backorderQuantity: 1,
    deliveryTimeBackorder: '> 10d',
    detailInfo: true,
    leadTimeErp: 1,
    productCode: '300-12-123',
    requestedQuantity: 10,
    statusLabel: 'status',
    stockLevelPickup: [pickupStock],
    stockLevelTotal: 9,
    stockLevels: [stockLevel],
  };

  const product: any = {
    calibrated: false,
    itemCategoryGroup: 'category',
    baseOptions: [],
    buyable: true,
    categories: [],
    code: 'product',
    codeErpRelevant: 'procudeERP',
    configurable: true,
    customsCode: 'customCode',
    dimensions: 'dim',
    distManufacturer: null,
    elfaArticleNumber: 'elfaArti',
    eligibleForReevoo: true,
    enumber: 'enu',
    grossWeight: '1',
    grossWeightUnit: 'kg',
    images: null,
    movexArticleNumber: 'movex',
    name: 'productName',
    navisionArticleNumber: 'navison',
    orderQuantityMinimum: 1,
    orderQuantityStep: 1,
    originalPackSize: 'string',
    productFamilyName: 'string',
    productImages: [],
    purchasable: true,
    replacementReason: 'reason',
    rohs: 'svhc',
    rohsCode: '21',
    salesStatus: '30',
    stock: null,
    salesUnit: 'pc',
    technicalAttributes: [],
    transportGroupData: null,
    typeName: 'type',
    url: 'url',
    volumePrices: [],
    energyEfficiencyLabelImage: {},
  };

  const cartEntry: OrderEntry = {
    type: 'entry',
    alternateAvailable: true,
    alternateQuantity: 1,
    availabilities: [],
    backOrderProfitable: true,
    backOrderedQuantity: 0,
    baseListPrice: null,
    basePrice: null,
    bom: false,
    cancellableQuantity: 1,
    configurationInfos: [],
    customerReference: 'ref',
    deliveryQuantity: 1,
    dummyItem: false,
    entryNumber: 1,
    isBackOrder: true,
    mandatoryItem: true,
    pendingQuantity: 1,
    product,
    quantity: 1,
    quotationReference: 'ref',
    returnableQuantity: 1,
    statusSummaryList: [],
    taxValue: 1,
    totalListPrice: null,
    totalPrice: null,
    isQuotation: false,
    moqAdjusted: false,
    stepAdjusted: false,
    quotationId: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [CartProductAvailabilityComponent, { provide: DistCartService, useClass: MockDistCartService }],
    });
    component = TestBed.inject(CartProductAvailabilityComponent);
    component.cartItem = cartEntry;
    component.productAvailability = availability;
  });

  it('should create', () => {
    component.ngOnInit();
    expect(component).toBeTruthy();
  });

  it('should be nanikon warehouse', () => {
    component.ngOnInit();

    expect(component.isWarehouse7371).toBeTrue();
  });

  it('should be pickup in nanikon warehouse', () => {
    component.ngOnInit();

    expect(component.isWarehouse7371).toBeTrue();
    expect(component.showPickup).toBeTrue();
    expect(component.showPickupValue).toEqual(1);
  });

  it('should be coming soon', () => {
    //given
    availability.stockLevelTotal = 0;

    //when
    component.ngOnInit();

    //then
    expect(component.showComingSoon).toBeTrue();
  });
});
