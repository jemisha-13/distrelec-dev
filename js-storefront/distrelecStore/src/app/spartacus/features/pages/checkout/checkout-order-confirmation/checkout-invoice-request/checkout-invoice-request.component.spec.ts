import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckoutInvoiceRequestComponent } from './checkout-invoice-request.component';
import { Store, StoreModule } from '@ngrx/store';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';
import { MockTranslatePipe } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { CheckoutService } from '@services/checkout.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

class MockCheckoutService {}

describe('CheckoutInvoiceRequestComponent', () => {
  let component: CheckoutInvoiceRequestComponent;
  let fixture: ComponentFixture<CheckoutInvoiceRequestComponent>;
  let mockCheckoutService;
  let mockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, StoreModule.forRoot({})],
      declarations: [CheckoutInvoiceRequestComponent, MockTranslatePipe],
      providers: [{ provide: CheckoutService, useClass: MockCheckoutService }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutInvoiceRequestComponent);
    component = fixture.componentInstance;
    mockCheckoutService = TestBed.inject(CheckoutService);
    mockStore = TestBed.inject(Store);
    component.order = MOCK_ORDER_DATA as unknown as Order;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle invoice request button click', () => {
    spyOn(component, 'handleRequestToPayWithInvoice').and.callThrough();
    const button = fixture.debugElement.nativeElement.querySelector('app-dist-button');
    button.click();
    fixture.detectChanges();

    expect(component.handleRequestToPayWithInvoice).toHaveBeenCalled();
  });
});
