import { ComponentFixture, fakeAsync, tick, TestBed } from '@angular/core/testing';
import { LegacyQuantitySelectorComponent } from '@design-system/quantity-selector-legacy/quantity-selector-legacy.component';
import { FormsModule } from '@angular/forms';
import { Translatable, TranslatableParams } from '@spartacus/core';
import { Pipe, PipeTransform } from '@angular/core';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

const returnSetOfTranslations = (input: Translatable | string, options: TranslatableParams = {}) => {
  switch (input) {
    case 'base.increase-quantity': {
      return `Increase Quantity`;
    }
    case 'base.decrease-quantity': {
      return `Decrease Quantity`;
    }
  }
};

@Pipe({
  name: 'cxTranslate',
})
class MockTranslatePipe implements PipeTransform {
  transform(input: Translatable | string, options: TranslatableParams = {}): Translatable | string {
    return returnSetOfTranslations(input, options);
  }
}

describe('QuantitySelectorComponent', () => {
  let quantitySelectorComponent: LegacyQuantitySelectorComponent;
  let quantitySelectorFixture: ComponentFixture<LegacyQuantitySelectorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LegacyQuantitySelectorComponent, MockTranslatePipe],
      imports: [FormsModule, DistIconModule],
    }).compileComponents();

    quantitySelectorFixture = TestBed.createComponent(LegacyQuantitySelectorComponent);
    quantitySelectorComponent = quantitySelectorFixture.componentInstance;
  });

  it('Should verify that a minus button is disabled by default', () => {
    quantitySelectorFixture.detectChanges();

    expect(quantitySelectorFixture.nativeElement.querySelector('button').disabled).toEqual(true);
  });

  it('Should increase quantity when quantity is less than maxQuantity', () => {
    quantitySelectorComponent.quantity = 2;
    quantitySelectorComponent.minQuantity = 1;
    quantitySelectorComponent.maxQuantity = 5;
    quantitySelectorComponent.quantityStep = 1;

    quantitySelectorComponent.increase();

    expect(quantitySelectorComponent.quantity).toBe(3);
  });

  it('Should be set to minQuantity if quantity is less than minQuantity', () => {
    quantitySelectorComponent.quantity = 20;
    quantitySelectorComponent.minQuantity = 30;

    quantitySelectorComponent.increase();

    expect(quantitySelectorComponent.quantity).toBe(30);
  });

  it('Should be set to maxQuantity if quantity is greater than maxQuantity', () => {
    quantitySelectorComponent.quantity = 30;
    quantitySelectorComponent.maxQuantity = 20;

    quantitySelectorComponent.increase();

    expect(quantitySelectorComponent.quantity).toBe(20);
  });

  it('Should decrease quantity when quantity is greater than minQuantity', () => {
    quantitySelectorComponent.quantity = 2;
    quantitySelectorComponent.minQuantity = 1;
    quantitySelectorComponent.quantityStep = 1;

    quantitySelectorComponent.decrease();

    expect(quantitySelectorComponent.quantity).toBe(1);
  });

  it('Should decrease quantity when quantityStep is greater than 1', () => {
    quantitySelectorComponent.quantity = 20;
    quantitySelectorComponent.minQuantity = 15;
    quantitySelectorComponent.quantityStep = 2;

    quantitySelectorComponent.decrease();

    expect(quantitySelectorComponent.quantity).toBe(5);
  });

  it('Should decrease quantity when quantityStep is not greater than 1', () => {
    quantitySelectorComponent.quantity = 20;
    quantitySelectorComponent.minQuantity = 15;
    quantitySelectorComponent.quantityStep = 1;

    quantitySelectorComponent.decrease();

    expect(quantitySelectorComponent.quantity).toBe(19);
  });

  it('Should debounce input event and call handleLogicWithMinQuantity when onInput is triggered', fakeAsync(() => {
    const handleLogicWithMinQuantitySpy = spyOn(quantitySelectorComponent, 'handleLogicWithMinQuantity');

    quantitySelectorComponent.onInput();

    const inputElement = quantitySelectorFixture.nativeElement.querySelector('input');
    inputElement.dispatchEvent(new Event('input'));

    tick(1000);

    expect(handleLogicWithMinQuantitySpy).toHaveBeenCalled();
  }));

  it('Should set quantity to minQuantity if quantity is not greater than 1', () => {
    const inputElement = quantitySelectorFixture.nativeElement.querySelector('input');
    quantitySelectorComponent.minQuantity = 10;

    inputElement.value = '0';
    inputElement.dispatchEvent(new Event('input'));

    quantitySelectorComponent.handleLogicWithMinQuantity();

    expect(quantitySelectorComponent.quantity).toBe(10);
  });

  it('Should parse input value into an integer', () => {
    const inputElement = quantitySelectorFixture.nativeElement.querySelector('input');

    inputElement.value = '5';
    inputElement.dispatchEvent(new Event('input'));

    quantitySelectorComponent.handleLogicWithMinQuantity();

    expect(quantitySelectorComponent.quantity).toBe(5);
  });

  it('Should adjust quantity based on the remainder', () => {
    const inputElement = quantitySelectorFixture.nativeElement.querySelector('input');

    quantitySelectorComponent.quantityStep = 6;

    inputElement.value = '20';
    inputElement.dispatchEvent(new Event('input'));

    quantitySelectorComponent.handleLogicWithMinQuantity();

    expect(quantitySelectorComponent.quantity).toBe(24);
  });

  it('Should assign minQuantity to quantity if quantity is NaN', () => {
    quantitySelectorComponent.quantity = NaN;
    quantitySelectorComponent.minQuantity = 20;

    quantitySelectorComponent.ifNoInputAssigned();

    expect(quantitySelectorComponent.quantity).toBe(20);
  });
});
