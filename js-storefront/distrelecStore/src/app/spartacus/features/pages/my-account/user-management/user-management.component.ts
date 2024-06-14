import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, NgZone, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { faAngleRight, faCheck, faPlus, faXmark } from '@fortawesome/free-solid-svg-icons';
import { OccEndpointsService, TranslationService, WindowRef } from '@spartacus/core';
import { catchError, first, map, take, tap } from 'rxjs/operators';
import { BehaviorSubject, Observable, Subscription, of } from 'rxjs';
import { Router } from '@angular/router';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-my-account-user-management',
  templateUrl: './user-management.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./user-management.component.scss'],
})
export class UserManagementComponent implements OnInit, OnDestroy {
  crossIcon = faXmark;
  responseType = '';
  responseMessage = '';
  buttonResetText = 'form.reset';
  buttonSearchText = 'form.searchText';
  faAngleRight = faAngleRight;
  faPlus = faPlus;
  checkIcon = faCheck;
  searchName = '';
  searchStatus = '';
  showUserManagementFlag = false;
  employee: any;
  employees_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  enableRegistration$: Observable<boolean> = this.siteService.isRegistrationEnabledForActiveSite();

  sortingList = [
    { name: 'accountorder.userManagement.sort.nameAsc', value: 'name:asc' },
    { name: 'accountorder.userManagement.sort.nameDesc', value: 'name:desc' },
  ];

  optionalSortingList = [
    { name: 'accountorder.userManagement.sort.status', value: 'status:asc' },
    { name: 'accountorder.userManagement.sort.budgetYearAsc', value: 'budgetYear:asc' },
    { name: 'accountorder.userManagement.sort.budgetYearDesc', value: 'budgetYear:desc' },
  ];

  userStatusList = [
    { name: 'form.select_empty', code: '' },
    { name: 'text.account.status.confirmationpending', code: 'confirmation_pending' },
    { name: 'text.account.status.active', code: 'active' },
  ];

  selectedStatusList = '';
  selectedSorting = 'name:asc';

  enableSubUserManagement$: Observable<boolean> = this.siteService.isSubUserManagementEnabled().pipe(
    take(1),
    tap((isSubUserManagementEnabled) => {
      if (isSubUserManagementEnabled) {
        this.sortingList.push(...this.optionalSortingList);
      }
    }),
  );

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private ngZone: NgZone,
    private winRef: WindowRef,
    private router: Router,
    private translation: TranslationService,
    private siteService: DistrelecBasesitesService,
  ) {
    this.responseType = '';
    this.responseMessage = '';
    const navigation = this.router.getCurrentNavigation();
    const state = navigation.extras.state as { data: string };
    if (state) {
      this.addNewEmployeeReturnEvent(state?.data);
      //reset extras
      this.router.getCurrentNavigation().extras = null;
    }
  }

  ngOnInit() {
    this.searchEmployee().subscribe();
  }

  showAddNewEmployeeComponent() {
    if (this.employee) {
      this.router.navigate([`/my-account/company/edit/employee/${this.employee.customerId}`]);
    } else {
      this.router.navigate([`/my-account/company/create/newemployee`]);
    }

    this.showUserManagementFlag = !this.showUserManagementFlag;
    this.scrollToTop();
  }

  editEmployee(emp: any) {
    this.employee = emp;
    this.showAddNewEmployeeComponent();
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

  addNewEmployeeReturnEvent(event: string = null) {
    if (event === 'added') {
      this.responseType = 'success';
      this.translation
        .translate('form.user_added')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
    } else if (event === 'deleted') {
      this.responseType = 'success';
      this.translation
        .translate('form.user_deleted')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
    } else if (event === 'updated') {
      this.responseType = 'success';
      this.translation
        .translate('form.user_updated')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
    } else if (event === 'deactivated') {
      this.responseType = 'success';
      this.translation
        .translate('form.user_deactivated')
        .pipe(first())
        .subscribe((val) => (this.responseMessage = val));
    }
    this.scrollToTop();
    this.showUserManagementFlag = false;
    this.resetForm();
  }

  resetForm() {
    this.searchName = '';
    this.searchStatus = '';
    this.selectedStatusList = '';
    this.searchEmployee().subscribe();

    this.ngZone.run(() => {
      setTimeout(() => {
        this.responseType = '';
        this.responseMessage = '';
      }, 5000);
    });
  }

  //search orders
  searchEmployee(): Observable<any> {
    this.employees_.next(null);
    const searchRequest = {
      name: this.searchName,
      stateCode: this.selectedStatusList,
    };
    return this.http
      .post<any>(this.occEndpoints.buildUrl(`/users/current/user-management?fields=FULL`), searchRequest)
      .pipe(
        take(1),
        tap((data: any) => {
          if (data) {
            this.employees_.next(data);
          }
        }),
        catchError((response: HttpErrorResponse) => of(this.handleSearchError(response))),
      );
  }

  handleSearchError(response: HttpErrorResponse): void {
    this.responseType = 'danger'; //error
    response?.error?.errors.forEach((err) => {
      this.responseMessage = err.message;
    });
    this.scrollToTop();
  }

  onSearchEmployee() {
    this.searchEmployee().subscribe();
  }

  onSortingSelected(event: any) {
    if (event) {
      const sortingOption = event.split(':');
      this.sorting(sortingOption[0], sortingOption[1]);
    }
  }

  sorting(entity = 'name', order = 'asc') {
    if (entity === 'name') {
      switch (order) {
        case 'asc':
          this.subscriptions.add(this.employees_.pipe(map((data) => data.sort(this.sortByNameAsc))).subscribe());
          break;
        case 'desc':
          this.subscriptions.add(this.employees_.pipe(map((data) => data.sort(this.sortByNameDesc))).subscribe());
          break;
      }
    } else if (entity === 'status') {
      this.subscriptions.add(this.employees_.pipe(map((data) => data.sort(this.sortByStatus))).subscribe());
    } else if (entity === 'budgetYear') {
      switch (order) {
        case 'asc':
          this.subscriptions.add(
            this.employees_.pipe(map((data) => data.sort(this.sortByYearlyBudgetAsc))).subscribe(),
          );
          break;
        case 'desc':
          this.subscriptions.add(
            this.employees_.pipe(map((data) => data.sort(this.sortByYearlyBudgetDesc))).subscribe(),
          );
          break;
      }
    }
  }

  sortByNameAsc = (a, b) => {
    const nameA = (a.firstName + ' ' + a.lastName).toLocaleUpperCase();
    const nameB = (b.firstName + ' ' + b.lastName).toLocaleUpperCase();
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  };

  sortByNameDesc = (a, b) => {
    const nameA = (a.firstName + ' ' + a.lastName).toLocaleUpperCase();
    const nameB = (b.firstName + ' ' + b.lastName).toLocaleUpperCase();
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };

  sortByStatus = (a, b) => {
    const nameA = a.doubleOptinActivated;
    const nameB = b.doubleOptinActivated;
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };

  sortByYearlyBudgetAsc = (a, b) => {
    if (a.budget === null) {
      return -1;
    }
    if (b.budget === null) {
      return 1;
    }
    const nameA = a.budget?.budget;
    const nameB = b.budget?.budget;
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  };

  sortByYearlyBudgetDesc = (a, b) => {
    if (a.budget === null) {
      return 1;
    }
    if (b.budget === null) {
      return -1;
    }
    const nameA = a.budget.budget;
    const nameB = b.budget.budget;
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
