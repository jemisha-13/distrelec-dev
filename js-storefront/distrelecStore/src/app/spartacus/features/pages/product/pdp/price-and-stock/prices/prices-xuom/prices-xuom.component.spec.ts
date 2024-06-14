import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PricesXUOMComponent } from './prices-xuom.component';
import { MOCK_PRICE_OBJECT } from '@features/mocks/mock-cart-store.service';

describe('PricesXUOMComponent', () => {
  let component: PricesXUOMComponent;
  let fixture: ComponentFixture<PricesXUOMComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PricesXUOMComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PricesXUOMComponent);
    component = fixture.componentInstance;
    component.price = MOCK_PRICE_OBJECT;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
