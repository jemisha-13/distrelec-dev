import { Component, Input, OnInit } from '@angular/core';
import { RetrieveERPCode } from '@model/checkout.model';
import { AuthService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Router } from '@angular/router';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { DistrelecUserService } from '@services/user.service';
import { of } from 'rxjs';
import { OrderStatus } from '@model/order.model';

@Component({
  selector: 'app-checkout-order-info',
  templateUrl: './checkout-order-info.component.html',
  styleUrls: ['./checkout-order-info.component.scss'],
})
export class CheckoutOrderInfoComponent implements OnInit {
  @Input() order: Order;
  @Input() customerUid: string;
  @Input() erpCode: RetrieveERPCode;

  isOrderApproval: boolean;
  orderContainsRsProducts: boolean;
  isCurrentUserOwnerOfOrder: boolean;
  constructor(
    private router: Router,
    private distrelecUserService: DistrelecUserService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.authService
      .isUserLoggedIn()
      .pipe(
        take(1),
        switchMap((isLoggedIn) => {
          if (isLoggedIn) {
            return this.distrelecUserService.getUserInformation().pipe(
              take(1),
              map((user) => this.order.b2bCustomerData.uid === user.uid),
            );
          }
          return of(false);
        }),
        tap((isSameUser) => (this.isCurrentUserOwnerOfOrder = isSameUser)),
      )
      .subscribe();

    this.isOrderApproval = this.isOrderPendingApproval(this.isCurrentUserOwnerOfOrder);
    this.orderContainsRsProducts = this.doesOrderContainRsProducts();
  }

  redirectToOrderApprovalRequestsList(): void {
    this.router.navigate(['/my-account/order-approval-requests']);
  }

  private isOrderPendingApproval(isSameUser: boolean): boolean {
    // distrelecUserInformation and 2nd part of the condition can be removed after finding a solution for retrieving order from store
    return (
      isSameUser &&
      (this.order.status === OrderStatus.PENDING_APPROVAL ||
        (this.order.exceededBudgetPrice?.value > 0 && this.order.status === OrderStatus.CREATED))
    );
  }

  private doesOrderContainRsProducts(): boolean {
    return this.order.entries.some((entry) => entry.mview === 'RSP');
  }
}
