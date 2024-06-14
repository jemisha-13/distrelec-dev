import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';
import { MockTranslatePipe, TranslationService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { CheckoutService } from '@services/checkout.service';
import { CustomerType } from '@model/site-settings.model';
import { CheckoutProfileInformationComponent } from './checkout-profile-information.component';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

class MockCheckoutService {
  updateUserProfile = jasmine.createSpy().and.returnValue(of({}));
  getFunctions = jasmine.createSpy().and.returnValue(of(/* Mock Data */));
  getDepartments = jasmine.createSpy().and.returnValue(of(/* Mock Data */));
}

class MockDistOrderService {
  checkCustomerType(order: Order, type: CustomerType) {
    return type === CustomerType.B2C;
  }
}

describe('CheckoutProfileInformationComponent', () => {
  let component: CheckoutProfileInformationComponent;
  let fixture: ComponentFixture<CheckoutProfileInformationComponent>;
  let mockCheckoutService;
  let mockDistOrderService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckoutProfileInformationComponent, MockTranslatePipe],
      imports: [CommonTestingModule, ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: CheckoutService, useClass: MockCheckoutService },
        { provide: DistOrderService, useClass: MockDistOrderService },
        { provide: TranslationService, useValue: TranslationService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutProfileInformationComponent);
    component = fixture.componentInstance;
    mockCheckoutService = TestBed.inject(CheckoutService);
    mockDistOrderService = TestBed.inject(DistOrderService);
    component.order = component.order = MOCK_ORDER_DATA as unknown as Order;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form and fetch data on ngOnInit', () => {
    component.ngOnInit();
    expect(component.profileForm).toBeDefined();
    expect(mockCheckoutService.getFunctions).toHaveBeenCalled();
    expect(mockCheckoutService.getDepartments).toHaveBeenCalled();
  });

  it('should validate form controls', () => {
    const areaOfUseControl = component.profileForm.get('areaOfUse');
    const jobRoleControl = component.profileForm.get('jobRole');

    areaOfUseControl.setValue('');
    jobRoleControl.setValue('');
    expect(areaOfUseControl.valid).toBeFalsy();
    expect(jobRoleControl.valid).toBeFalsy();

    areaOfUseControl.setValue('1234');
    jobRoleControl.setValue('1234');
    expect(areaOfUseControl.valid).toBeTruthy();
    expect(jobRoleControl.valid).toBeTruthy();
  });

  it('should call updateUserProfile on form submission', () => {
    component.updateUserProfile();
    expect(mockCheckoutService.updateUserProfile).toHaveBeenCalled();
  });
});
