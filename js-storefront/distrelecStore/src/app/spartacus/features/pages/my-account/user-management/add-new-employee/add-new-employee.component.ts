import { HttpClient } from '@angular/common/http';
import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewEncapsulation,
} from '@angular/core';
import { faAngleLeft, faAngleRight, faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';
import { GlobalMessageService, GlobalMessageType, OccEndpointsService, WindowRef } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { distinctUntilChanged, first, take } from 'rxjs/operators';
import { OptionSetListService } from 'src/app/spartacus/services/option-set-list.service';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { DepartmentList, FunctionList, TitleList } from '@model/my-account.model';
import { AllsitesettingsService } from 'src/app/spartacus/services/allsitesettings.service';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { phonePrefixList, validateAllFormFields } from '@helpers/form-helper';

@Component({
  selector: 'app-my-account-add-new-employee',
  templateUrl: './add-new-employee.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./add-new-employee.component.scss'],
})
export class AddNewEmployeeComponent implements OnInit, OnDestroy {
  @Output() addNewEmployeeEvent = new EventEmitter<string>();
  @Input() employee;

  functionList$: Observable<FunctionList> = this.optionSetListService.getFunctionList();
  departmentList$: Observable<DepartmentList> = this.optionSetListService.getDepartmentList();
  titleList$: Observable<TitleList> = this.optionSetListService.getTitleList();
  crossIcon = faXmark;
  checkIcon = faCheck;
  responseEmployeeType = '';
  responseEmployeeMessage = '';
  showConfirm = false;
  addEmployeeUrl = '/users/current/user-management/create-employee';
  apiRequestUrl = '';
  faAngleLeft = faAngleLeft;
  faAngleRight = faAngleRight;
  loadingResendEmail = false;
  currencyName = 'EUR';
  currentChannel_ = this.siteSettingsService.currentChannelData$;
  siteSettings: any;
  phonePrefix = '';
  navigationMessage: NavigationExtras = { state: { data: 'added' } };
  isSaving = false;
  userId: string;

  addNewEmployee = {
    uid: '',
    customerId: '',
    titleCode: 'mr',
    firstName: '',
    lastName: '',
    departmentCode: '',
    functionCode: '',
    email: '',
    phoneNumber: '',
    mobileNumber: '',
    faxNumber: '',
    budgetWithoutLimit: true,
    budgetPerOrder: '',
    yearlyBudget: '',
    requestQuotationPermission: false,
    residualBudget: '',
  };

  functionList: any = [];
  departmentList: any = [];
  titleList: any = [];

  addNewEmployeeForm = new UntypedFormGroup({
    title: new UntypedFormControl('', [Validators.required]),
    faxNumber: new UntypedFormControl(''),
    departmentCode: new UntypedFormControl(''),
    functionCode: new UntypedFormControl(''),
    budgetWithoutLimit: new UntypedFormControl(''),
    budgetPerOrder: new UntypedFormControl(''),
    yearlyBudget: new UntypedFormControl(''),
    quotationRequest: new UntypedFormControl(''),
    firstName: new UntypedFormControl('', [Validators.required]),
    lastName: new UntypedFormControl('', [Validators.required]),
    email: new UntypedFormControl('', [Validators.required, Validators.email]),
    phoneNumber: new UntypedFormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
    mobilePhone: new UntypedFormControl('', [Validators.required, Validators.pattern('[- +()0-9]+')]),
  });

  private subscription: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private optionSetListService: OptionSetListService,
    private winRef: WindowRef,
    private cdr: ChangeDetectorRef,
    private siteSettingsService: AllsitesettingsService,
    private globalMessageService: GlobalMessageService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit() {
    this.userId = this.route.snapshot.paramMap.get('email');
    this.functionList$.pipe(first()).subscribe((res) => {
      this.functionList = res;
    });

    //subscribing to departments observable
    this.departmentList$.pipe(first()).subscribe((res) => {
      this.departmentList = res;
    });

    this.titleList$.pipe(take(1)).subscribe((res) => {
      this.titleList = res;
    });

    if (this.userId) {
      this.http
        .get<any>(this.occEndpoints.buildUrl(`/users/current/user-management/employee-details/${this.userId}`))
        .pipe(take(1))
        .subscribe(
          (data: any) => {
            if (data) {
              this.employee = data;

              this.addNewEmployee = this.employee;
              this.addNewEmployee.customerId = this.employee.customerId;
              this.addNewEmployee.email = this.employee.uid;
              this.addNewEmployee.functionCode = this.employee.functionCode ?? '';
              this.addNewEmployee.departmentCode = this.employee.contactAddress?.departmentCode ?? '';
              this.addNewEmployee.phoneNumber = this.employee.contactAddress?.phone1;
              this.addNewEmployee.mobileNumber = this.employee.contactAddress?.cellphone;
              this.addNewEmployee.faxNumber = this.employee.contactAddress?.fax;
              this.addNewEmployee.yearlyBudget = this.employee.budget?.budget;
              this.addNewEmployee.budgetPerOrder = this.employee.budget?.orderBudget;
              this.addNewEmployee.residualBudget = this.employee.budget?.yearlyBudget;
              this.addNewEmployee.requestQuotationPermission = this.employee.requestQuotationPermission;
              this.currencyName = this.employee.currency?.isocode;

              if (this.addNewEmployee.budgetPerOrder == null && this.addNewEmployee.yearlyBudget == null) {
                this.addNewEmployee.budgetWithoutLimit = true;
              } else {
                this.addNewEmployee.budgetWithoutLimit = false;
              }
            }
          },
          (response) => {
            this.responseEmployeeType = 'danger'; //error
            response?.error?.errors.forEach((err) => {
              this.responseEmployeeMessage = err.message;
            });
          },
        );
    }

    this.cdr.detectChanges();
    this.subscription.add(
      this.currentChannel_.subscribe((data) => {
        this.siteSettings = data;
        this.currencyName = this.siteSettings?.currency;
        const currentCountry = this.siteSettings?.country ?? 'EX';
        this.phonePrefix = phonePrefixList.find((country) => country.isocode === currentCountry)?.prefix ?? '+41';
      }),
    );

    this.mobileOrPhoneValidation();
    this.budgetValidation();
  }

  /**
   * method  for adding new user
   */
  addEditNewUser() {
    if (this.addNewEmployeeForm.valid) {
      this.isSaving = true;
      this.responseEmployeeMessage = '';
      this.responseEmployeeType = '';

      this.apiRequestUrl =
        this.addNewEmployee.customerId === ''
          ? this.addEmployeeUrl
          : '/users/current/user-management/edit-employee/' + this.addNewEmployee.customerId;

      const postedData = {
        titleCode: this.addNewEmployee.titleCode,
        firstName: this.addNewEmployee.firstName,
        lastName: this.addNewEmployee.lastName,
        departmentCode: this.addNewEmployee.departmentCode,
        functionCode: this.addNewEmployee.functionCode,
        email: this.addNewEmployee.email,
        phoneNumber: this.addNewEmployee.phoneNumber,
        mobileNumber: this.addNewEmployee.mobileNumber,
        faxNumber: this.addNewEmployee.faxNumber,
        budgetWithoutLimit: this.addNewEmployee.budgetWithoutLimit,
        budgetPerOrder: this.addNewEmployee.budgetPerOrder,
        yearlyBudget: this.addNewEmployee.yearlyBudget,
        requestQuotationPermission: this.addNewEmployee.requestQuotationPermission,
        residualBudget: 0,
      };

      this.http
        .post<any>(this.occEndpoints.buildUrl(this.apiRequestUrl), postedData)
        .pipe(first())
        .subscribe(
          () => {
            if (this.addNewEmployee.customerId !== '') {
              this.navigationMessage = { state: { data: 'updated' } };
              this.router.navigate(['my-account/company/user-management'], this.navigationMessage);
            } else {
              this.router.navigate(['my-account/company/user-management'], this.navigationMessage);
            }
          },
          (response) => {
            this.responseEmployeeType = 'danger'; //error

            response?.error?.errors.forEach((err) => {
              this.globalMessageService.add(err.message, GlobalMessageType.MSG_TYPE_ERROR);
            });

            this.isSaving = false;
            this.scrollToTop();
            this.cdr.detectChanges();
          },
          () => {
            this.cdr.detectChanges();
          },
        );
    } else {
      validateAllFormFields(this.addNewEmployeeForm);
    }
  }

  goBack() {
    this.router.navigate(['my-account/company/user-management']);
  }

  deactivateUser() {
    this.http
      .post<any>(
        this.occEndpoints.buildUrl(`/users/current/user-management/deactivate/${this.addNewEmployee.customerId}`),
        {},
      )
      .pipe(take(1))
      .subscribe(
        () => {
          this.navigationMessage = { state: { data: 'deactivated' } };
          this.router.navigate(['my-account/company/user-management'], this.navigationMessage);
        },
        (response) => {
          this.responseEmployeeType = 'danger'; //error

          response?.error?.errors.forEach((err) => {
            this.responseEmployeeMessage = err.message;
          });

          response?.errors?.errors.forEach((err) => {
            this.responseEmployeeMessage = err.message;
          });

          this.cdr.detectChanges();
          this.scrollToTop();
        },
        () => {
          this.cdr.detectChanges();
        },
      );
  }

  resendActivationEmail() {
    this.loadingResendEmail = true;

    this.http
      .post<any>(
        this.occEndpoints.buildUrl(
          `/users/current/user-management/resend-activation/${this.addNewEmployee.customerId}`,
        ),
        {},
      )
      .pipe(take(1))
      .subscribe(
        (data) => {
          this.loadingResendEmail = false;
          this.responseEmployeeType = 'success';
          this.responseEmployeeMessage = data;
        },
        (response) => {
          this.responseEmployeeType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseEmployeeMessage = err.message;
          });
          this.loadingResendEmail = false;
        },
        () => {
          this.cdr.detectChanges();
        },
      );
  }

  deleteEmployee() {
    this.showConfirm = true;
  }

  confirmEmployeeDelete(returnedEvent: any) {
    this.responseEmployeeType = '';
    this.responseEmployeeMessage = '';

    if (returnedEvent === 'cancelled') {
      this.showConfirm = false;
    } else {
      this.http
        .delete<any>(
          this.occEndpoints.buildUrl(`/users/current/user-management/delete/${this.addNewEmployee?.customerId}`),
        )
        .pipe(take(1))
        .subscribe(
          () => {
            //save as default success
            this.navigationMessage = { state: { data: 'deleted' } };
            this.router.navigate(['my-account/company/user-management'], this.navigationMessage);
            this.showConfirm = false;
          },
          (response) => {
            this.responseEmployeeType = 'danger'; //error
            this.globalMessageService.add(response.error?.errors[0]?.message, GlobalMessageType.MSG_TYPE_ERROR);
          },
          () => {
            //completed
            this.cdr.detectChanges();
          },
        );

      this.scrollToTop();
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
    //if phone is added
    this.subscription.add(
      this.addNewEmployeeForm
        .get('phoneNumber')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.addNewEmployeeForm.get('mobilePhone').clearValidators();
          } else {
            this.addNewEmployeeForm.get('mobilePhone').setValidators(Validators.required);
          }
          this.addNewEmployeeForm.get('mobilePhone').updateValueAndValidity();
        }),
    );

    //if mobile is added
    this.subscription.add(
      this.addNewEmployeeForm
        .get('mobilePhone')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.addNewEmployeeForm.get('phoneNumber').clearValidators();
          } else {
            this.addNewEmployeeForm.get('phoneNumber').setValidators(Validators.required);
          }
          this.addNewEmployeeForm.get('phoneNumber').updateValueAndValidity();
        }),
    );
  }

  budgetValidation() {
    this.subscription.add(
      this.addNewEmployeeForm
        .get('budgetWithoutLimit')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (value) {
            this.addNewEmployeeForm.get('budgetPerOrder').clearValidators();
            this.addNewEmployeeForm.get('yearlyBudget').clearValidators();
          } else {
            this.addNewEmployeeForm.get('budgetPerOrder').setValidators(Validators.required);
            this.addNewEmployeeForm.get('yearlyBudget').setValidators(Validators.required);
          }
          this.addNewEmployeeForm.get('budgetPerOrder').updateValueAndValidity({ emitEvent: false });
          this.addNewEmployeeForm.get('yearlyBudget').updateValueAndValidity({ emitEvent: false });
        }),
    );

    this.subscription.add(
      this.addNewEmployeeForm
        .get('budgetPerOrder')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (this.addNewEmployeeForm.get('budgetWithoutLimit').value === false) {
            if (value) {
              this.addNewEmployeeForm.get('yearlyBudget').clearValidators();
            } else {
              this.addNewEmployeeForm.get('yearlyBudget').setValidators(Validators.required);
            }
            this.addNewEmployeeForm.get('yearlyBudget').updateValueAndValidity();
          }
        }),
    );

    this.subscription.add(
      this.addNewEmployeeForm
        .get('yearlyBudget')
        .valueChanges.pipe(distinctUntilChanged())
        .subscribe((value) => {
          if (this.addNewEmployeeForm.get('budgetWithoutLimit').value === false) {
            if (value) {
              this.addNewEmployeeForm.get('budgetPerOrder').clearValidators();
            } else {
              this.addNewEmployeeForm.get('budgetPerOrder').setValidators(Validators.required);
            }
            this.addNewEmployeeForm.get('budgetPerOrder').updateValueAndValidity();
          }
        }),
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
