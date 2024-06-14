import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { faAngleRight, faCheck, faXmark } from '@fortawesome/free-solid-svg-icons';
import { createFrom, EventService, OccEndpointsService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { DistrelecUserService } from '@services/user.service';
import { Router } from '@angular/router';
import { LocalStorageService } from '@services/local-storage.service';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { OrderStatus } from '@model/order.model';

@Component({
  selector: 'app-my-account-order-approval',
  templateUrl: './order-approval.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./order-approval.component.scss'],
})
export class OrderApprovalComponent implements OnInit, OnDestroy {
  responseType = '';
  responseMessage = '';
  faAngleRight = faAngleRight;
  checkIcon = faCheck;
  sortType = 'DESC';

  sortingList = [
    { name: 'accountorder.approvals.sort.byDateasc', value: 'byDate:asc' },
    { name: 'accountorder.approvals.sort.byDatedesc', value: 'byDate:desc' },
    { name: 'accountorder.approvals.sort.byStatusasc', value: 'byStatus:asc' },
    { name: 'accountorder.approvals.sort.byStatusdesc', value: 'byStatus:desc' },
    { name: 'accountorder.approvals.sort.byTotalPriceasc', value: 'byTotalPrice:asc' },
    { name: 'accountorder.approvals.sort.byTotalPricedesc', value: 'byTotalPrice:desc' },
  ];

  selectedOrderSorting = 'byDate:desc';
  B2BApprovals_: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  faPending = faXmark;
  userDetails$: BehaviorSubject<any>;
  userDetails: any;
  userRoles: any;
  orderStatus = OrderStatus;

  private subscriptions: Subscription = new Subscription();

  constructor(
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private winRef: WindowRef,
    private distrelecUserService: DistrelecUserService,
    private router: Router,
    private localStorage: LocalStorageService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    if (this.localStorage.getItem('approval-rejected')) {
      this.responseType = 'warning';
      this.responseMessage = 'The order has been rejected';
      this.localStorage.removeItem('approval-rejected');
    }

    this.searchApprovals();
  }

  searchApprovals() {
    this.B2BApprovals_.next(null); //reset

    const b2bUrl = '/users/current/order-approval-requests';
    const b2bAdminUrl = '/users/current/order-approval?fields=DEFAULT';

    this.userDetails$ = this.distrelecUserService.userDetails_;

    this.subscriptions.add(
      this.userDetails$.subscribe((data) => {
        if (data) {
          this.userDetails = data;
          this.userRoles = data?.roles;
          if (
            (data?.customerType === 'B2B' || data?.customerType === 'B2B_KEY_ACCOUNT') &&
            !this.findRole('b2badmingroup') &&
            this.findRole('b2bcustomergroup')
          ) {
            this.callData(b2bUrl, 10);
          } else if (
            (data?.customerType === 'B2B' || data?.customerType === 'B2B_KEY_ACCOUNT') &&
            this.findRole('b2badmingroup')
          ) {
            this.callData(b2bAdminUrl, 10);
          }
        }
      }),
    );
  }

  callData(url: string, pageSize: number) {
    const searchRequest = {
      page: 0,
      pageSize,
      show: 'PAGE',
      sort: this.selectedOrderSorting,
      sortType: this.sortType,
    };
    this.http
      .post<any>(this.occEndpoints.buildUrl(url), searchRequest)
      .pipe(take(1))
      .subscribe(
        (data: any) => {
          if (data) {
            this.B2BApprovals_.next(data);
          }
        },
        (response) => {
          this.responseType = 'danger'; //error
          response?.error?.errors.forEach((err) => {
            this.responseMessage = err.message;
          });
        },
        () => {},
      );
  }

  findRole(role: string) {
    return this.userRoles.indexOf(role) > -1;
  }

  onSorting(event: any) {
    if (event) {
      this.selectedOrderSorting = event;
      this.searchApprovals();
    }
  }

  print() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.ORDER_APPROVAL } }));
    this.winRef.nativeWindow.print();
  }

  showApprovalDetails(order: any) {
    if (this.userDetails?.customerType === 'B2B' || this.userDetails?.customerType === 'B2B_KEY_ACCOUNT') {
      if (!this.findRole('b2badmingroup') && this.findRole('b2bcustomergroup')) {
        this.router.navigate([
          `my-account/order-approval-requests/order-details/${order.order.code}/workflow/${order.code}`,
        ]);
      } else if (this.findRole('b2badmingroup')) {
        this.router.navigate([`my-account/order-approval/order-details/${order.order.code}/workflow/${order.code}`]);
      }
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
