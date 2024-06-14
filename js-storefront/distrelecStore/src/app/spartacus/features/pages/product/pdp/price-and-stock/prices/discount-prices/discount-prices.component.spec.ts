import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DiscountPricesComponent } from './discount-prices.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('DiscountPricesComponent', () => {
  let component: DiscountPricesComponent;
  let fixture: ComponentFixture<DiscountPricesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DiscountPricesComponent],
      imports: [CommonTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountPricesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
