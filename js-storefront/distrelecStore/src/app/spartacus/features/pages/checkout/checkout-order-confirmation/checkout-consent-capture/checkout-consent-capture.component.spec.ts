import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { BehaviorSubject, of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { LoginService } from '@services/login.service';
import { NewsletterService } from '@services/newsletter.service';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';
import { AppendComponentService } from '@services/append-component.service';
import { MockTranslatePipe, Translatable, TranslatableParams, TranslationService } from '@spartacus/core';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { DistrelecRecaptchaModule } from '@features/shared-modules/recaptcha/recaptcha.module';
import { CheckoutConsentCaptureComponent } from './checkout-consent-capture.component';
import { MockTranslationService } from '@features/mocks/mock-translation.service';

class MockLoginService {
  isCaptchaDisabled_ = new BehaviorSubject<boolean>(false);
}

class MockNewsletterService {}

class MockDistOrderService {
  checkCustomerType(order, type) {}
  getCustomerUid(order) {
    return 'test@example.com';
  }
}

class MockAppendComponentService {}

describe('CheckoutConsentCaptureComponent', () => {
  let component: CheckoutConsentCaptureComponent;
  let fixture: ComponentFixture<CheckoutConsentCaptureComponent>;
  let mockLoginService: MockLoginService;
  let mockNewsletterService: MockNewsletterService;
  let mockDistOrderService: MockDistOrderService;
  let mockAppendComponentService: MockAppendComponentService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule, ReactiveFormsModule, DistrelecRecaptchaModule],
      declarations: [CheckoutConsentCaptureComponent, MockTranslatePipe],
      providers: [
        FormBuilder,
        { provide: LoginService, useClass: MockLoginService },
        { provide: NewsletterService, useClass: MockNewsletterService },
        { provide: DistOrderService, useClass: MockDistOrderService },
        { provide: AppendComponentService, useClass: MockAppendComponentService },
        { provide: TranslationService, useValue: MockTranslationService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutConsentCaptureComponent);
    component = fixture.componentInstance;
    mockLoginService = TestBed.inject(LoginService);
    mockNewsletterService = TestBed.inject(NewsletterService);
    mockDistOrderService = TestBed.inject(DistOrderService);
    mockAppendComponentService = TestBed.inject(AppendComponentService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the subscribe form with default values', () => {
    expect(component.subscribeForm).toBeDefined();
    expect(component.subscribeForm.get('email')).toBeDefined();
    // Assuming customerUid is provided as an input
    component.customerUid = 'test@example.com';
    component.ngOnInit();
    expect(component.subscribeForm.get('email').value).toBe('test@example.com');
  });
});
