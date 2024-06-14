import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidatorFn, Validators } from '@angular/forms';
import { faCheck, faCheckCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { SiteContextConfig, WindowRef } from '@spartacus/core';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { catchError, first, take } from 'rxjs/operators';
import { DistCookieService } from '@services/dist-cookie.service';
import { VatValidationData } from '@model/registration.model';
import { RegisterService } from 'src/app/spartacus/services/register.service';
import {
  CountriesWithFunctionCode,
  CountryCodesEnum,
  CountryService,
} from 'src/app/spartacus/site-context/services/country.service';
import { Router } from '@angular/router';
import { CountriesWithoutVat, RegisterServiceHelper } from '@helpers/register-helpers';
import { PhoneNumberService } from '@services/phonenumber.service';
import { isActiveSiteInternational } from '../../../../site-context/utils';

@Component({
  selector: 'app-b2b-form',
  templateUrl: './b2b-form.component.html',
  styleUrls: ['./b2b-form.component.scss'],
})
export class B2bFormComponent implements OnInit, OnDestroy {
  @Input() activeSiteId: string;
  @Input() stepsList_: BehaviorSubject<{ key: string; active: boolean }[]>;

  generalRegForm: UntypedFormGroup;
  faCheckCircle = faCheckCircle;
  faTimes = faTimes;
  faCheck = faCheck;
  isExportShop: boolean;
  disableCountryCodeOtherB2B$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  isVatValidationLoading$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  vatIdValidation$: BehaviorSubject<VatValidationData> = new BehaviorSubject<VatValidationData>(null);
  displayNextSteps$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isEmailExist$: BehaviorSubject<{ isEmailExist: boolean }> = new BehaviorSubject({ isEmailExist: false });

  private subscription: Subscription = new Subscription();

  private href: string = this.router.url;

  constructor(
    private fb: UntypedFormBuilder,
    private renderer: Renderer2,
    private cookieService: DistCookieService,
    private registerService: RegisterService,
    private winRef: WindowRef,
    private router: Router,
    private registerServiceHelper: RegisterServiceHelper,
    private countryService: CountryService,
    private phoneNumberService: PhoneNumberService,
    private config: SiteContextConfig,
    private changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.isExportShop = isActiveSiteInternational(this.config);
    this.generalRegForm = this.fb.group({
      type: new UntypedFormControl('B2B'),
      companyname: new UntypedFormControl('', [Validators.required, Validators.maxLength(105)]),
      titleCode: new UntypedFormControl('', Validators.required),
      firstName: new UntypedFormControl('', {
        validators: Validators.required,
        updateOn: 'blur',
      }),
      lastName: new UntypedFormControl('', {
        validators: Validators.required,
        updateOn: 'blur',
      }),
      countryCode: new UntypedFormControl(this.activeSiteId, [Validators.required]),
      uid: new UntypedFormControl('', {
        validators: [Validators.required, Validators.email],
        updateOn: 'blur',
      }),
      password: new UntypedFormControl('', {
        validators: [Validators.required, Validators.minLength(6)],
      }),
      checkPwd: new UntypedFormControl('', [Validators.required, Validators.pattern('^[^s]+(s+[^s]+)*$')]),

      marketingConsent: new UntypedFormControl(false),
      smsConsent: new UntypedFormControl(false),
      phoneConsent: new UntypedFormControl(false),
      postConsent: new UntypedFormControl(false),
      personalisationConsent: new UntypedFormControl(false),
      profilingConsent: new UntypedFormControl(false),
      termsOfUseOption: new UntypedFormControl(false, Validators.requiredTrue),
    });

    // If the reg form needs country input, we create a new control which will contain
    // the new selected country value, so we still know which webshop user is on
    if (
      this.isExportShop ||
      this.generalRegForm.get('countryCode').value === 'CH' ||
      this.generalRegForm.get('countryCode').value === 'LI' ||
      this.generalRegForm.get('countryCode').value === 'IT' ||
      this.generalRegForm.get('countryCode').value === 'SM' ||
      this.generalRegForm.get('countryCode').value === 'VA'
    ) {
      this.generalRegForm.addControl('countryCodeOther', new UntypedFormControl('', [Validators.required]));
    }

    if (this.generalRegForm.get('countryCode').value === 'IT') {
      this.generalRegForm.addControl(
        'vat4',
        new UntypedFormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(7)]),
      );
    }

    this.injectFunctionCode();
    this.injectBizCountry();
    this.injectInvoiceEmail();
    this.listenToDisplayChange();
    this.triggerVatValidators();

    this.generalRegForm.addControl(
      'phoneNumber',
      new UntypedFormControl('', {
        asyncValidators: [
          this.phoneNumberService.createPhoneNumberValidator(
            this.generalRegForm.get('countryCodeOther') ?? this.generalRegForm.get('countryCode'),
          ),
        ],
        updateOn: 'blur',
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  // When user clicks "Yes", we create a new control inside of the form
  // We also update the array of controls to validate this field when any input is clicked
  triggerCustNo(value: boolean) {
    this.registerService.accountExists = value;
    this.displayNextSteps$.next(false);
    if (value) {
      this.generalRegForm.addControl('customerId', new UntypedFormControl(''));
      this.generalRegForm.get('customerId').enable();
    } else {
      this.registerService.isCustomerExist = false;
      this.generalRegForm.get('customerId')?.disable();
    }

    if (this.generalRegForm.get('countryCode').value === 'IT') {
      this.handleItalyFields(!value);
    }

    if (this.registerService.isCountryWithOrgNumber(this.generalRegForm.get('countryCode').value)) {
      this.handleControlForCustomerNumber(!value);
    }
  }

  validateCompanyDetails(): void {
    if (this.generalRegForm.get('vatId')) {
      this.isVatValidationLoading$.next(true);
      this.registerService
        .validateVAT(this.getCountryCode(this.isExportShop), this.generalRegForm.get('vatId').value)
        .pipe(
          take(1),
          catchError(() => of(true)),
        )
        .subscribe((isValid: boolean) => {
          if (isValid) {
            this.vatIdValidation$.next(this.updateVatValidationAfterSubmit(true));
            this.generalRegForm.get('vatId').setErrors(null);
            this.disableCountryCodeOtherB2B$.next(this.isExportShop);
          } else {
            this.vatIdValidation$.next(this.updateVatValidationAfterSubmit(false));
            this.generalRegForm.get('vatId').setErrors({ isInvalidVAT: true });
          }
          this.isVatValidationLoading$.next(false);
          this.changeDetectorRef.detectChanges();
          this.validateCompanyDetailsWithoutVat();
        });
    } else {
      this.validateCompanyDetailsWithoutVat();
    }
  }

  countrySelected(country: string) {
    this.setCountryPref(country);
    this.triggerVatValidators();
  }

  // Go through previous inputs of the selected one and check if they are empty
  // Scroll to the first input that comes as invalid
  // Set error to incorrect and mark the input as touched when it is invalid
  // So when user types into invalid input, the validation is activated
  onControlTouch(controlName: string) {
    const elements = this.winRef?.document.querySelectorAll('.js-validate');

    if (elements) {
      const array = [];
      let skip = false;
      elements.forEach((key) => {
        // @ts-ignore
        const formControlName = key.attributes.formcontrolname.nodeValue;
        const optional = key.classList.contains('js-validate-optional');
        if (!skip && !optional) {
          array.push(formControlName);
        } else {
          if (controlName === formControlName) {
            skip = true;
          }
        }
      });

      let scrollActivated = false;

      array.some((key) => {
        if (key === controlName) {
          return true;
        }

        this.generalRegForm.get(key).markAsTouched();

        if (this.generalRegForm.get(key).invalid) {
          // Scroll to the first invalid input
          const element = this.renderer.selectRootElement(`#${key + this.generalRegForm.get('type').value}`, true);

          if (!scrollActivated) {
            element.scrollIntoView({
              behavior: 'smooth',
              block: 'center',
              inline: 'nearest',
            });
            scrollActivated = true;
          }
        }
      });
    }
  }

  private updateVatValidationAfterSubmit(isVatValid: boolean) {
    return {
      ...this.vatIdValidation$.getValue(),
      isVatValid,
      isVatValidated: true,
    };
  }

  private validateCompanyDetailsWithoutVat() {
    this.validateCompanyName();
    this.validateOrganisationNumber();
    this.validateCodiceDestinarioAndLegalEmail();
    this.validateCountryInBiz();
    this.displayNextSteps$.next(this.areCompanyDetailsValid());
  }

  private setCountryPref(newItem: string) {
    const bizCountryValue = this.cookieService.get('bizCountryValue');
    if ((bizCountryValue === '' || bizCountryValue !== newItem) && this.isExportShop) {
      this.cookieService.set('bizCountryValue', newItem);
      this.generalRegForm.get('countryCode').setValue(newItem);
      this.countryService.setActive(newItem);
    }
  }

  private handleControlForCustomerNumber(value?: boolean): void {
    if (value) {
      this.generalRegForm.addControl('orgNumber', new UntypedFormControl('', this.setOrgNumberValidators()));
    } else {
      this.generalRegForm.removeControl('orgNumber');
    }
  }

  private setOrgNumberValidators(): ValidatorFn[] {
    const countryCode = this.generalRegForm.get('countryCode').value;
    return [
      Validators.required,
      Validators.pattern(this.registerServiceHelper.getOrgNumberPattern(countryCode)),
      Validators.maxLength(11),
    ];
  }

  private triggerVatValidators() {
    if (this.vatIdValidation$) {
      this.registerService
        .createVatIDValidators(this.generalRegForm.get('countryCode').value)
        .pipe(first())
        .subscribe((data) => {
          this.vatIdValidation$.next(data);
          if (this.isVatSupportedInCountry() && !this.generalRegForm.contains('vatId')) {
            this.generalRegForm.addControl('vatId', new UntypedFormControl(''));
          }
        });
    }
  }

  private injectFunctionCode() {
    if (
      Object.values(CountriesWithFunctionCode).includes(this.generalRegForm.get('countryCode').value) &&
      this.generalRegForm.get('type').value === 'B2B' &&
      this.href.indexOf('checkout') === -1
    ) {
      this.generalRegForm.addControl('functionCode', new UntypedFormControl('', [Validators.required]));
    }
  }

  private injectBizCountry() {
    const bizCountryCode = this.cookieService.get('bizCountryValue');
    if (this.isExportShop && bizCountryCode !== '') {
      this.generalRegForm.get('countryCodeOther').setValue(bizCountryCode);
      this.generalRegForm.get('countryCode').setValue(bizCountryCode);
      this.countryService.setActive(bizCountryCode);
    }
  }

  private injectInvoiceEmail() {
    if (this.isSelectedCountryNotItaly() && this.generalRegForm.get('type').value === 'B2B') {
      this.generalRegForm.addControl(
        'invoiceEmail',
        new UntypedFormControl('', {
          validators: [Validators.email],
          updateOn: 'blur',
        }),
      );
    }
  }

  private isSelectedCountryNotItaly(): boolean {
    return (
      this.generalRegForm.get('countryCode').value !== CountryCodesEnum.ITALY &&
      this.generalRegForm.get('countryCode').value !== CountryCodesEnum.SAN_MARINO &&
      this.generalRegForm.get('countryCode').value !== CountryCodesEnum.VATICAN
    );
  }

  private listenToDisplayChange() {
    this.subscription.add(
      this.displayNextSteps$.asObservable().subscribe((val) => {
        if (val) {
          // We need the timeout for the about-you-form components to render first
          setTimeout(() => {
            this.scrollIntoView('phoneNumberB2B');
          }, 500);
        }
      }),
    );
  }

  get isValidNumberFound(): boolean {
    return this.registerService.isCustomerNumberFound;
  }

  get accountExists(): boolean {
    return this.registerService.accountExists;
  }

  private handleItalyFields(value?: boolean): void {
    if (value) {
      this.generalRegForm.addControl(
        'vat4',
        new UntypedFormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(7)]),
      );
      this.triggerVatValidators();
    } else {
      this.generalRegForm.removeControl('vat4');
      this.generalRegForm.removeControl('vatId');
    }
  }

  private areCompanyDetailsValid(): boolean {
    return (
      !this.generalRegForm.get('orgNumber')?.errors &&
      !this.generalRegForm.get('companyname').errors &&
      !this.generalRegForm.get('vatId')?.errors &&
      !this.generalRegForm.get('vat4')?.errors &&
      !this.generalRegForm.get('legalEmail')?.errors &&
      !this.generalRegForm.get('countryCodeOther')?.errors
    );
  }

  private validateOrganisationNumber(): void {
    if (this.generalRegForm.get('orgNumber')) {
      this.generalRegForm.get('orgNumber').markAsTouched();
      if (this.generalRegForm.get('orgNumber').errors) {
        this.generalRegForm.get('orgNumber').setErrors({ invalid: true });
      } else {
        this.generalRegForm.get('orgNumber').setErrors(null);
      }
    }
  }

  private validateCompanyName(): void {
    if (this.generalRegForm.get('companyname').errors) {
      this.generalRegForm.get('companyname').markAsTouched();
      this.generalRegForm.get('companyname').setErrors({ invalid: true });
    }
  }

  private isVatSupportedInCountry(): boolean {
    return !Object.values(CountriesWithoutVat).includes(this.generalRegForm.get('countryCode').value);
  }

  private validateCodiceDestinarioAndLegalEmail(): void {
    if (this.generalRegForm.get('legalEmail')) {
      this.generalRegForm.get('legalEmail').markAsTouched();
      if (this.generalRegForm.get('legalEmail').errors) {
        this.generalRegForm.get('legalEmail').setErrors({ invalid: true });
      }
    }

    if (this.generalRegForm.get('vat4')) {
      this.generalRegForm.get('vat4').markAsTouched();
      if (this.generalRegForm.get('vat4').errors) {
        this.generalRegForm.get('vat4').setErrors({ invalid: true });
      }
    }
  }

  private validateCountryInBiz(): void {
    if (this.generalRegForm.get('countryCodeOther')) {
      this.generalRegForm.get('countryCodeOther').markAsTouched();
      if (this.generalRegForm.get('countryCodeOther').errors) {
        this.generalRegForm.get('countryCodeOther').setErrors({ invalid: true });
      }
    }
  }

  private getCountryCode(isExportShop: boolean): string {
    return isExportShop
      ? this.generalRegForm.get('countryCodeOther').value
      : this.generalRegForm.get('countryCode').value;
  }

  private scrollIntoView(elem: string) {
    const element = this.winRef.document.querySelector('#' + elem);
    if (element) {
      element.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
        inline: 'nearest',
      });
    }
  }
}
