import { AfterContentChecked, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { faAngleRight, faCheck, faTimes } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { OccEndpointsService, TranslationService, WindowRef } from '@spartacus/core';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { DepartmentList, FunctionList, TitleList } from '@model/my-account.model';
import { distinctUntilChanged, first, tap } from 'rxjs/operators';
import { CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { phonePrefixList, validateAllFormFields } from '@helpers/form-helper';
import { DistCountryCode, PhoneNumberService } from '@services/phonenumber.service';

@Component({
  selector: 'app-account-user-information',
  templateUrl: './account-user-information.component.html',
  styleUrls: ['./account-user-information.component.scss'],
})
export class AccountUserInformationComponent implements OnInit, OnDestroy, AfterContentChecked {
  userProfileChangeForm = new UntypedFormGroup({
    firstName: new UntypedFormControl('', [Validators.required]),
    lastName: new UntypedFormControl('', [Validators.required]),
    phone: new UntypedFormControl('', [Validators.required]),
    mobilePhone: new UntypedFormControl('', [Validators.required]),
    fax: new UntypedFormControl(false),
    userDepartment: new UntypedFormControl(false),
    userFunction: new UntypedFormControl(false),
    title: new UntypedFormControl(false),
  });

  faAngleRight = faAngleRight;
  faTimes = faTimes;
  faCheck = faCheck;
  functionList$: Observable<FunctionList> = this.optionSetListService.getFunctionList();
  departmentList$: Observable<DepartmentList> = this.optionSetListService.getDepartmentList();
  titleList$: Observable<TitleList> = this.optionSetListService.getTitleList();
  selectLabel$ = this.translation.translate('form.select_empty');
  userUpdatedDetails: any = [];
  functionList: any = [];
  departmentList: any = [];
  titleList: any = [];
  siteCountryCode$: Observable<string> = this.countryService.getActive();
  buttonDisabled = false;
  responseType;
  responseMessage;
  updating = false;
  userDetails_: BehaviorSubject<any>;
  userDepartmentCode = '';
  userFunctionCode = '';
  countryCode: DistCountryCode;
  userRoles: any;
  phonePrefix = '';

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private optionSetListService: OptionSetListService,
    private distrelecUserService: DistrelecUserService,
    private cdRef: ChangeDetectorRef,
    private countryService: CountryService,
    private translation: TranslationService,
    private winRef: WindowRef,
    private phoneNumberService: PhoneNumberService,
  ) {}

  markFieldAsInvalid(controlName) {
    this.userProfileChangeForm.get(controlName).setErrors({ incorrect: true });
    this.userProfileChangeForm.get(controlName).markAsTouched();
  }

  validatePhoneNumber(controlName) {
    const value = this.userProfileChangeForm.get(controlName).value;

    this.phoneNumberService
      .isValidPhoneNumber(value, this.countryCode)
      .pipe(
        first(),
        tap((isValid) => {
          if (!isValid) {
            this.markFieldAsInvalid(controlName);
          } else {
            this.userProfileChangeForm.get(controlName).setErrors(null);
          }
        }),
      )
      .subscribe();
  }

  ngOnInit() {
    //get active country
    this.subscriptions.add(
      this.siteCountryCode$.subscribe((data: DistCountryCode) => {
        this.countryCode = data;
        const currentCountry = this.countryCode ?? 'EX';
        this.phonePrefix = phonePrefixList.find((country) => country.isocode === currentCountry)?.prefix ?? '+41';
      }),
    );

    this.userDetails_ = this.distrelecUserService.userDetails_;
    this.subscriptions.add(
      this.userDetails_.subscribe((data) => {
        this.userUpdatedDetails = data;
        this.userRoles = data?.roles;
        this.userFunctionCode = this.userUpdatedDetails?.functionCode ?? '';
        this.userDepartmentCode = this.userUpdatedDetails?.contactAddress?.departmentCode ?? '';
      }),
    );

    if (
      this.userUpdatedDetails?.customerType === 'B2B' ||
      this.userUpdatedDetails?.customerType === 'B2B_KEY_ACCOUNT'
    ) {
      combineLatest([this.functionList$, this.selectLabel$])
        .pipe(first())
        .subscribe(([fetchedFunctions, selectLabel]) => {
          this.functionList = fetchedFunctions;
          this.functionList.functions.unshift({ code: '', name: selectLabel, disabled: true });
        });

      combineLatest([this.departmentList$, this.selectLabel$])
        .pipe(first())
        .subscribe(([fetchedDepartments, selectLabel]) => {
          this.departmentList = fetchedDepartments;
          this.departmentList.departments.unshift({ code: '', name: selectLabel, disabled: true });
        });
    }

    this.titleList$.pipe(first()).subscribe((res) => {
      this.titleList = res;
    });

    this.mobileOrPhoneValidation();
  }

  ngAfterContentChecked() {
    this.cdRef.detectChanges();
  }

  changeUserInformation() {
    if (this.userProfileChangeForm.valid) {
      this.updating = true;
      this.buttonDisabled = true;
      this.responseMessage = '';
      this.responseType = '';

      const updatedUserInformation = {
        contactAddress: {
          cellphone: this.userUpdatedDetails.contactAddress.cellphone,
          phone: this.userUpdatedDetails.contactAddress.phone,
          fax: this.userUpdatedDetails.contactAddress.fax,
          departmentCode: this.userDepartmentCode,
        },
        titleCode: this.userUpdatedDetails.titleCode,
        firstName: this.userUpdatedDetails.contactAddress.firstName,
        lastName: this.userUpdatedDetails.contactAddress.lastName,
        uid: this.userUpdatedDetails.uid,
        functionCode: this.userFunctionCode,
      };

      //let's update the details now
      this.subscriptions.add(
        this.http
          .patch<any>(this.occEndpoints.buildUrl(`/users/current`), updatedUserInformation)
          .pipe(first())
          .subscribe(
            () => {
              //update user information on successful updating
              this.distrelecUserService.getUserInformation();
            },
            (response) => {
              this.responseType = 'danger'; //error
              response?.error?.errors.forEach((err) => {
                this.responseMessage = err.message;
                this.scrollToTop();
              });

              this.updating = false;
              this.buttonDisabled = false;
            },
            () => {
              //completed
              this.responseType = 'success';
              this.translation
                .translate('account.confirmation.profile.updated')
                .pipe(first())
                .subscribe((val) => (this.responseMessage = val));
              this.scrollToTop();
              this.updating = false;
              this.buttonDisabled = false;
            },
          ),
      );
    } else {
      validateAllFormFields(this.userProfileChangeForm);
    }
  }

  scrollToTop() {
    if (this.winRef.isBrowser()) {
      this.winRef.nativeWindow.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
      });
    }
  }

  mobileOrPhoneValidation() {
    this.subscriptions.add(
      this.userProfileChangeForm
        .get('phone')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.userProfileChangeForm.get('mobilePhone').clearValidators();
          } else {
            this.userProfileChangeForm.get('mobilePhone').setValidators(Validators.required);
          }
          this.userProfileChangeForm.get('mobilePhone').updateValueAndValidity();
        }),
    );

    this.subscriptions.add(
      this.userProfileChangeForm
        .get('mobilePhone')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.userProfileChangeForm.get('phone').clearValidators();
          } else {
            this.userProfileChangeForm.get('phone').setValidators(Validators.required);
          }
          this.userProfileChangeForm.get('phone').updateValueAndValidity();
        }),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  findRole(role: string) {
    return this.userRoles.indexOf(role) > -1;
  }
}
