import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { faAngleDown, faAngleRight, faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import { AddressChangeService } from '@services/address-change.service';
import { CheckoutService } from '@services/checkout.service';
import { first, map } from 'rxjs/operators';

@Component({
  selector: 'app-offline-change-address',
  templateUrl: './offline-change-address.component.html',
  styleUrls: ['./offline-change-address.component.scss'],
})
export class OfflineChangeAddressComponent implements OnInit {
  faArrowRight = faAngleRight;
  faAlert = faExclamationTriangle;
  faAngleDown = faAngleDown;
  countryList = [];
  submitError = false;

  addressForm = this.formBuilder.group({
    // build generic form validators
    customerNumber: new UntypedFormControl('', Validators.required),

    // build old form validators
    oldFirstName: new UntypedFormControl('', Validators.required),
    oldLastName: new UntypedFormControl('', Validators.required),
    oldStreet: new UntypedFormControl('', Validators.required),
    oldPostalCode: new UntypedFormControl('', Validators.required),
    oldTown: new UntypedFormControl('', Validators.required),
    oldCountry: new UntypedFormControl('', Validators.required),
    oldDepartment: new UntypedFormControl(''),

    // build new form validators
    newFirstName: new UntypedFormControl('', Validators.required),
    newLastName: new UntypedFormControl('', Validators.required),
    newStreet: new UntypedFormControl('', Validators.required),
    newPostalCode: new UntypedFormControl('', Validators.required),
    newTown: new UntypedFormControl('', Validators.required),
    newCountry: new UntypedFormControl('', Validators.required),
    newDepartment: new UntypedFormControl(''),
    comment: new UntypedFormControl(''),
  });

  constructor(
    private formBuilder: UntypedFormBuilder,
    private addressChangeService: AddressChangeService,
    private checkoutService: CheckoutService,
  ) {}

  // populate generic
  get customerNumber() {
    return this.addressForm.get('customerNumber');
  }

  // populate old form
  get oldFirstName() {
    return this.addressForm.get('oldFirstName');
  }
  get oldLastName() {
    return this.addressForm.get('oldLastName');
  }
  get oldStreet() {
    return this.addressForm.get('oldStreet');
  }
  get oldPostalCode() {
    return this.addressForm.get('oldPostalCode');
  }
  get oldTown() {
    return this.addressForm.get('oldTown');
  }
  get oldCountry() {
    return this.addressForm.get('oldCountry');
  }

  get oldDepartment() {
    return this.addressForm.get('oldDepartment');
  }

  // populate new form
  get newFirstName() {
    return this.addressForm.get('newFirstName');
  }
  get newLastName() {
    return this.addressForm.get('newLastName');
  }
  get newStreet() {
    return this.addressForm.get('newStreet');
  }
  get newPostalCode() {
    return this.addressForm.get('newPostalCode');
  }
  get newTown() {
    return this.addressForm.get('newTown');
  }
  get newCountry() {
    return this.addressForm.get('newCountry');
  }
  get newDepartment() {
    return this.addressForm.get('newDepartment');
  }
  get comment() {
    return this.addressForm.get('comment');
  }

  ngOnInit(): void {
    this.checkoutService
      .getCountries('SHIPPING')
      .pipe(
        first(),
        map((data) => {
          this.countryList = data.countries.map((country) => country.isocode);
        }),
      )
      .subscribe();
  }

  isControlInvalid(controlName: string): boolean {
    if (!this.addressForm.get(controlName)) {
      return false;
    }
    return (
      this.addressForm.get(controlName).invalid &&
      (this.addressForm.get(controlName).dirty || this.addressForm.get(controlName).touched)
    );
  }

  validateFormFields(formGroup: UntypedFormGroup): void {
    Object.keys(formGroup.controls).forEach((field) => {
      const control = formGroup.get(field);
      if (control instanceof UntypedFormControl) {
        control.markAsTouched({ onlySelf: true });
      }
    });
  }

  onSubmit() {
    if (this.addressForm.valid) {
      this.addressChangeService.postAddressChange(this.addressForm.value);
    } else {
      this.validateFormFields(this.addressForm);
    }
  }
}
