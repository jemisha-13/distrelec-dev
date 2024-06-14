import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { faAngleDown, faAngleUp } from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { DistrelecUserService } from 'src/app/spartacus/services/user.service';
import { SideMenuCountService } from 'src/app/spartacus/services/side-menu-count.service';
import { first, take } from 'rxjs/operators';

@Component({
  selector: 'app-my-account-sidebar-menu',
  templateUrl: './my-account-sidebar-menu.component.html',
  styleUrls: ['./my-account-sidebar-menu.component.scss'],
})
export class MyAccountSidebarMenuComponent implements OnInit, OnDestroy {
  @Input() listCount: string;
  @Output() sendEventToParent = new EventEmitter<any>();

  faAngleDownAccountDetails = faAngleDown;
  faAngleDownPayment = faAngleDown;
  faAngleDownOrderManagement = faAngleDown;
  accountDetailsExpand = false;
  paymentDetailsExpand = false;
  orderDetailsExpand = false;
  accountDetailsExpandClass = false;
  paymentDetailsExpandClass = false;
  orderDetailsExpandClass = false;
  public isCollapsed = false;

  activeBasesiteData$ = this.baseSiteService.getBaseStoreData();
  userDetails$: BehaviorSubject<any>;
  userDetails: any;

  uri = 'my-account-information';
  userRoles: any;

  totalCount$: Subscription;
  count: any = {};

  private subscriptions: Subscription = new Subscription();

  constructor(
    private baseSiteService: DistrelecBasesitesService,
    private distrelecUserService: DistrelecUserService,
    private router: Router,
    private sideMenuCountService: SideMenuCountService,
  ) {
    this.sideMenuCountService.setSideMenuCount();
  }

  ngOnInit() {
    let receivedRoute = this.router.url;
    if (receivedRoute?.includes('?')) {
      receivedRoute = receivedRoute.substring(0, receivedRoute.indexOf('?'));
    }

    this.baseSiteService.getBaseStoreData().pipe(first()).subscribe();
    this.userDetails$ = this.distrelecUserService.userDetails_;

    this.subscriptions.add(
      this.userDetails$.subscribe((data) => {
        this.userDetails = data;
        this.userRoles = data?.roles;

        if (this.userDetails?.customerType === 'B2B' && receivedRoute === '/my-account/my-account-information') {
          this.uri = 'my-account-information';
          this.accountDetailsExpand = true;
          this.faAngleDownAccountDetails = faAngleUp;
          this.accountDetailsExpandClass = true;
          this.paymentDetailsExpandClass = false;
          this.orderDetailsExpandClass = false;
        } else {
          this.enableActiveMenuItem(receivedRoute);
        }
      }),
    );

    this.sideMenuCountService
      .getSideMenuCount()
      .pipe(take(1))
      .subscribe((data) => {
        this.count = data;
      });
  }

  findRole(role: string) {
    return this.userRoles.indexOf(role) > -1;
  }

  goToAccountLink(url: string): void {
    // this.uri = url;
    // this.sendEventToParent.emit(url);
    this.router.navigate([url]);
  }

  enableActiveMenuItem(uri: string) {
    if (uri.indexOf('/my-account/order-history') !== -1) {
      this.uri = 'order-history';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (uri.indexOf('/my-account/invoice-history') !== -1) {
      this.uri = 'invoice-history';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (uri.indexOf('/my-account/quote-history') !== -1) {
      this.uri = 'quote-history';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (uri.indexOf('/my-account/order-approval-requests') !== -1) {
      this.uri = 'order-approval-requests';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (uri.indexOf('/my-account/order-approval') !== -1) {
      this.uri = 'order-approval';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (
      uri.indexOf('/my-account/company/user-management') !== -1 ||
      uri.indexOf('/my-account/company/create/newemployee') !== -1 ||
      uri.indexOf('/my-account/company/edit/employee') !== -1
    ) {
      this.uri = 'company/user-management';
      this.paymentDetailsExpand = false;
      this.faAngleDownOrderManagement = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.orderDetailsExpand = true;
    } else if (uri.indexOf('/my-account/payment-and-delivery-options') !== -1) {
      this.uri = 'payment-and-delivery-options';
      this.paymentDetailsExpand = true;
      this.faAngleDownPayment = faAngleUp;
      this.accountDetailsExpandClass = false;
      this.paymentDetailsExpandClass = true;
      this.orderDetailsExpandClass = false;
    } else if (uri.indexOf('/my-account/company/information') !== -1) {
      this.uri = 'company/information';
      this.accountDetailsExpand = true;
      this.faAngleDownAccountDetails = faAngleUp;
      this.accountDetailsExpandClass = true;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
    } else if (uri.indexOf('/my-account/my-account-information') !== -1) {
      this.uri = 'my-account-information';
      this.accountDetailsExpand = true;
      this.faAngleDownAccountDetails = faAngleUp;
      this.accountDetailsExpandClass = true;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
    } else if (uri.indexOf('/my-account/preference-center') !== -1) {
      this.uri = 'preference-center';
      this.accountDetailsExpand = true;
      this.faAngleDownAccountDetails = faAngleUp;
      this.accountDetailsExpandClass = true;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
    } else if (
      uri.indexOf('/my-account/addresses') !== -1 ||
      uri.indexOf('/my-account/add-address') !== -1 ||
      uri.indexOf('/my-account/edit-address') !== -1
    ) {
      this.uri = 'addresses';
      this.accountDetailsExpand = true;
      this.faAngleDownAccountDetails = faAngleUp;
      this.accountDetailsExpandClass = true;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
    }
  }

  changeIcons(event) {
    if (event.currentTarget?.id === 'accountDetails') {
      this.faAngleDownAccountDetails = this.faAngleDownAccountDetails === faAngleDown ? faAngleUp : faAngleDown;
      this.faAngleDownPayment = faAngleDown;
      this.faAngleDownOrderManagement = faAngleDown;
      this.accountDetailsExpandClass = true;
      this.paymentDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
      this.orderDetailsExpand = false;
      this.paymentDetailsExpand = false;
    } else if (event.currentTarget?.id === 'paymentAndDelivery') {
      this.faAngleDownPayment = this.faAngleDownPayment === faAngleDown ? faAngleUp : faAngleDown;
      this.faAngleDownAccountDetails = faAngleDown;
      this.faAngleDownOrderManagement = faAngleDown;
      this.paymentDetailsExpandClass = true;
      this.accountDetailsExpandClass = false;
      this.orderDetailsExpandClass = false;
      this.accountDetailsExpand = false;
      this.orderDetailsExpand = false;
    } else if (event.currentTarget?.id === 'orderManagement') {
      this.faAngleDownOrderManagement = this.faAngleDownOrderManagement === faAngleDown ? faAngleUp : faAngleDown;
      this.faAngleDownAccountDetails = faAngleDown;
      this.faAngleDownPayment = faAngleDown;
      this.paymentDetailsExpandClass = false;
      this.accountDetailsExpandClass = false;
      this.orderDetailsExpandClass = true;
      this.accountDetailsExpand = false;
      this.paymentDetailsExpand = false;
    } else {
      this.faAngleDownAccountDetails = faAngleDown;
      this.faAngleDownPayment = faAngleDown;
      this.faAngleDownOrderManagement = faAngleDown;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
