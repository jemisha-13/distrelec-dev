/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MockTranslatePipe, OccEndpointsService, TranslationService, UserIdService } from '@spartacus/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { PricesComponent } from './prices.component';
import { BehaviorSubject, of } from 'rxjs';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { VolumePricePipeModule } from '@features/shared-modules/pipes/volume-price-pipe.module';
import { DecimalPlacesPipeModule } from '@features/shared-modules/pipes/decimal-places-pipe.module';
import {
  MOCK_PRICES_WITH_VOLUMEMAP_AND_VOLUMEPRICEMAP,
  MOCK_VOLUME_PRICE_WITH_DISCOUNT,
} from '@features/mocks/mock-price-data';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { Prices } from '@model/price.model';
import { CountryCodesEnum } from '@context-services/country.service';
import { DistJsonLdModule } from '@features/shared-modules/directives/dist-json-ld.module';
import { MainPriceComponent } from '@features/pages/product/pdp/price-and-stock/prices/main-price/main-price.component';
import { PricesXUOMComponent } from '@features/pages/product/pdp/price-and-stock/prices/prices-xuom/prices-xuom.component';
import { BulkDiscountLabelComponent } from '@features/pages/product/pdp/price-and-stock/prices/bulk-discount-label/bulk-discount-label.component';
import { VolumePricesComponent } from '@features/pages/product/pdp/price-and-stock/prices/volume-prices/volume-prices.component';
import { DiscountPricesModule } from '@features/pages/product/pdp/price-and-stock/prices/discount-prices/discount-prices.module';
import { UntypedFormControl } from '@angular/forms';

describe('PricesComponent', () => {
  let component: PricesComponent;
  let fixture: ComponentFixture<PricesComponent>;
  let userIdService: UserIdService;
  let occEndpointService;
  let http;
  let quantityControl: UntypedFormControl;

  beforeEach(() => {
    occEndpointService = jasmine.createSpyObj('OccEndpointsService', ['buildUrl']);
    http = jasmine.createSpyObj('HttpClient', ['get']);
    quantityControl = new UntypedFormControl(1);
    TestBed.configureTestingModule({
      imports: [
        CommonTestingModule,
        DistIconModule,
        VolumePricePipeModule,
        DecimalPlacesPipeModule,
        DiscountPricesModule,
        HttpClientTestingModule,
        DistJsonLdModule,
      ],
      declarations: [
        PricesComponent,
        BulkDiscountLabelComponent,
        MainPriceComponent,
        PricesXUOMComponent,
        VolumePricesComponent,
        MockTranslatePipe,
      ],
      providers: [
        { provide: TranslationService, useValue: MockTranslationService },
        { provide: OccEndpointsService, useValue: occEndpointService },
        { provide: HttpClient, useValue: http },
      ],
    }).compileComponents();

    (occEndpointService.buildUrl as jasmine.Spy).and.returnValue(
      'https://pretest.api.distrelec.com/rest/v2/distrelec_CH/users/anonymous/products/30142072/prices?fields=volumePrices(FULL)%2CvolumePricesMap(FULL)%2Cprice(FULL)',
    );
    (http.get as jasmine.Spy).and.returnValue(of(MOCK_PRICES_WITH_VOLUMEMAP_AND_VOLUMEPRICEMAP));
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricesComponent);
    userIdService = TestBed.inject(UserIdService);

    component = fixture.componentInstance;
    component.minQuantity = 1;
    component.quantityStep = 1;
    component.currentChannel = {
      currency: 'CHF',
      channel: 'B2B',
      language: 'en',
      country: CountryCodesEnum.SWITZERLAND,
      domain: 'https://pretest.distrelec.ch',
      mediaDomain: 'https://pretest.media.distrelec.com',
      storefrontDomain: 'https://pretest.storefront.distrelec.ch',
    };
    component.price$ = of({
      basePrice: 0.0509,
      currencyIso: 'CHF',
      formattedPriceWithVat: '0.05502',
      minQuantity: 25,
      priceWithVat: 0.05502,
      value: 0.0509,
      vatPercentage: 8.1,
      vatValue: 0.00412,
      pricePerXBaseQty: 100,
      pricePerXUOM: 'M',
      pricePerXUOMDesc: 'meter',
      pricePerXUOMQty: 1,
    });
    component.activeCountryCode$ = of('CH');
    component.userId$ = of('current');
    component.prices = MOCK_PRICES_WITH_VOLUMEMAP_AND_VOLUMEPRICEMAP as Prices;
    component.volumePricesMap = MOCK_VOLUME_PRICE_WITH_DISCOUNT;
    component.control = quantityControl;

    fixture.detectChanges();
  });

  // it('should show main price without VAT if user is B2B', (() => {
  //   const mainPrice = fixture.debugElement.nativeElement.querySelector('.main-price');
  //   const vatPrice = fixture.debugElement.nativeElement.querySelector('.vat-price');

  //   expect(mainPrice.innerText).toContain('0.0509');
  //   expect(mainPrice.innerText).toContain('(product.exc_vat)');
  //   expect(vatPrice.innerText).toContain('0.055');
  //   expect(vatPrice.innerText).toContain('(product.inc_vat)');
  // }));

  // it('should show main price with VAT if user is B2C', (() => {
  //   component.currentChannelData$ = new BehaviorSubject({ ...component.currentChannelData$.value, channel: 'B2C' });
  //   fixture.detectChanges();

  //   const mainPrice = fixture.debugElement.nativeElement.querySelector('.main-price');
  //   const vatPrice = fixture.debugElement.nativeElement.querySelector('.vat-price');

  //   expect(mainPrice.innerText).toContain('0.055');
  //   expect(mainPrice.innerText).toContain('(product.inc_vat)');
  //   expect(vatPrice.innerText).toContain('0.0509');
  //   expect(vatPrice.innerText).toContain('(product.exc_vat)');
  // }));

  // it('should display discounted prices if product has a discount for B2C', (() => {
  //   component.currentChannelData$ = new BehaviorSubject({ ...component.currentChannelData$.value, channel: 'B2C' });
  //   fixture.detectChanges();

  //   const prices = fixture.debugElement.nativeElement.querySelectorAll('.prices__priceholder-per-q');

  //   prices.forEach((priceComponent, index) => {
  //     if (index === 0) {
  //       expect(priceComponent.querySelector('.quantity-title')?.innerText).toContain('order.quantity');
  //     } else if (index === 1) {
  //       // Run the check for this individually since volume price pipe is converting 0.0550 into 0.055
  //       expect(priceComponent.querySelector('.quantity')?.innerText).toContain(component.volumePricesMap[index -1]?.key);
  //       expect(priceComponent.querySelector('.prices__priceholder__discounted')?.innerText).toContain('0.055');
  //     } else {
  //       expect(priceComponent.querySelector('.quantity')?.innerText).toContain(component.volumePricesMap[index -1]?.key);
  //       expect(priceComponent.querySelector('.prices__priceholder__discounted')?.innerText).toContain(component.volumePricesMap[index -1].value[0].value.priceWithVat.toFixed(4));
  //     }
  //   });
  // }));

  it('should display discounted prices if product has a discount for B2B', () => {
    const prices = fixture.debugElement.nativeElement.querySelectorAll('.prices__priceholder-per-q');

    prices.forEach((priceComponent, index) => {
      if (index === 0) {
        expect(priceComponent.querySelector('.quantity-title')?.innerText).toContain('order.quantity');
      } else {
        expect(priceComponent.querySelector('.quantity')?.innerText).toContain(
          component.volumePricesMap[index - 1]?.key,
        );
        expect(priceComponent.querySelector('.prices__priceholder__discounted')?.innerText).toContain(
          component.volumePricesMap[index - 1].value[0].value.basePrice.toFixed(4),
        );
      }
    });
  });

  // it('should show price per quantity and unit', (() => {
  //   const pricePerQuantity = fixture.debugElement.nativeElement.querySelector('.price-per-quantity');

  //   expect(pricePerQuantity.innerText).toContain('CHF');
  //   expect(pricePerQuantity.innerText).toContain('/');
  //   expect(pricePerQuantity.innerText).toContain('1');
  //   expect(pricePerQuantity.innerText).toContain('M');
  // }));

  // it('should display bulk quantity order message', (() => {
  //   const bulkDiscountLabel = fixture.debugElement.nativeElement.querySelector('#pdp_price_bulk_discount_display_btn_label');

  //   expect(bulkDiscountLabel.innerText).toContain('product.product_info.bulk_discount_notification');
  // }));

  it('should display not display bulk quantity order message if user is not on relevant webshop and user is NOT logged', () => {
    component.activeCountryCode$ = of('NO');
    component.userId$ = of('anonymous');
    fixture.detectChanges();

    const bulkDiscountLabel = fixture.debugElement.nativeElement.querySelector(
      '#pdp_price_bulk_discount_display_btn_label',
    );

    expect(bulkDiscountLabel).toBe(null);
  });

  // it('should display discounted prices if product has a discount for B2C', (() => {
  //   userIdService.setUserId('anonymous');

  //   component.quantitySelected_.next(51);
  //   fixture.detectChanges();

  //   const mainPrice = fixture.debugElement.nativeElement.querySelector('.main-price');
  //   const vatPrice = fixture.debugElement.nativeElement.querySelector('.vat-price');

  //   expect(mainPrice.innerText).toContain('0.0466');
  //   expect(mainPrice.innerText).toContain('(product.exc_vat)');
  //   expect(vatPrice.innerText).toContain('0.0504');
  //   expect(vatPrice.innerText).toContain('(product.inc_vat)');

  // }));
});
