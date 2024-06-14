import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NumericStepperComponentModule } from './numeric-stepper.module';
import { NumericStepperComponent } from './numeric-stepper.component';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';
import { DistButtonComponentModule } from '@design-system/button/button.module';
import { StoreModule } from '@ngrx/store';
import { MockTranslatePipe, TranslationService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { QuantitySelectorComponentModule } from '@design-system/quantity-selector/quantity-selector.module';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('NumericStepperComponent', () => {
  let component: NumericStepperComponent;

  let fixture: ComponentFixture<NumericStepperComponent>;

  const mockTranslationService = {
    translate: (key: string): Observable<string> => {
      switch (key) {
        case 'homepage.quickOrderMoq': {
          return of(`Minimum order quantity is ${component.minimumQuantity}`);
        }
        case 'searchResults.moqMsg': {
          return of(`Minimum order quantity for article ${component.productCode} is ${component.control.value} pcs`);
        }
        case 'searchResults.stepMsg': {
          return of(`Sorry, this product is only available in multiples of ${component.control.value}`);
        }
        case 'validation.error.max.order.quantity.reached': {
          return of(`Maximum order quantity reached`);
        }
        default: {
          return of(key);
        }
      }
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        NumericStepperComponentModule,
        DistButtonComponentModule,
        QuantitySelectorComponentModule,
        DistIconModule,
        TooltipComponentModule,
        CommonTestingModule,
      ],
      declarations: [NumericStepperComponent, MockTranslatePipe],
      providers: [{ provide: TranslationService, useValue: mockTranslationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(NumericStepperComponent);

    component = fixture.componentInstance;

    component.minimumQuantity = 0;
    component.quantityStep = 0;
    component.control.setValue(1);
    component.productCode = '';

    fixture.detectChanges();
  });

  afterEach(() => {
    component = null;
    fixture = null;
  });

  it('should create component,', () => {
    expect(component).toBeTruthy();
  });

  it('if quantity is less then minQuantity tooltip should show minimum order quantity', () => {
    const tooltip = fixture.debugElement.nativeElement.querySelector('.min-quantity-tooltip');

    component.control.setValue(7);
    component.minimumQuantity = 15;

    fixture.detectChanges();

    expect(tooltip.textContent).toContain(`Minimum order quantity is ${component.minimumQuantity}`);
  });

  it('should show tooltip if isMaxOrderQuantityDisplayed is true', () => {
    // spyOnProperty(component, 'isMaxOrderQuantityDisplayed', 'get').and.returnValue(true);
    component.control.setValue(1);

    fixture.detectChanges();

    const tooltip = fixture.debugElement.nativeElement.querySelector('.maximum-quantity-tooltip');
    expect(tooltip.textContent).toContain(`Maximum order quantity reached`);
  });

  it('should show tooltip for non-multiple of quantityStep with isCartSearch true', () => {
    const tooltip = fixture.debugElement.nativeElement.querySelector('.min-quantity-tooltip');

    component.control.setValue(1);
    component.quantityStep = 2;

    fixture.detectChanges();

    expect(tooltip.textContent).toContain(`Minimum order quantity is ${component.minimumQuantity}`);
  });

  it('should show minimum order quantity tooltip when quantity is not equal to maxQuantity', () => {
    // spyOnProperty(component, 'maxQuantity', 'get').and.returnValue(10);

    component.control.setValue(7);
    component.quantityStep = 2;

    fixture.detectChanges();

    const tooltip = fixture.debugElement.nativeElement.querySelector('.min-quantity-tooltip');

    expect(tooltip).toBeTruthy();

    const expectedTooltipContent = `Minimum order quantity is ${component.minimumQuantity}`;
    expect(tooltip.textContent).toContain(expectedTooltipContent);
  });

  it('Should display Minimum order quantity for article tooltip if quantity is not equal to maqQuantity and if quantity is less then minQuantity', () => {
    // spyOnProperty(component, 'maxQuantity', 'get').and.returnValue(10);

    component.control.setValue(7);
    component.quantityStep = 2;
    component.minimumQuantity = 10;

    fixture.detectChanges();

    const tooltip = fixture.debugElement.nativeElement.querySelector('.min-quantity-tooltip');

    expect(tooltip).toBeTruthy();

    const expectedTooltipContent = `Minimum order quantity for article ${component.productCode} is ${component.control?.value} pcs`;
    expect(tooltip.textContent).toContain(expectedTooltipContent);
  });
});
