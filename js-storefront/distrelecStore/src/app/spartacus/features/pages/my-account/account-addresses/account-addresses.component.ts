import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { OccEndpointsService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { SiteIdEnum } from '@model/site-settings.model';

@Component({
  selector: 'app-account-addresses',
  templateUrl: './account-addresses.component.html',
  styleUrls: ['./account-addresses.component.scss'],
})
export class AccountAddressesComponent implements OnInit, AfterViewInit, OnDestroy {
  buttonText = 'form.add_address';
  responseType = '';
  responseMessage = '';
  isDisabled = false;
  faPlus = faPlus;
  selectedSortList = 'byCity:asc';
  address: any;
  userDetails$ = this.distrelecUserService.userDetails_;
  userDetails: any;
  showConfirm = false;
  selectedAddressId = '';
  isBillingAddress = false;
  isShippingAddress = false;
  defaultSorting = true;
  isExportShop = false;

  billingAddresses_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  shippingAddresses_: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  sortingList = [
    { name: 'accountorder.addresses.sort.byCityasc', value: 'byCity:asc' },
    { name: 'accountorder.addresses.sort.byCitydesc', value: 'byCity:desc' },
    { name: 'accountorder.addresses.sort.byNameasc', value: 'byName:asc' },
    { name: 'accountorder.addresses.sort.byNamedesc', value: 'byName:desc' },
  ];

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    private windowRef: WindowRef,
    private ngZone: NgZone,
    private distrelecUserService: DistrelecUserService,
    private router: Router,
  ) {
    this.responseType = '';
    this.responseMessage = '';
    const navigation = this.router.getCurrentNavigation();
    const state = navigation.extras.state as { data: string };
    if (state) {
      this.addEditAddressReturnEvent(state?.data);
      //reset extras
      this.router.getCurrentNavigation().extras = null;
    }
  }

  ngOnInit() {
    this.userDetails$.pipe(take(1)).subscribe((data) => {
      this.userDetails = data;
    });

    this.getCustomerAddresses();

    if (this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT') {
      this.sortingList.push(
        { name: 'accountorder.addresses.sort.byCompanyasc', value: 'byCompany:asc' },
        { name: 'accountorder.addresses.sort.byCompanydesc', value: 'byCompany:desc' },
      );
    }

    this.isExportShop = this.userDetails?.customersBaseSite === SiteIdEnum.EX;
  }

  getCustomerAddresses() {
    //billing addresses
    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/addresses?fields=FULL&type=BILLING`))
      .pipe(take(1))
      .subscribe((data: any) => {
        if (data) {
          this.billingAddresses_.next(data.distAddresses);
        }
      });

    //shipping addresses
    this.http
      .get<any>(this.occEndpoints.buildUrl(`/users/current/addresses?fields=FULL&type=SHIPPING`))
      .pipe(take(1))
      .subscribe((data: any) => {
        if (data) {
          const deliveryAddresses = data.distAddresses?.filter((address) => !address.billingAddress);
          this.shippingAddresses_.next(deliveryAddresses);
          if (this.defaultSorting) {
            this.sorting();
          }
        }
      });
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  addEditAddressReturnEvent(event: string = null) {
    if (event === 'added') {
      this.responseType = 'success';
      this.responseMessage = 'form.address_updated';
    } else if (event === 'updated') {
      this.responseType = 'success';
      this.responseMessage = 'form.address_updated';
    } else if (event === 'deleted') {
      this.responseType = 'success';
      this.responseMessage = 'form.address_deleted';
    }
    this.scrollToTop();
    this.reset();
    this.defaultSorting = true;
    this.getCustomerAddresses();
  }

  editAddress(address: any) {
    this.address = address;
    if (this.address) {
      if (this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT') {
        const addressType = this.address.billingAddress ? 'b2bbilling' : 'b2bshipping';
        this.router.navigate([`my-account/edit-address/${addressType}/${this.address.id}`]);
      } else if (this.userDetails?.customerType === 'B2C') {
        this.router.navigate([`my-account/edit-address/b2c/${this.address.id}`]);
      }
    }
    this.scrollToTop();
  }

  addAddress() {
    this.address = '';
    this.router.navigate(['my-account/add-address']);
    this.scrollToTop();
  }

  setDefaultAddress(addressId: string, billingAddress: boolean, shippingAddress: boolean) {
    this.selectedAddressId = addressId;
    this.isBillingAddress = billingAddress;
    this.isShippingAddress = shippingAddress;
    this.showConfirm = true;
  }

  confirmedDefaultAddress(returnedEvent: any) {
    this.responseType = '';
    this.responseMessage = '';

    if (returnedEvent === 'cancelled') {
      this.showConfirm = false;
    } else {
      const request = {
        addressId: this.selectedAddressId,
        shippingAddress: this.isShippingAddress,
        billingAddress: this.isBillingAddress,
      };

      //let's set default address
      this.http
        .post(
          this.occEndpoints.buildUrl(`/users/current/addresses/${this.selectedAddressId}/setDefaultAddress`),
          request,
        )
        .pipe(take(1))
        .subscribe(
          () => {
            //save as default success
            this.responseMessage = 'form.address_set_as_default';
            this.responseType = 'success';
            this.selectedAddressId = ''; //reset
            this.isBillingAddress = false;
            this.isShippingAddress = false;
            this.showConfirm = false;
            this.getCustomerAddresses();
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
            this.selectedAddressId = ''; //reset
            this.isBillingAddress = false;
            this.isShippingAddress = false;
          },
        );

      this.scrollToTop();
      this.reset();
    }
  }

  scrollToTop() {
    if (this.windowRef.isBrowser()) {
      this.windowRef.nativeWindow.scroll({
        top: 0,
        left: 0,
        behavior: 'smooth',
      });
    }
  }

  reset() {
    this.ngZone.run(() => {
      setTimeout(() => {
        this.responseType = '';
        this.responseMessage = '';
      }, 5000);
    });
  }

  onSortingSelected(event: any) {
    if (event) {
      this.defaultSorting = false;
      const sortingOption = event.split(':');
      this.sorting(sortingOption[0], sortingOption[1]);
    }
  }

  sorting(entity: string = 'byCity', order: string = 'asc') {
    if (entity === 'byName') {
      switch (order) {
        case 'asc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByNameAsc))).subscribe(),
          );
          break;
        case 'desc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByNameDesc))).subscribe(),
          );
          break;
      }
    } else if (entity === 'byCity') {
      switch (order) {
        case 'asc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByCityAsc))).subscribe(),
          );
          break;
        case 'desc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByCityDesc))).subscribe(),
          );
          break;
      }
    } else if (entity === 'byCompany') {
      switch (order) {
        case 'asc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByCompanyAsc))).subscribe(),
          );
          break;
        case 'desc':
          this.subscriptions.add(
            this.shippingAddresses_.pipe(map((data) => data.sort(this.sortByCompanyDesc))).subscribe(),
          );
          break;
      }
    }
  }

  sortByNameAsc = (a, b) => {
    const nameA = (a.lastName + ' ' + a.firstName).toLocaleUpperCase();
    const nameB = (b.lastName + ' ' + b.firstName).toLocaleUpperCase();
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  };

  sortByNameDesc = (a, b) => {
    const nameA = (a.lastName + ' ' + a.firstName).toLocaleUpperCase();
    const nameB = (b.lastName + ' ' + b.firstName).toLocaleUpperCase();
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };

  sortByCityAsc = (a, b) => {
    const nameA = a.town.toLocaleUpperCase();
    const nameB = b.town.toLocaleUpperCase();
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  };

  sortByCityDesc = (a, b) => {
    const nameA = a.town.toLocaleUpperCase();
    const nameB = b.town.toLocaleUpperCase();
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };

  sortByCompanyAsc = (a, b) => {
    const nameA = a.companyName.toLocaleUpperCase();
    const nameB = b.companyName.toLocaleUpperCase();
    return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
  };

  sortByCompanyDesc = (a, b) => {
    const nameA = a.companyName.toLocaleUpperCase();
    const nameB = b.companyName.toLocaleUpperCase();
    return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
  };
}
