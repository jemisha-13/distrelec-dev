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
import { MOCK_SALES_DATA_30 } from '@features/mocks/mock-sales-status-data';
import {
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BTRNORM,
  MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_BTRNORM,
  MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BTRNORM,
  MOCK_AVAILABILITY_STATUS_30_NL_IN_STOCK_BTRNORM,
  MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM,
} from '@features/mocks/mock-pdp-availability-BTR-NORMAL';
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

describe('StockComponent BTR/WALDOM', () => {
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

  it('should show availability messaging for status 30 for CH BTR Products NO stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_BTRNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      noStockStatus30sText,
      iconStatus20or21,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noBTOStockHeadline,
      noStockStatus20sText,
      noStockStatus30sWaldomText,
    ];

    expect(iconStatus30or31.innerHTML).toContain('xInStock');

    expect(noStockStatus30sBtoText.innerText).toBe('Available to backorder');
    expect(noStockReplenishDeliveryPickupText.innerText).toBe('Additional stock will be available in 28 days');
    expect(notifyStockButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH BTR Products IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_BTRNORM);
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
      salesDataBTOText,
    ];

    expect(stockLevelAvailable.innerText).toBe('2100 In stock - delivered in 3 business day(s)');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(waldomDeliveryTime?.innerHTML).toContain(
      'The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for LI BTR Products IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_BTRNORM);
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
      salesDataBTOText,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('2100 In stock - delivered in 3 business day(s)');
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(waldomDeliveryTime?.innerHTML).toContain(
      'The exact delivery date will be stated in the order confirmation.',
    );
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for NORWAY BTR Products IN stock', (done) => {
    (countryService.getActive as jasmine.Spy).and.returnValue(of('NO'));

    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM);
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
      waldomDeliveryTime,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('2100 In stock - delivered in 3 business day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change.',
    );
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for SWEDEN BTR Products IN stock', (done) => {
    (countryService.getActive as jasmine.Spy).and.returnValue(of('SE'));

    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM);
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
      waldomDeliveryTime,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('2100 In stock - delivered in 3 business day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change.',
    );
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for FINLAND BTR Products IN stock', (done) => {
    (countryService.getActive as jasmine.Spy).and.returnValue(of('FI'));

    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_NO_IN_STOCK_BTRNORM);
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
      waldomDeliveryTime,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('2100 In stock - delivered in 3 business day(s)');
    expect(salesDataBTOText?.innerText).toBe(
      'Delivery times in accordance with supplier guidelines, subject to change.',
    );
    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30, for NL BTR Products dont show availability message', (done) => {
    (countryService.getActive as jasmine.Spy).and.returnValue(of('NL'));

    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_NL_IN_STOCK_BTRNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_NL_IN_STOCK_BTRNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      stockLevelAvailable,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
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
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      additionalStock7371,
      withoutStockButFurtherStock,
      moreStockAvailable,
      additionalStock7374,
      withStockShippingAvailable,
    ];

    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    expect(stockLevelAvailable.innerHTML).toContain('8248 In stock - delivered in 3 business day(s)');

    done();
  });
});
