import { Component, EventEmitter, Input, OnInit, Output, Renderer2 } from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { faAngleDown, faCheck, faCircleNotch, faEye, faEyeSlash, faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject } from 'rxjs';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import { first } from 'rxjs/operators';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { SiteContextConfig } from '@spartacus/core';
import { isActiveSiteInternational } from '../../../../../site-context/utils';
import { EventTypes } from './country-select/country-select.component';
import { CountryOfOrigin } from '@model/product.model';

@Component({
  selector: 'app-about-you-form',
  templateUrl: './about-you-form.component.html',
  styleUrls: ['./about-you-form.component.scss'],
})
export class AboutYouFormComponent implements OnInit {
  // We pass the form from b2c or b2b components
  @Input() generalRegForm: UntypedFormGroup;
  @Input() onControlTouch: (controlName: string) => void;
  @Input() activeSiteId: string;
  @Input() stepsList_: BehaviorSubject<{ key: string; active: boolean }[]>;
  @Input() isEmailExist$: BehaviorSubject<{ isEmailExist: boolean }>;

  @Output() inputClicked = new EventEmitter<string>();
  @Output() countryChanged = new EventEmitter<boolean>();

  isPasswordVisible: boolean;
  isConfrmPasswordVisible: boolean;
  isLoading: boolean;
  registrationType: string;

  faTimes = faTimes;
  faCheck = faCheck;
  faEye = faEye;
  faEyeSlash = faEyeSlash;
  faCircleNotch = faCircleNotch;
  faAngleDown = faAngleDown;

  countryList_: BehaviorSubject<CountryOfOrigin> = new BehaviorSubject<CountryOfOrigin>(null);
  activeSiteCountry$: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  functionResults_: BehaviorSubject<Array<object>> = new BehaviorSubject<Array<object>>(null);
  isDisplayInvoiceContainer_: BehaviorSubject<boolean> = this.registerService.isDisplayInvoiceContainer_;

  countriesWithJobRole = ['FR', 'BE', 'NL'];

  isValidNumberForRegion = true;
  isUserTyping = false;
  showInvoiceField = false;

  isExportShop: boolean;

  constructor(
    // Please keep renderer for onControlTouch() which is passed from the parent class
    private renderer: Renderer2,
    private registerService: RegisterService,
    private countryService: CountryService,
    private config: SiteContextConfig,
  ) {}

  ngOnInit() {
    this.countryService
      .getActive()
      .pipe(first())
      .subscribe((country) => this.activeSiteCountry$.next(country));

    this.registrationType = this.generalRegForm.get('type').value;

    this.isExportShop = isActiveSiteInternational(this.config);

    this.activeSiteCountry$.pipe(first()).subscribe({
      next: (country) => {
        if (this.generalRegForm.controls.countryCode.value !== 'EX') {
          if (this.generalRegForm.controls.countryCodeOther) {
            this.generalRegForm.controls.countryCodeOther.setValue(country);
          } else {
            this.generalRegForm.controls.countryCode.setValue(country);
          }
        }
        if (this.countriesWithJobRole.includes(country)) {
          this.validateJobFunction();
        }
        this.registerService.invoiceContainerDisplay(this.generalRegForm, country);
      },
    });

    this.retrieveCountryList();

    this.stepsList_?.next([
      { key: 'registration.general.account_type', active: true },
      { key: 'registration.b2b.company_title', active: true },
      { key: 'registration.general.your_details', active: false },
    ]);
  }

  retrieveCountryList() {
    this.registerService
      .getCountryCodes(this.generalRegForm)
      .pipe(first())
      .subscribe((value: CountryOfOrigin) => {
        this.countryList_.next(value);
      });
  }

  validateJobFunction() {
    this.registerService
      .validateFunctions()
      .pipe(first())
      .subscribe((value) => {
        this.functionResults_.next(value.functions);
      });
  }

  onTitleClick(name: string) {
    this.generalRegForm.patchValue({
      titleCode: name,
    });
  }

  toggleDisplayDiv() {
    this.showInvoiceField = !this.showInvoiceField;
  }

  validateIfEmailExist() {
    if (!this.generalRegForm.get('uid').errors) {
      this.isLoading = true;
      this.registerService
        .validateEmail(this.generalRegForm.get('uid').value)
        .pipe(first())
        .subscribe((value) => {
          if (value) {
            this.isEmailExist$.next({ isEmailExist: false });
          } else {
            this.isEmailExist$.next({ isEmailExist: true });
          }
          this.isLoading = false;
        });
    }
  }

  updateValueAndValidityOnPhoneField() {
    this.generalRegForm.get('phoneNumber').updateValueAndValidity();
  }

  resetInputValidation(control: AbstractControl) {
    control.setErrors(null);
    control.markAsPending();
  }

  onCountrySelectEvent(event: EventTypes) {
    if (event === 'click') {
      this.onControlTouch('countryCodeOther');
    } else if (event === 'change') {
      this.countryChanged.emit(true);
    } else if (event === 'blur') {
      this.updateValueAndValidityOnPhoneField();
    }
  }

  isNonExportShopWithMultipleCountries(countryList: CountryOfOrigin[]) {
    return countryList.length > 1 && !this.isExportShop;
  }
}
