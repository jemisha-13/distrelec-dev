import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TextFieldComponent } from './text-field.component';
import { FormsModule, ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DistIconModule } from '@features/shared-modules/icon/icon.module';

describe('TextFieldComponent', () => {
  let component: TextFieldComponent;
  let fixture: ComponentFixture<TextFieldComponent>;
  let formGroup: UntypedFormGroup;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule, DistIconModule, ReactiveFormsModule],
      declarations: [TextFieldComponent],
    }).compileComponents();

    formGroup = new UntypedFormGroup({
      formControl: new UntypedFormControl(),
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextFieldComponent);

    component = fixture.componentInstance;
    component.fieldId = 'testField';
    component.labelText = 'Test Field';
    component.autoComplete = 'password';
    component.parentFormGroup = formGroup;
    component.parentFormControlName = 'formControl';
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle secure field', () => {
    expect(component.showSecureText).toBeFalsy();
    component.toggleSecureField();
    expect(component.showSecureText).toBeTruthy();
    component.toggleSecureField();
    expect(component.showSecureText).toBeFalsy();
  });

  it('should render label if labelText is provided', () => {
    const labelElement = fixture.nativeElement.querySelector('label');
    expect(labelElement.textContent).toContain('Test Field');
  });

  it('should render input field with correct attributes', () => {
    const inputElement = fixture.nativeElement.querySelector('input');
    expect(inputElement).toBeTruthy();
    expect(inputElement.getAttribute('id')).toBe('testField');
    expect(inputElement.getAttribute('autocomplete')).toBe(component.autoComplete);
  });

  it('should toggle secure field on button click', () => {
    component.isSecure = true;
    component.showSecureText = false;
    fixture.detectChanges();

    const secureButton = fixture.nativeElement.querySelector(`#${component.fieldId}-show-secure-text`);
    expect(component.showSecureText).toBeFalsy();
    secureButton.click();
    fixture.detectChanges();
    expect(component.showSecureText).toBeTruthy();
  });

  it('should toggle input type to password when isSecure and showSecureText are true', () => {
    component.isSecure = true;
    component.showSecureText = true;
    fixture.detectChanges();
    const inputElement = fixture.nativeElement.querySelector('input');
    expect(inputElement.getAttribute('type')).toBe('password');
  });

  it('should render the valid state when isValidState is true & isInvalidState is false', () => {
    component.isValidState = true;
    component.isInvalidState = false;
    fixture.detectChanges();
    const invalidIcon = fixture.nativeElement.querySelector('.icon.invalid-cross');
    const validIcon = fixture.nativeElement.querySelector('.icon.valid-check');
    const inputIsValid = fixture.nativeElement.querySelector('input').classList.contains('is-valid');
    const inputIsInvalid = fixture.nativeElement.querySelector('input').classList.contains('is-invalid');
    expect(validIcon).toBeTruthy();
    expect(invalidIcon).toBeNull();
    expect(inputIsValid).toBeTruthy();
    expect(inputIsInvalid).toBeFalsy();
  });

  it('should render the invalid state when isInvalidState is true & isValidState is false', () => {
    component.isInvalidState = true;
    component.isValidState = false;
    fixture.detectChanges();
    const invalidIcon = fixture.nativeElement.querySelector('.icon.invalid-cross');
    const validIcon = fixture.nativeElement.querySelector('icon.valid-check');
    const inputIsValid = fixture.nativeElement.querySelector('input').classList.contains('is-valid');
    const inputIsInvalid = fixture.nativeElement.querySelector('input').classList.contains('is-invalid');
    expect(invalidIcon).toBeTruthy();
    expect(validIcon).toBeNull();
    expect(inputIsValid).toBeFalsy();
    expect(inputIsInvalid).toBeTruthy();
  });

  it('should render default state when isValidState & isInvalidState is true', () => {
    component.isInvalidState = true;
    component.isValidState = true;
    fixture.detectChanges();
    const invalidIcon = fixture.nativeElement.querySelector('.icon.invalid-cross');
    const validIcon = fixture.nativeElement.querySelector('.icon.valid-check');
    const inputIsValid = fixture.nativeElement.querySelector('input').classList.contains('is-valid');
    const inputIsInvalid = fixture.nativeElement.querySelector('input').classList.contains('is-invalid');
    expect(invalidIcon).toBeNull();
    expect(validIcon).toBeNull();
    expect(inputIsValid).toBeFalsy();
    expect(inputIsInvalid).toBeFalsy();
  });

  it('should render default state when isValidState & isInvalidState is false', () => {
    component.isInvalidState = false;
    component.isValidState = false;
    fixture.detectChanges();
    const invalidIcon = fixture.nativeElement.querySelector('.icon.invalid-cross');
    const validIcon = fixture.nativeElement.querySelector('.icon.valid-check');
    const inputIsValid = fixture.nativeElement.querySelector('input').classList.contains('is-valid');
    const inputIsInvalid = fixture.nativeElement.querySelector('input').classList.contains('is-invalid');
    expect(invalidIcon).toBeNull();
    expect(validIcon).toBeNull();
    expect(inputIsValid).toBeFalsy();
    expect(inputIsInvalid).toBeFalsy();
  });
});
