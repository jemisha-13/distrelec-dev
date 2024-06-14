import { Injectable } from '@angular/core';
import { BehaviorSubject, interval, Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CommandService, EventService, OccEndpointsService, RoutingService, UserIdService } from '@spartacus/core';
import { concatMap, defaultIfEmpty, delayWhen, filter, take, tap } from 'rxjs/operators';
import { RetrieveERPCode } from '@model/checkout.model';
import { CheckoutService } from '@services/checkout.service';
import { CustomerType } from '@model/site-settings.model';
import { OrderConnector, OrderService } from '@spartacus/order/core';
import { Order } from '@spartacus/order/root';
import { ActiveCartFacade } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export class DistOrderService extends OrderService {
  userOrderResponse$ = new ReplaySubject<Order>(1);
  delayForERPRequest_ = new BehaviorSubject<number>(1000);
  constructor(
    protected activeCartFacade: ActiveCartFacade,
    protected commandService: CommandService,
    protected orderConnector: OrderConnector,
    protected userIdService: UserIdService,
    protected routingService: RoutingService,
    protected eventService: EventService,
    private http: HttpClient,
    private occEndpoints: OccEndpointsService,
    private checkoutService: CheckoutService,
  ) {
    super(activeCartFacade, userIdService, commandService, orderConnector, eventService);
  }

  getUserOrder(userId: string, orderCode: string, fields: string): Observable<Order> {
    let urlString = `/users/${userId}/orders/${orderCode}`;
    if (fields) {
      urlString += '?fields=' + fields;
    }
    const url = this.occEndpoints.buildUrl(urlString);

    return this.http.get<Order>(url).pipe(tap((data) => this.userOrderResponse$.next(data)));
  }

  getCustomerUid(orderData: Order): string {
    return orderData.customerType === CustomerType.B2E || orderData.customerType === CustomerType.GUEST
      ? orderData.billingAddress.email
      : orderData.b2bCustomerData.displayUid;
  }

  isMissingProfileInformation(orderData: Order): boolean {
    return !(!!orderData.b2bCustomerData.functionCode && !!orderData.b2bCustomerData.contactAddress.departmentCode);
  }

  checkCustomerType(orderData: Order, type: CustomerType): boolean {
    const customerType: string = orderData.b2bCustomerData.customerType;

    switch (type) {
      case 'B2C':
        return customerType === CustomerType.B2C;
      case 'B2B':
        return customerType === CustomerType.B2B;
      case 'B2E':
        return customerType === CustomerType.B2E;
      case 'B2B_KEY_ACCOUNT':
        return customerType === CustomerType.B2B_KEY_ACCOUNT;
      case 'GUEST':
        return orderData.guestCustomer;
      default:
        throw new Error(`Unknown user type: ${type}`);
    }
  }

  getOrderCode(orderData: Order): string {
    return orderData.guestCustomer ? orderData.guid : orderData.code;
  }

  retrieveErpCodeForOrder(orderData: Order): Observable<RetrieveERPCode> {
    const MAX_NUMBER_OF_RETRIES = 7;
    return this.delayForERPRequest_.pipe(
      take(MAX_NUMBER_OF_RETRIES),
      concatMap((waitingTime: number) =>
        this.checkoutService.getERPCode(this.getOrderCode(orderData)).pipe(
          tap((erpData) => {
            if (erpData.status === 'waiting') {
              this.delayForERPRequest_.next(waitingTime * 2);
            }
          }),
          delayWhen((erpData) => interval(this.getIntervalValue(erpData, waitingTime))),
          filter((erp) => erp.status === 'ok'),
          take(1),
        ),
      ),
      defaultIfEmpty(this.getTimeoutResponse()),
    );
  }

  private getIntervalValue(erpData: RetrieveERPCode, waitingTime: number): number {
    return erpData.status === 'ok' ? 0 : waitingTime;
  }

  private getTimeoutResponse(): RetrieveERPCode {
    return { erpCode: '', status: 'waiting', timeout: true };
  }
}
