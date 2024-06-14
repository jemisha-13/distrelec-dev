/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Pipe, PipeTransform } from '@angular/core';
import { StockComponent } from '../stock.component';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { SalesStatusService } from '@services/sales-status.service';
import { Translatable, TranslatableParams } from '@spartacus/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { Observable, of } from 'rxjs';
import {
  assignWarehouseData,
  getAvailabilityInStockLabels,
  getAvailabilityOutOfStockLabels,
  resetProductAvailability,
  returnSetOfTranslations,
} from './stock-test-helper';
import {
  MOCK_SALES_DATA_30,
  MOCK_SALES_DATA_40,
  MOCK_SALES_DATA_41,
  MOCK_SALES_DATA_50,
  MOCK_SALES_DATA_52,
  MOCK_SALES_DATA_90,
} from '@features/mocks/mock-sales-status-data';
import {
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BOTH_WAREHOUSES_BTO,
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_BTO,
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_NANIKON_BTO,
  MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK,
  MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_CDC_BTO,
  MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_NANIKON_DIR,
  MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BOTH_WAREHOUSES_BTO,
  MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDC_BTO,
  MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_BTO,
  MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_BTO,
  MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK,
  MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_BTO,
  MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK,
} from '@features/mocks/mock-pdp-availability-BTO-BANCS';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { provideMockActions } from '@ngrx/effects/testing';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { processStockData } from '@features/pages/product/pdp/price-and-stock/stock/stock-helper';

@Pipe({
  name: 'cxTranslate',
})
class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return returnSetOfTranslations(input, options);
  }
}

describe('StockComponent BTO/BANCS', () => {
  let actions$: Observable<any>;
  let component: StockComponent;
  let fixture: ComponentFixture<StockComponent>;
  let countryService: CountryService;
  let salesStatus: SalesStatusService;
  let productDataService: ProductDataService;

  beforeEach(() => {
    countryService = jasmine.createSpyObj('CountryService', ['getActive']);
    salesStatus = jasmine.createSpyObj('SalesStatusService', ['getSalesStatusConfiguration']);
    productDataService = jasmine.createSpyObj('ProductDataService', [
      'setIsProductNotBuyable',
      'getIsProductNotBuyable',
    ]);

    (countryService.getActive as jasmine.Spy).and.returnValue(of('CH'));

    TestBed.configureTestingModule({
      imports: [CommonTestingModule, DistIconModule],
      declarations: [StockComponent, MockTranslatePipe],
      providers: [
        provideMockActions(() => actions$),
        { provide: CountryService, useValue: countryService },
        { provide: SalesStatusService, useValue: salesStatus },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    component = resetProductAvailability(component);
  });

  it('should show availability messaging for status 30 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      noStockHeadline,
      iconStatus30or31,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockBTOorDIRComingSoon,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_status_30_31_icon');
    expect(noStockBTOorDIR30or31.innerHTML).toContain('xInStock');
    expect(noBTOStockHeadline.innerText).toBe('Available to backorder');
    expect(noStockBTOtext.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH IN stock BOTH warehouses', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BOTH_WAREHOUSES_BTO.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BOTH_WAREHOUSES_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('6 Stock will be available in 57* day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for LI IN stock BOTH warehouses', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BOTH_WAREHOUSES_BTO.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BOTH_WAREHOUSES_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('6 Stock will be available in 57* day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH IN stock NANIKON', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_NANIKON_BTO.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_NANIKON_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('2 Stock will be available in 35* day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for DE IN stock NANIKON', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_NANIKON_DIR.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_NANIKON_DIR);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      noStockHeadline,
      iconStatus30or31,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockBTOorDIRComingSoon,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_status_30_31_icon');
    expect(noStockBTOorDIR30or31.innerHTML).toContain('xInStock');
    expect(noBTOStockHeadline.innerText).toBe('Available to backorder');
    expect(noStockBTOtext.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH IN stock CDC', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_BTO.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('1 Stock will be available in 57* day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for DE IN stock CDC', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_CDC_BTO.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_DE_IN_STOCK_CDC_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('1 Stock will be available in 57 * day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 40 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('40').and.returnValue(MOCK_SALES_DATA_40);

    component.salesData = MOCK_SALES_DATA_40;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_BTO.res;
    component.salesStatus = '40';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      noStockHeadline,
      iconStatus30or31,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockFurtherAdditionaltext,
      noStockBTOtext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockBTOorDIR30or31,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_coming_soon_icon');
    expect(noStockBTOorDIRComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noBTOStockHeadline.innerText).toBe('No longer available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 40 for CH IN stock CDC', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('40').and.returnValue(MOCK_SALES_DATA_40);

    component.salesData = MOCK_SALES_DATA_40;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDC_BTO.res;
    component.salesStatus = '40';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDC_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('shippingAvailableInFuture');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockShippingAvailable?.innerText).toContain('2 Stock will be available in 49* day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change. The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 41 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('41').and.returnValue(MOCK_SALES_DATA_41);

    component.salesData = MOCK_SALES_DATA_41;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_BTO.res;
    component.salesStatus = '41';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      noStockHeadline,
      iconStatus30or31,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockFurtherAdditionaltext,
      noStockBTOtext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockBTOorDIR30or31,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_coming_soon_icon');
    expect(noStockBTOorDIRComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noBTOStockHeadline.innerText).toBe('No longer available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 50 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('50').and.returnValue(MOCK_SALES_DATA_50);

    component.salesData = MOCK_SALES_DATA_50;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK.res;
    component.salesStatus = '50';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      iconStatus30or31,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockBTOorDIR21or22,
      noStockFurtherAdditionaltext,
      noStockBTOtext,
      noBTOStockHeadline,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockBTOorDIR30or31,
    ];

    expect(noStockHeadline.innerHTML).toContain('pdp_noStock_coming_soon_icon');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 52 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('52').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_52;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_BTO.res;
    component.salesStatus = '52';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_BTO);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      iconStatus30or31,
      noStockBTOorDIR30or31,
      iconComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockBTOorDIR21or22,
      noStockFurtherAdditionaltext,
      noStockBTOtext,
      noStockHeadline,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockBTOorDIR30or31,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_coming_soon_icon');
    expect(noStockBTOorDIRComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noBTOStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 90 for CH NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('90').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_90;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK.res;
    component.salesStatus = '90';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noBTOStockHeadline,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      notifyStockButton,
      iconStatus30or31,
      noStockBTOorDIR30or31,
      iconComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockBTOorDIR21or22,
      noStockFurtherAdditionaltext,
      noStockBTOtext,
      noStockHeadline,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockBTOorDIR30or31,
    ];

    expect(noBTOStockHeadline.innerHTML).toContain('pdp_noStock_bto_dir_coming_soon_icon');
    expect(noStockBTOorDIRComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noBTOStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });
});
