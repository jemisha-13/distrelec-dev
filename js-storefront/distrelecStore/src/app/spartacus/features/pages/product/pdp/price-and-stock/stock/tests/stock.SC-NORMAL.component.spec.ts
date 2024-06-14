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
  MOCK_AVAILABILITY_STATUS_20_IN_STOCK_BOTH_WAREHOUSES_CH,
  MOCK_AVAILABILITY_STATUS_20_IN_STOCK_CH,
  MOCK_AVAILABILITY_STATUS_20_NO_STOCK_CH,
  MOCK_AVAILABILITY_STATUS_21_NO_STOCK_CH,
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_ONLY_SCNORM,
  MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_CDC_ONLY_SCNORM,
  MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_31_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_31_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDCONLY_SCNORM,
  MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_CDCONLY_SCNORM,
  MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_50_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_50_DE_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_51_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_51_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_ALL_WAREHOUSES_SCNORM,
  MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_CDC_ONLY_SCNORM,
  MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_52_DE_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_53_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_53_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_60_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_61_CH_NO_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_90_CH_IN_STOCK_SCNORM,
  MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK_SCNORM,
} from '@features/mocks/mock-pdp-availability-SC-NORMAL';
import {
  MOCK_SALES_DATA_20,
  MOCK_SALES_DATA_21,
  MOCK_SALES_DATA_30,
  MOCK_SALES_DATA_31,
  MOCK_SALES_DATA_40,
  MOCK_SALES_DATA_41,
  MOCK_SALES_DATA_50,
  MOCK_SALES_DATA_51,
  MOCK_SALES_DATA_52,
  MOCK_SALES_DATA_53,
  MOCK_SALES_DATA_60,
  MOCK_SALES_DATA_61,
  MOCK_SALES_DATA_90,
} from '@features/mocks/mock-sales-status-data';
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

describe('StockComponent SC/NORM', () => {
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

  it('should show availability messaging for status 20 for CH NOT stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('20').and.returnValue(MOCK_SALES_DATA_20);

    component.salesData = MOCK_SALES_DATA_20;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_20_NO_STOCK_CH.res;
    component.salesStatus = '20';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_20_NO_STOCK_CH);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      noStockStatus20sText,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconComingSoon,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
    ];

    expect(noStockHeadline.innerHTML).toContain('pdp_noStock_status_21_20_icon');
    expect(noStockHeadline.innerHTML).toContain('fa-clock-icon');
    expect(iconStatus20or21.innerHTML).toContain('iconClock');
    expect(noStockStatus20sText.innerText).toBe('Coming soon');
    expect(notifyStockButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 20 for CH IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('20').and.returnValue(MOCK_SALES_DATA_20);

    component.salesData = MOCK_SALES_DATA_20;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_20_IN_STOCK_CH.res;
    component.salesStatus = '20';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_20_IN_STOCK_CH);
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
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      withStockAvailableCHText,
      withStockAvailableText,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockAvailableText,
      withStockIconCheck,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('pdp_stock_headline_clock_icon');
    expect(withStockIconClock.innerHTML).toContain('iconClock');
    expect(withStockAvailableCHText.innerText).toBe('Coming soon');
    expect(withStockNotifyButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 20 for CH IN stock both warehouses', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('20').and.returnValue(MOCK_SALES_DATA_20);

    component.salesData = MOCK_SALES_DATA_20;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_20_IN_STOCK_BOTH_WAREHOUSES_CH.res;
    component.salesStatus = '20';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_20_IN_STOCK_BOTH_WAREHOUSES_CH);
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
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      withStockAvailableCHText,
      withStockAvailableText,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockAvailableText,
      withStockIconCheck,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerHTML).toContain('pdp_stock_headline_clock_icon');
    expect(withStockIconClock.innerHTML).toContain('iconClock');
    expect(withStockAvailableCHText.innerText).toBe('Coming soon');
    expect(withStockNotifyButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 21 for CH NOT stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('21').and.returnValue(MOCK_SALES_DATA_21);

    component.salesData = MOCK_SALES_DATA_21;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_21_NO_STOCK_CH.res;
    component.salesStatus = '21';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_21_NO_STOCK_CH);
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
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconComingSoon,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noBTOStockHeadline,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
    ];

    expect(noStockHeadline.innerHTML).toContain('pdp_noStock_status_21_20_icon');
    expect(noStockHeadline.innerHTML).toContain('fa-clock-icon');
    expect(iconStatus20or21.innerHTML).toContain('iconClock');
    expect(noStockStatus20sText.innerText).toBe('Pre-order now');
    expect(notifyStockButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH NOT stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_NO_STOCK_SCNORM);
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
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      iconStatus20or21,
      iconComingSoon,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noStockReplenishDeliveryPickupText,
      noStockStatus30sText,
      noBTOStockHeadline,
      noStockStatus20sText,
      noStockStatus30sWaldomText,
    ];

    expect(noStockHeadline.innerHTML).toContain('pdp_noStock_status_30_31_icon');
    expect(iconStatus30or31.innerHTML).toContain('xInStock');
    expect(noStockStatus30sBtoText.innerText).toBe('Available to backorder');
    expect(notifyStockButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH in stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockAvailableCHText,
      withStockAvailableText,
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
      withStockAvailableText,
      withStockShippingAvailable,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      noStockForBtrDir,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockAvailableCHText?.innerText).toContain('23 In stock - delivered in 3 business day(s)');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(waldomStockStatusBelow60.innerText).toBe('1001 Additional stock will be available in 3 days');
    expect(additionalStock7371.innerText).toBe('Additional stock will be available in 50 days');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for LI in stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_SCNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockAvailableCHText,
      withStockAvailableText,
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
      withStockAvailableText,
      withStockShippingAvailable,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      noStockForBtrDir,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockAvailableCHText?.innerText).toContain('23 In stock - delivered in 3 business day(s)');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(waldomStockStatusBelow60.innerText).toBe('1001 Additional stock will be available in 3 days');
    expect(additionalStock7371.innerText).toBe('Additional stock will be available in 50 days');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for CH in stock for CDC only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_ONLY_SCNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_CH_IN_STOCK_CDC_ONLY_SCNORM);
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
      withStockAvailableCHText,
      withStockAvailableText,
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
      withStockAvailableCHText,
      withStockAvailableText,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerHTML).toContain('availableToOrderForCDC');
    expect(withStockAvailableToOrderCDC?.innerText).toContain('423 In stock - delivered in 3 business day(s)');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 30 for LI in stock for CDC only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('30').and.returnValue(MOCK_SALES_DATA_30);

    component.salesData = MOCK_SALES_DATA_30;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_CDC_ONLY_SCNORM.res;
    component.salesStatus = '30';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_30_LI_IN_STOCK_CDC_ONLY_SCNORM);
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
      withStockAvailableCHText,
      withStockAvailableText,
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
      withStockAvailableCHText,
      withStockAvailableText,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerHTML).toContain('availableToOrderForCDC');
    expect(withStockAvailableToOrderCDC?.innerText).toContain('423 In stock - delivered in 3 business day(s)');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 31 for CH NOT stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('31').and.returnValue(MOCK_SALES_DATA_31);

    component.salesData = MOCK_SALES_DATA_31;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_31_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '31';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_31_CH_NO_STOCK_SCNORM);
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
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
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
      noStockStatus30sText,
    ];

    expect(noStockHeadline.innerHTML).toContain('pdp_noStock_status_30_31_icon');
    expect(iconStatus30or31.innerHTML).toContain('xInStock');
    expect(noStockStatus30sBtoText.innerText).toBe('Available to backorder');
    expect(noStockReplenishDeliveryPickupText.innerText).toBe('Additional stock will be available in 28 days');
    expect(notifyStockButton.innerText).toBe('Notify me');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 31 for CH in stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('31').and.returnValue(MOCK_SALES_DATA_31);

    component.salesData = MOCK_SALES_DATA_31;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_31_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '31';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_31_CH_IN_STOCK_SCNORM);
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
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      waldomStockStatusBelow60,
      additionalStock7371,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain(
      '1045 In stock - delivered in 1 business day(s) or pick up in 2hrs',
    );
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 40 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('40').and.returnValue(MOCK_SALES_DATA_40);

    component.salesData = MOCK_SALES_DATA_40;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '40';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_40_CH_NO_STOCK_SCNORM);
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
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
      notifyStockButton,
    } = getAvailabilityOutOfStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
      noBTOStockHeadline,
      noStockStatus20sText,
      noStockStatus30sWaldomText,
      noStockStatus30sText,
      noStockStatus30sBtoText,
    ];

    expect(iconComingSoon.innerHTML).toContain('svg');
    expect(iconComingSoon.innerHTML).toContain('#E50018');
    expect(noStockHeadline.innerText).toBe('No longer available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 40 for CH in stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('40').and.returnValue(MOCK_SALES_DATA_40);

    component.salesData = MOCK_SALES_DATA_40;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '40';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockAvailableCHText,
      withStockAvailableText,
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
      withStockAvailableText,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockAvailableCHText?.innerText).toContain(
      '109 In stock - delivered in 1 business day(s) or pick up in 2hrs',
    );
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(waldomStockStatusBelow60.innerText).toBe('322 Additional stock will be available in 3 days');
    expect(additionalStock7371.innerText).toBe('Additional stock will be available in 40 days');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 40 IN stock CH CDC only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('40').and.returnValue(MOCK_SALES_DATA_40);

    component.salesData = MOCK_SALES_DATA_40;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDCONLY_SCNORM.res;
    component.salesStatus = '40';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_40_CH_IN_STOCK_CDCONLY_SCNORM);
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
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      waldomStockStatusBelow60,
      pickUpLabel,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable.innerHTML).toContain('availableToOrder');
    expect(withStockAvailableToOrder?.innerText).toContain('157 In stock - delivered in 3 business day(s)');
    expect(additionalStock7371?.innerText).toContain('Additional stock will be available in 161 days');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());
    done();
  });

  it('should show availability messaging for status 41 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('41').and.returnValue(MOCK_SALES_DATA_41);

    component.salesData = MOCK_SALES_DATA_41;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '41';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_41_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('svg');
    expect(iconComingSoon.innerHTML).toContain('#E50018');
    expect(noStockHeadline.innerText).toBe('No longer available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 41 IN stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('41').and.returnValue(MOCK_SALES_DATA_41);

    component.salesData = MOCK_SALES_DATA_41;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '41';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_SCNORM);
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
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
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
      waldomStockStatusBelow60,
      additionalStock7371,
      moreStockAvailable,
      additionalStock7374,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain('100 In stock - delivered in 1 business day(s)');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());
    done();
  });

  it('should show availability messaging for status 41 IN stock CH CDC Only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('41').and.returnValue(MOCK_SALES_DATA_41);

    component.salesData = MOCK_SALES_DATA_41;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_CDCONLY_SCNORM.res;
    component.salesStatus = '41';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_41_CH_IN_STOCK_CDCONLY_SCNORM);
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
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      waldomStockStatusBelow60,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain('157 In stock - delivered in 3 business day(s)');
    expect(additionalStock7371?.innerText).toContain('Additional stock will be available in 161 days');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());
    done();
  });

  it('should show availability messaging for status 50 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('50').and.returnValue(MOCK_SALES_DATA_50);

    component.salesData = MOCK_SALES_DATA_50;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '50';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_50_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('tick-cancel-circle-grey');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 50 NO stock DE', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('50').and.returnValue(MOCK_SALES_DATA_50);

    component.salesData = MOCK_SALES_DATA_50;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_50_DE_NO_STOCK_SCNORM.res;
    component.salesStatus = '50';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_50_DE_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 50 IN stock CH CDC only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('50').and.returnValue(MOCK_SALES_DATA_50);

    component.salesData = MOCK_SALES_DATA_50;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_50_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '50';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_50_CH_IN_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      withStockIconCheck,
      withStockIconTimes,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      waldomNextDayDelivery,
      sameDayOffer,
      withoutStockButFurtherStock,
      pickUpLabel,
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconClock,
      withStockNotifyButton,
      withStockIconCheck,
      withStockAvailableToOrder,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      salesDataBTOText,
      waldomDeliveryTime,
      waldomNextDayDelivery,
      additionalStock7371,
      sameDayOffer,
      pickUpLabel,
      moreStockAvailable,
      additionalStock7374,
      waldomStockStatusBelow60,
    ];

    expect(withStockIconTimes.innerHTML).toContain('tickCancelCircle');
    expect(withoutStockButFurtherStock.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());
    done();
  });

  it('should show availability messaging for status 51 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('51').and.returnValue(MOCK_SALES_DATA_51);

    component.salesData = MOCK_SALES_DATA_51;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_51_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '51';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_51_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('tick-cancel-circle-grey');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 51 IN stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('51').and.returnValue(MOCK_SALES_DATA_51);

    component.salesData = MOCK_SALES_DATA_51;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_51_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '51';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_51_CH_IN_STOCK_SCNORM);
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
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
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
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain('28 In stock - delivered in 1 business day(s) or pick up in 2hrs');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());
    done();
  });

  it('should show availability messaging for status 52 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('52').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_52;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '52';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_52_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('tick-cancel-circle-grey');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 52 for CH IN stock all warehouses', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('52').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_52;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_ALL_WAREHOUSES_SCNORM.res;
    component.salesStatus = '52';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_ALL_WAREHOUSES_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockAvailableCHText,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      additionalStock7374,
      waldomStockStatusBelow60,
      additionalStock7371,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
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
      additionalStock7374,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(withStockAvailableCHText?.innerText).toContain(
      '1061 In stock - delivered in 1 business day(s) or pick up in 2hrs',
    );
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(waldomStockStatusBelow60.innerText).toBe('180 Additional stock will be available in 3 days');
    expect(additionalStock7371.innerText).toBe('Additional stock will be available in 27 days');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 52 for CH IN stock CDC only', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('52').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_52;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_CDC_ONLY_SCNORM.res;
    component.salesStatus = '52';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_52_CH_IN_STOCK_CDC_ONLY_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockIconCheck,
      withStockIconClock,
      withStockNotifyButton,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      withStockAvailableToOrderCDC,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      additionalStock7371,
    ];

    expect(withStockIconTimes.innerHTML).toContain('tickCancelCircle');
    expect(withoutStockButFurtherStock.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 52 for DE IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('52').and.returnValue(MOCK_SALES_DATA_52);

    component.salesData = MOCK_SALES_DATA_52;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_52_DE_IN_STOCK_SCNORM.res;
    component.salesStatus = '52';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_52_DE_IN_STOCK_SCNORM);
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
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7371,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain(
      '137 In stock - delivered in 9 business day(s) or pick up in 2hrs',
    );
    expect(additionalStock7374.innerText).toBe('Additional stock will be available in 9 days');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 53 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('53').and.returnValue(MOCK_SALES_DATA_53);

    component.salesData = MOCK_SALES_DATA_53;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_53_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '53';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_53_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('tick-cancel-circle-grey');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 53 for CH IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('53').and.returnValue(MOCK_SALES_DATA_53);

    component.salesData = MOCK_SALES_DATA_53;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_53_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '53';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_53_CH_IN_STOCK_SCNORM);
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
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      pickUpLabel,
    ];

    expect(withStockIconCheck?.innerHTML).toContain('xInStock');
    expect(stockLevelAvailable?.innerText).toContain('55 In stock - delivered in 1 business day(s) or pick up in 2hrs');
    expect(waldomNextDayDelivery?.innerText).toBe('The exact delivery date will be stated in the order confirmation');
    expect(waldomStockStatusBelow60.innerText).toBe('100 Additional stock will be available in 3 days');
    expect(additionalStock7371.innerText).toBe('Additional stock will be available in 28 days');
    expect(sameDayOffer.innerText).toBe('Same Day – Now offering same day delivery across Switzerland');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 60 CDC ONLY CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('60').and.returnValue(MOCK_SALES_DATA_60);

    component.salesData = MOCK_SALES_DATA_60;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_60_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '60';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_60_CH_NO_STOCK_SCNORM);
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
      withStockIconCheck,
      withStockIconClock,
      withStockNotifyButton,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withStockShippingAvailable,
      noStockForBtrDir,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
    ];

    expect(stockLevelAvailable.innerText).toBe('No longer available');
    expect(withStockIconTimes.innerHTML).toContain('tickCancelCircle');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 61 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('61').and.returnValue(MOCK_SALES_DATA_61);

    component.salesData = MOCK_SALES_DATA_61;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_61_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '61';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_61_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('svg');
    expect(iconComingSoon.innerHTML).toContain('#E50018');
    expect(noStockHeadline.innerText).toBe('No longer available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 90 NO stock CH', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('90').and.returnValue(MOCK_SALES_DATA_90);

    component.salesData = MOCK_SALES_DATA_90;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK_SCNORM.res;
    component.salesStatus = '90';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_90_CH_NO_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
      noStockHeadline,
      iconStatus20or21,
      iconStatus30or31,
      iconComingSoon,
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
      noStockReplenishDeliveryPickupText,
      notifyStockButton,
      iconStatus20or21,
      iconStatus30or31,
      noStockBTOorDIR21or22,
      noStockBTOorDIR30or31,
      noStockBTOorDIRComingSoon,
      noStockBTOtext,
      noStockFurtherAdditionaltext,
      noStockReplenishDeliveryText,
    ];

    expect(iconComingSoon.innerHTML).toContain('tick-cancel-circle-grey');
    expect(iconComingSoon.innerHTML).toContain('tickCancelCircle');
    expect(noStockHeadline.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });

  it('should show availability messaging for status 90 for DE IN stock', (done) => {
    (salesStatus.getSalesStatusConfiguration as jasmine.Spy).withArgs('90').and.returnValue(MOCK_SALES_DATA_90);

    component.salesData = MOCK_SALES_DATA_90;
    component.productAvailability = MOCK_AVAILABILITY_STATUS_90_CH_IN_STOCK_SCNORM.res;
    component.salesStatus = '90';

    const data = processStockData(MOCK_AVAILABILITY_STATUS_90_CH_IN_STOCK_SCNORM);
    component = assignWarehouseData(data, component);
    fixture.detectChanges();

    const {
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
      withStockAvailableCHText,
      withStockAvailableText,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      additionalStock7374,
    } = getAvailabilityInStockLabels(fixture);

    const arrayOfNotDisplayedLabels = [
      withStockIconCheck,
      withStockIconClock,
      withStockNotifyButton,
      withStockAvailableText,
      waldomNextDayDelivery,
      sameDayOffer,
      pickUpLabel,
      waldomStockStatusBelow60,
      additionalStock7371,
      withStockShippingAvailable,
      withStockAvailableToOrder,
      withStockAvailableToBackOrder,
      withStockAvailableToOrderCDC,
      withoutStockButFurtherStock,
      salesDataBTOText,
      waldomDeliveryTime,
      moreStockAvailable,
      additionalStock7374,
      noStockForBtrDir,
    ];

    expect(withStockIconTimes.innerHTML).toContain('tick-cancel-circle-grey');
    expect(withStockIconTimes.innerHTML).toContain('tickCancelCircle');
    expect(withStockAvailableCHText.innerText).toBe('Currently not available');
    arrayOfNotDisplayedLabels.map((label) => expect(label).toBeNull());

    done();
  });
});
