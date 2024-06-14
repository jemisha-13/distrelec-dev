import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { faCheck, faExclamationTriangle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { RegisterService } from 'src/app/spartacus/services/register.service';

@Component({
  selector: 'app-customer-number',
  templateUrl: './customer-number.component.html',
  styleUrls: ['./customer-number.component.scss'],
})
export class CustomerNumberComponent {
  @Input() generalRegForm: UntypedFormGroup;
  @Input() displayNextSteps$: BehaviorSubject<boolean>;

  faTimes = faTimes;
  faCheck = faCheck;
  faExclamationTriangle = faExclamationTriangle;
  isValidated = false;
  isValidNumberFound = false;

  isCustomerNumberInvalid$: BehaviorSubject<{ isCustomerNumberInvalid: boolean }> = new BehaviorSubject({
    isCustomerNumberInvalid: false,
  });

  constructor(private registerService: RegisterService) {}

  validateCustomerNumber(): void {
    if (this.generalRegForm.get('customerId').value.length <= 6) {
      this.setCustomerInputInvalid();
      this.isValidated = true;
    } else {
      this.callValidateNumber();
    }
  }

  setCustomerInputInvalid(): void {
    this.generalRegForm.get('customerId').setErrors({ incorrect: true });
    this.isCustomerNumberInvalid$.next({ isCustomerNumberInvalid: true });
    this.generalRegForm.get('customerId').markAsTouched();
  }

  callValidateNumber(): void {
    this.registerService
      .validateCustomerNumber(this.generalRegForm.get('customerId').value.trim(), this.generalRegForm)
      .pipe(first())
      .subscribe((isValidNumber) => {
        this.handleCustNumberValidation(isValidNumber);
        this.isValidNumberFound = isValidNumber;
        this.registerService.isCustomerExist = true;
        this.registerService.invoiceContainerDisplay(this.generalRegForm, this.generalRegForm.get('countryCode').value);
      });
  }

  handleCustNumberValidation(isValidNumber: boolean): void {
    if (isValidNumber) {
      this.isCustomerNumberInvalid$.next({ isCustomerNumberInvalid: false });
      this.generalRegForm.get('customerId').setErrors(null);
      this.displayNextSteps$.next(true);
    } else {
      this.isCustomerNumberInvalid$.next({ isCustomerNumberInvalid: true });
      this.generalRegForm.get('customerId').setErrors({ isInvalidVAT: true });
    }
    this.isValidated = true;
  }
}
