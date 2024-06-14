/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StickyAddToCartComponent } from './sticky-add-to-cart.component';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { DistCartService } from '@services/cart.service';
import { StoreModule } from '@ngrx/store';
import { BaseSiteService, MockTranslatePipe, TranslationService } from '@spartacus/core';
import { of } from 'rxjs';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NumericStepperComponentModule } from '@features/shared-modules/components/numeric-stepper/numeric-stepper.module';
import { CountryCodesEnum } from '@context-services/country.service';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
export type Channel = 'B2B' | 'B2C';

const translationService = jasmine.createSpyObj('TranslationService', ['translate']);

class MockDistCartService {
  addProductToCart() {
    return of({});
  }
}

class MockProductDataService {
  getIsProductNotBuyable() {
    return of(true);
  }
}

describe('StickyComponent', () => {
  let component: StickyAddToCartComponent;

  let fixture: ComponentFixture<StickyAddToCartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        CommonTestingModule,
        DistButtonComponentModule,
        DistIconModule,
        RouterTestingModule,
        HttpClientTestingModule,
        NumericStepperComponentModule,
      ],
      declarations: [StickyAddToCartComponent, MockTranslatePipe],
      providers: [
        { provide: DistCartService, useClass: MockDistCartService },
        { provide: ProductDataService, useClass: MockProductDataService },
        { provide: TranslationService, useValue: translationService },
      ],
    }).compileComponents();

    translationService.translate.and.returnValue(of(''));

    fixture = TestBed.createComponent(StickyAddToCartComponent);

    component = fixture.componentInstance;
    component.addToCartForm = new UntypedFormGroup({
      quantity: new UntypedFormControl(1),
    });
    component.productCode = '30099461';
    component.title = 'Key Ring Torch, LED, 1x AAA, 20lm, 25m, IPX4, Black';
    component.manNumber = 'RND 510-00003';
    component.orderQuantityMinimum = 1;
    component.quantityStep = 1;
    // @ts-ignore
    component.currentChannelData$ = of({
      country: 'ch',
      language: 'en',
      channel: 'B2C',
      currency: CountryCodesEnum.SWITZERLAND,
      domain: '',
      mediaDomain: '',
    });

    fixture.detectChanges();
  });

  it('should have title and manufacturer number', () => {
    expect(component.title).toBe('Key Ring Torch, LED, 1x AAA, 20lm, 25m, IPX4, Black');
    expect(component.manNumber).toBe('RND 510-00003');
  });

  // it('should call update quantity selected on quantity change', () => {
  //   component.onChange({ qty: 2, isValid: true });
  //   fixture.detectChanges();
  //   expect(component.quantitySelected_.value).toBe(2);
  // });

  it('should call add to cart button', () => {
    //@ts-ignore
    spyOn(component.cartService, 'addProductToCart').and.callThrough();
    spyOn(component, 'addToCartClick').and.callThrough();

    component.addToCartClick();
    fixture.detectChanges();
    expect(component.addToCartClick).toHaveBeenCalled();
  });
});
