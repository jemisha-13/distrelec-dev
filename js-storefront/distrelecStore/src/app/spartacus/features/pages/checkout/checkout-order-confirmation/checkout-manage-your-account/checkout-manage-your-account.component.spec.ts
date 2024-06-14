import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';
import { MockTranslatePipe, TranslationService } from '@spartacus/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Order } from '@spartacus/order/root';
import { CheckoutManageYourAccountComponent } from './checkout-manage-your-account.component';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { MockTranslationService } from '@features/mocks/mock-translation.service';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';

class MockDistOrderService {}

describe('CheckoutManageYourAccountComponent', () => {
  let component: CheckoutManageYourAccountComponent;
  let fixture: ComponentFixture<CheckoutManageYourAccountComponent>;
  let mockDistOrderService: MockDistOrderService;
  let order: Order;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      declarations: [CheckoutManageYourAccountComponent, MockTranslatePipe],
      providers: [
        { provide: DistOrderService, useClass: MockDistOrderService },
        { provide: TranslationService, useValue: MockTranslationService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutManageYourAccountComponent);
    component = fixture.componentInstance;
    mockDistOrderService = TestBed.inject(DistOrderService);
    component.order = MOCK_ORDER_DATA as unknown as Order;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
