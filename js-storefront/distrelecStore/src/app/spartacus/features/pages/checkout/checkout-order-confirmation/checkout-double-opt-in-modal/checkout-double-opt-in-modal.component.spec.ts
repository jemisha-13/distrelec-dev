import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';
import { MockTranslatePipe, Translatable, TranslatableParams, TranslationService } from '@spartacus/core';
import { CheckoutDoubleOptInModalComponent } from './checkout-double-opt-in-modal.component';
import { MockTranslationService } from '@features/mocks/mock-translation.service';

class MockAppendComponentService {
  appendBackdropModal = jasmine.createSpy('appendBackdropModal');
  removeBackdropComponentFromBody = jasmine.createSpy('removeBackdropComponentFromBody');
}

describe('DoubleOptInModalComponent', () => {
  let component: CheckoutDoubleOptInModalComponent;
  let fixture: ComponentFixture<CheckoutDoubleOptInModalComponent>;
  let mockAppendComponentService: MockAppendComponentService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckoutDoubleOptInModalComponent, MockTranslatePipe],
      providers: [
        { provide: AppendComponentService, useClass: MockAppendComponentService },
        { provide: TranslationService, useValue: MockTranslationService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutDoubleOptInModalComponent);
    component = fixture.componentInstance;
    mockAppendComponentService = TestBed.inject(AppendComponentService) as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call appendBackdropModal on init', () => {
    expect(mockAppendComponentService.appendBackdropModal).toHaveBeenCalled();
  });

  it('should call removeBackdropComponentFromBody on destroy', () => {
    fixture.destroy();
    expect(mockAppendComponentService.removeBackdropComponentFromBody).toHaveBeenCalled();
  });

  it('should emit false when closeModal is called', () => {
    spyOn(component.doubleOptInModalVisibility, 'emit');
    component.closeModal();
    expect(component.doubleOptInModalVisibility.emit).toHaveBeenCalledWith(false);
  });

  it('should handle email input correctly', () => {
    const testEmail = 'test@example.com';
    component.email = testEmail;
    expect(component.email).toBe(testEmail);
  });
});
