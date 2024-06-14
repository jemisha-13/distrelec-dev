import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { faAngleLeft, faAngleRight, faPlus } from '@fortawesome/free-solid-svg-icons';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { Observable, Subscription } from 'rxjs';
import { CountryResponse, TitleList } from '@model/my-account.model';
import { distinctUntilChanged, take } from 'rxjs/operators';
import { GlobalMessageService, GlobalMessageType, OccEndpointsService } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { phonePrefixList, validateAllFormFields } from '@helpers/form-helper';
import { PostalValidation } from '@helpers/postal-validations';

@Component({
  selector: 'app-add-edit-addresses',
  templateUrl: './add-edit-addresses.component.html',
  styleUrls: ['./add-edit-addresses.component.scss'],
})
export class AddEditAddressesComponent implements OnInit, AfterViewInit, OnDestroy {
  addEditAddressForm = new UntypedFormGroup({
    streetName: new UntypedFormControl('', [Validators.required]),
    streetNr: new UntypedFormControl('', [Validators.required, Validators.maxLength(10)]),
    postalCode: new UntypedFormControl('', [Validators.required]),
    town: new UntypedFormControl('', [Validators.required]),
    phoneNumber: new UntypedFormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
    country: new UntypedFormControl('', [Validators.required]),
  });

  buttonText = 'form.save';
  responseType = '';
  responseMessage = '';
  isDisabled = false;
  faPlus = faPlus;
  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;
  address: any = [];
  currentChannel_ = this.siteSettingsService.currentChannelData$;
  countryList$: Observable<CountryResponse> = this.optionSetListService.getCountryList();
  countryList: any = [];
  siteSettings: any;
  phonePrefix = '';
  submitAttempt = true;
  regionList: any = [];

  addressDetails = {
    title: 'mr',
    firstName: '',
    lastName: '',
    companyName: '',
    companyName2: '',
    streetName: '',
    streetNr: '',
    postalCode: '',
    town: '',
    country: '',
    phoneNumber: '',
    mobileNumber: '',
    faxNumber: '',
    region: '',
  };
  addEditAddressSubtitle = 'form.add_shipping_address';
  userDetails$ = this.distrelecUserService.userDetails_;
  userDetails: any;
  titleList$: Observable<TitleList> = this.optionSetListService.getTitleList();
  titleList: any = [];
  showConfirm = false;
  navigationMessage: NavigationExtras = { state: { data: 'added' } };
  addressId: string;
  loadedAddress = false;
  removableAddress = false;
  isBillingAddress = false;
  addressType: string;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private distrelecUserService: DistrelecUserService,
    private optionSetListService: OptionSetListService,
    private siteSettingsService: AllsitesettingsService,
    private router: Router,
    private route: ActivatedRoute,
    private globalMessageService: GlobalMessageService,
    private postalValidation: PostalValidation,
  ) {}

  ngOnInit() {
    this.userDetails$.pipe(take(1)).subscribe((data) => {
      this.userDetails = data;
    });

    this.subscriptions.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        const currentCountry = this.siteSettings?.country ?? 'EX';
        this.phonePrefix = phonePrefixList.find((country) => country.isocode === currentCountry)?.prefix ?? '+41';
      }),
    );

    this.addressId = this.route.snapshot.paramMap.get('addressId');
    this.addressType = this.route.snapshot.paramMap.get('addressType');
    if (this.addressId) {
      this.fetchCountriesAndRegions(false);
      this.http
        .get<any>(this.occEndpoints.buildUrl(`/users/current/addresses/${this.addressId}`))
        .pipe(take(1))
        .subscribe(
          (data: any) => {
            if (data) {
              this.address = data;
              this.loadedAddress = true;

              this.addressDetails = {
                title: this.address?.titleCode,
                firstName: this.address?.firstName,
                lastName: this.address?.lastName,
                companyName: this.address?.companyName,
                companyName2: this.address?.companyName2,
                streetName: this.address?.line1,
                streetNr: this.address?.line2,
                postalCode: this.address?.postalCode,
                town: this.address?.town,
                country: this.address?.country.isocode,
                phoneNumber: this.address?.phone,
                mobileNumber: this.address?.cellphone,
                faxNumber: this.address?.fax,
                region: this.address?.region?.isocode,
              };

              if (this.address) {
                this.isBillingAddress = this.address.billingAddress;
                this.addEditAddressSubtitle = this.address.billingAddress
                  ? 'form.edit_billing_address'
                  : 'form.edit_shipping_address';
                this.removableAddress = !this.address.billingAddress;
              }
            }
          },
          (response) => {
            this.responseType = 'danger'; //error
            response?.error?.errors.forEach((err) => {
              this.responseMessage = err.message;
            });
            this.loadedAddress = true;
          },
        );
    } else {
      this.fetchCountriesAndRegions(true);
      this.loadedAddress = true;
    }

    this.titleList$.pipe(take(1)).subscribe((res) => {
      this.titleList = res;
    });

    if (this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT') {
      this.addEditAddressForm.addControl('companyName', new UntypedFormControl('', [Validators.required]));
      this.addEditAddressForm.addControl('companyName2', new UntypedFormControl(''));
      this.addEditAddressForm.addControl('faxNumber', new UntypedFormControl(''));

      if (!this.addressId || this.addressType === 'b2bshipping') {
        this.addEditAddressForm.addControl(
          'mobileNumber',
          new UntypedFormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
        );
        this.mobileOrPhoneValidation();
      } else {
        this.addEditAddressForm.addControl(
          'mobileNumber',
          new UntypedFormControl('', [Validators.pattern('[- +()0-9]+')]),
        );
      }
    }

    if (this.userDetails?.customerType === 'B2C') {
      this.addEditAddressForm.addControl('title', new UntypedFormControl('', [Validators.required]));
      this.addEditAddressForm.addControl('firstName', new UntypedFormControl('', [Validators.required]));
      this.addEditAddressForm.addControl('lastName', new UntypedFormControl('', [Validators.required]));
      this.addEditAddressForm.addControl(
        'mobileNumber',
        new UntypedFormControl('', [Validators.pattern('[- +()0-9]+')]),
      );
    }

    this.isDisabled = false;
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  fetchCountriesAndRegions(preselected: boolean): void {
    this.countryList$.pipe(take(1)).subscribe((res) => {
      this.countryList = res.countries;
      if (this.countryList?.length > 0) {
        this.regionList = this.countryList[0].regions;
        if (this.regionList?.length > 0) {
          this.addEditAddressForm.addControl('region', new UntypedFormControl('', [Validators.required]));
        }

        if (preselected) {
          this.addressDetails.country =
            this.countryList.find((country) => country.isocode === 'CH' || country.isocode === 'IT')?.isocode ??
            this.countryList[0].isocode;
          this.addressDetails.region = this.regionList?.length > 0 ? this.regionList[0].isocode : null;
        }
      }
    });
  }

  cancelAddEditAddress() {
    this.router.navigateByUrl('/my-account/addresses');
  }

  saveAddEditAddress() {
    if (this.addEditAddressForm.valid) {
      this.buttonText = 'form.please_wait';
      this.isDisabled = true;
      this.responseMessage = '';
      this.responseType = '';
      //prepare data to be posted
      const request =
        this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT'
          ? this.b2bRequest()
          : this.b2cRequest();

      if (this.address?.id) {
        this.updateAddress(request);
      } else {
        this.addNewAddress(request);
      }
    } else {
      validateAllFormFields(this.addEditAddressForm);
    }
  }

  addNewAddress(request: any) {
    this.http
      .post(this.occEndpoints.buildUrl(`/users/current/addresses`), request)
      .pipe(take(1))
      .subscribe(
        () => {
          //save address success
          this.router.navigate(['my-account/addresses'], this.navigationMessage);
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.globalMessageService.add(err.message, GlobalMessageType.MSG_TYPE_ERROR);
          });

          this.buttonText = 'form.save';
          this.isDisabled = false;
        },
        () => {
          //completed
        },
      );
  }

  updateAddress(request: any) {
    this.http
      .patch(this.occEndpoints.buildUrl(`/users/current/addresses/${request.addressId}`), request)
      .pipe(take(1))
      .subscribe(
        () => {
          //save address success
          this.navigationMessage = { state: { data: 'updated' } };
          this.router.navigate(['my-account/addresses'], this.navigationMessage);
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.globalMessageService.add(err.message, GlobalMessageType.MSG_TYPE_ERROR);
          });

          this.buttonText = 'form.save';
          this.isDisabled = false;
        },
        () => {
          //completed
        },
      );
  }

  deleteAddress() {
    this.showConfirm = true;
  }

  b2cRequest() {
    return {
      country: {
        isocode: this.addressDetails.country,
      },
      firstName: this.addressDetails.firstName,
      lastName: this.addressDetails.lastName,
      line1: this.addressDetails.streetName,
      line2: this.addressDetails.streetNr,
      cellphone: this.addressDetails.mobileNumber,
      phone: this.addressDetails.phoneNumber,
      postalCode: this.postalValidation.formatPostalCode(this.addressDetails.postalCode, this.addressDetails.country),
      titleCode: this.addressDetails.title,
      town: this.addressDetails.town,
      addressId: this.address?.id ?? '',
      region: this.addressDetails.region ? { isocode: this.addressDetails.region } : null,
    };
  }

  b2bRequest() {
    return {
      country: {
        isocode: this.addressDetails.country,
      },
      companyName: this.addressDetails.companyName,
      companyName2: this.addressDetails.companyName2,
      line1: this.addressDetails.streetName,
      line2: this.addressDetails.streetNr,
      cellphone: this.addressDetails.mobileNumber,
      phone: this.addressDetails.phoneNumber,
      postalCode: this.postalValidation.formatPostalCode(this.addressDetails.postalCode, this.addressDetails.country),
      town: this.addressDetails.town,
      fax: this.addressDetails.faxNumber,
      addressId: this.address?.id ?? '',
      region: this.addressDetails.region ? { isocode: this.addressDetails.region } : null,
    };
  }

  confirmedDeleteAddress(returnedEvent: any) {
    this.responseType = '';
    this.responseMessage = '';

    if (returnedEvent === 'cancelled') {
      this.showConfirm = false;
    } else {
      this.http
        .delete(this.occEndpoints.buildUrl(`/users/current/addresses/${this.address?.id}`))
        .pipe(take(1))
        .subscribe(
          () => {
            //save as default success
            this.navigationMessage = { state: { data: 'deleted' } };
            this.router.navigate(['my-account/addresses'], this.navigationMessage);
            this.showConfirm = false;
          },
          (response) => {
            this.responseType = 'danger'; //error
            response?.error?.errors.forEach((err) => {
              this.responseMessage = err.message;
              this.showConfirm = false;
            });
          },
          () => {
            //completed
          },
        );
    }
  }

  mobileOrPhoneValidation() {
    //if phone is added

    this.subscriptions.add(
      this.addEditAddressForm
        .get('phoneNumber')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.addEditAddressForm.get('mobileNumber').clearValidators();
          } else {
            this.addEditAddressForm.get('mobileNumber').setValidators(Validators.required);
          }
          this.addEditAddressForm.get('mobileNumber').updateValueAndValidity();
        }),
    );

    //if mobile is added
    this.subscriptions.add(
      this.addEditAddressForm
        .get('mobileNumber')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.addEditAddressForm.get('phoneNumber').clearValidators();
          } else {
            this.addEditAddressForm.get('phoneNumber').setValidators(Validators.required);
          }
          this.addEditAddressForm.get('phoneNumber').updateValueAndValidity();
        }),
    );
  }
}
