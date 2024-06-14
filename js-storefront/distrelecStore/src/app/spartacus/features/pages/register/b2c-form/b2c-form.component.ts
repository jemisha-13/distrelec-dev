import { Component, Input, OnInit, Renderer2 } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { WindowRef } from '@spartacus/core';
import { BehaviorSubject } from 'rxjs';
import { DistCookieService } from '@services/dist-cookie.service';
import { CountryCodesEnum, CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { RegisterService } from '@services/register.service';
import { PhoneNumberService } from '@services/phonenumber.service';

@Component({
  selector: 'app-b2c-form',
  templateUrl: './b2c-form.component.html',
  styleUrls: ['./b2c-form.component.scss'],
})
export class B2cFormComponent implements OnInit {
  @Input() stepsList_: BehaviorSubject<{ key: string; active: boolean }[]>;
  @Input() activeSiteId: string;

  generalRegForm: UntypedFormGroup;

  isEmailExist$: BehaviorSubject<{ isEmailExist: boolean }> = new BehaviorSubject({ isEmailExist: false });

  constructor(
    private fb: UntypedFormBuilder,
    private renderer: Renderer2,
    private winRef: WindowRef,
    private cookieService: DistCookieService,
    private registerService: RegisterService,
    private phoneNumberService: PhoneNumberService,
    private countryService: CountryService,
  ) {}

  setCountryPref() {
    const bizCountryValue = this.cookieService.get('bizCountryValue');
    const selectedBizCountry = this.generalRegForm.get('countryCodeOther').value;
    if (
      (bizCountryValue === '' || bizCountryValue !== selectedBizCountry) &&
      this.registerService.returnCountryCodeEU(this.generalRegForm.get('countryCode').value)
    ) {
      this.cookieService.set('bizCountryValue', selectedBizCountry);
      this.generalRegForm.get('countryCode').setValue(selectedBizCountry);
      this.countryService.setActive(selectedBizCountry);
    }
  }

  injectBizCountry() {
    const bizCountryCode = this.cookieService.get('bizCountryValue');
    if (this.generalRegForm.get('countryCode').value === CountryCodesEnum.EXPORT && bizCountryCode !== '') {
      this.generalRegForm.get('countryCodeOther').setValue(bizCountryCode);
    }
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

  ngOnInit() {
    this.generalRegForm = this.fb.group({
      type: new UntypedFormControl('B2C'),
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
      password: new UntypedFormControl('', [Validators.required, Validators.minLength(6)]),
      checkPwd: new UntypedFormControl('', [Validators.required]),

      marketingConsent: new UntypedFormControl(false),
      smsConsent: new UntypedFormControl(false),
      phoneConsent: new UntypedFormControl(false),
      postConsent: new UntypedFormControl(false),
      personalisationConsent: new UntypedFormControl(false),
      profilingConsent: new UntypedFormControl(false),
      termsOfUseOption: new UntypedFormControl(false, Validators.requiredTrue),
    });

    if (
      this.registerService.returnCountryCodeEU(this.generalRegForm.get('countryCode').value) ||
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
        'codiceFiscale',
        new UntypedFormControl('', [Validators.required, Validators.minLength(16)]),
      );
    }

    this.injectBizCountry();

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
}
