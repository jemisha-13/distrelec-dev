import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckoutGuestRegistrationComponent } from './checkout-guest-registration.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CheckoutService } from '@services/checkout.service';
import { DistGuestUserService } from '@services/guest-user.service';
import { DistLogoutService } from '@services/logout.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { RouterTestingModule } from '@angular/router/testing';
import { MockTranslatePipe } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { MOCK_ORDER_DATA } from '@features/mocks/mock-order-data';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';

class MockCheckoutService {}

class MockGuestUserService {}

class MockLogoutService {
  logoutGuestUser = jasmine.createSpy('logoutGuestUser');
}

describe('CheckoutGuestRegistrationComponent', () => {
  let component: CheckoutGuestRegistrationComponent;
  let fixture: ComponentFixture<CheckoutGuestRegistrationComponent>;
  let mockCheckoutService;
  let mockGuestUserService;
  let mockLogoutService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckoutGuestRegistrationComponent, MockTranslatePipe],
      imports: [CommonTestingModule, ReactiveFormsModule, RouterTestingModule, DistrelecRecaptchaModule],
      providers: [
        FormBuilder,
        { provide: CheckoutService, useClass: MockCheckoutService },
        { provide: DistGuestUserService, useClass: MockGuestUserService },
        { provide: DistLogoutService, useClass: MockLogoutService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutGuestRegistrationComponent);
    component = fixture.componentInstance;
    mockCheckoutService = TestBed.inject(CheckoutService);
    mockGuestUserService = TestBed.inject(DistGuestUserService);
    mockLogoutService = TestBed.inject(DistLogoutService);
    component.order = MOCK_ORDER_DATA as unknown as Order;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form and call logout on ngOnInit', () => {
    component.ngOnInit();
    expect(component.checkoutGuestRegistrationForm).toBeDefined();
    expect(mockLogoutService.logoutGuestUser).toHaveBeenCalled();
  });

  it('should initialize the registration form with required controls', () => {
    const form = component.checkoutGuestRegistrationForm;
    expect(form).toBeDefined();
    expect(form.get('guestRegisterPassword')).toBeDefined();
    expect(form.get('guestRegisterPasswordConfirm')).toBeDefined();
  });

  it('should validate the password fields', () => {
    const form = component.checkoutGuestRegistrationForm;
    const passwordControl = form.get('guestRegisterPassword');
    const confirmPasswordControl = form.get('guestRegisterPasswordConfirm');

    passwordControl.setValue('');
    confirmPasswordControl.setValue('');
    expect(passwordControl.valid).toBeFalsy();
    expect(confirmPasswordControl.valid).toBeFalsy();

    passwordControl.setValue('123456');
    confirmPasswordControl.setValue('123456');
    expect(passwordControl.valid).toBeTruthy();
    expect(confirmPasswordControl.valid).toBeTruthy();
  });

  it('should check if passwords match', () => {
    const form = component.checkoutGuestRegistrationForm;
    form.get('guestRegisterPassword').setValue('password');
    form.get('guestRegisterPasswordConfirm').setValue('password');
    expect(component.arePasswordsEqual()).toBeTrue();

    form.get('guestRegisterPasswordConfirm').setValue('different');
    expect(component.arePasswordsEqual()).toBeFalse();
  });
});
