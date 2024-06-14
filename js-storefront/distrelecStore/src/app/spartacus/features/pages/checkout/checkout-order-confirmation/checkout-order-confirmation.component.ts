import { AfterViewInit, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { OrderDetailsService } from '@spartacus/order/components';
import { filter, switchMap, take, tap } from 'rxjs/operators';
import { isEmpty } from 'lodash-es';
import { CustomerType } from '@model/site-settings.model';
import { DistOrderService } from '@features/pages/order/core/dist-order.service';
import { Order, OrderFacade } from '@spartacus/order/root';
import { createFrom, EventService, SiteContextConfig } from '@spartacus/core';
import { LocalStorageService } from '@services/local-storage.service';
import { DistCartService } from '@services/cart.service';
import { CheckoutService } from '@services/checkout.service';
import { microsoftUetTagId } from '@services/siteConfig.service';
import { ScriptService } from '@services/script.service';
import { isActiveSiteInternational } from '../../../../site-context/utils';
import { PurchaseEvent } from '@features/tracking/events/purchase-event';
import { EventHelper } from '@features/tracking/event-helper.service';
import { RetrieveERPCode } from '@model/checkout.model';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-checkout-order-confirmation',
  templateUrl: './checkout-order-confirmation.component.html',
  styleUrls: ['./checkout-order-confirmation.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CheckoutOrderConfirmationComponent implements OnInit, AfterViewInit {
  erpCode_: BehaviorSubject<RetrieveERPCode> = new BehaviorSubject<RetrieveERPCode>(null);

  isCustomerB2B: boolean;
  isCustomerB2C: boolean;
  isCustomerGuest: boolean;
  isCustomerB2E: boolean;
  displayProfileInformation: boolean;

  customerUid: string;
  isoCode: string;
  public isNewsletterEnabled$: Observable<boolean> = this.distBaseSiteService.isNewsletterEnabled();
  public order$: Observable<Order> = this.orderService.getOrderDetails().pipe(
    switchMap((orderFromStore: Order) => this.getOrder(orderFromStore)),
    filter((data) => !isEmpty(data)),
    tap((orderData) => {
      this.isCustomerB2B = this.isB2BCustomer(orderData);
      this.isCustomerB2C = this.distOrderService.checkCustomerType(orderData, CustomerType.B2C);
      this.isCustomerGuest = this.distOrderService.checkCustomerType(orderData, CustomerType.GUEST);
      this.isCustomerB2E = this.distOrderService.checkCustomerType(orderData, CustomerType.B2E);
      this.customerUid = this.distOrderService.getCustomerUid(orderData);
      this.isoCode = orderData.billingAddress.country.isocode;
      this.displayProfileInformation =
        this.distOrderService.isMissingProfileInformation(orderData) && !this.isCustomerGuest && !this.isCustomerB2E;
    }),
  );

  constructor(
    private cartService: DistCartService,
    private checkoutService: CheckoutService,
    private config: SiteContextConfig,
    private eventHelper: EventHelper,
    private eventService: EventService,
    private localStorage: LocalStorageService,
    private orderDetailsService: OrderDetailsService,
    private orderService: OrderFacade,
    private scriptService: ScriptService,
    protected distOrderService: DistOrderService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnInit(): void {
    this.handleProgressBarStatus();
    this.handleB2ECustomer();
    this.handleLocalStorageForIframe();
    this.clearCart();

    this.order$.pipe(take(1)).subscribe((order: Order) => {
      this.distOrderService
        .retrieveErpCodeForOrder(order)
        .pipe(take(1))
        .subscribe((erpCode) => this.erpCode_.next(erpCode));

      if (this.localStorage.getItem('bingConversionTracking')) {
        this.executeBingScript();
      }
    });
  }

  ngAfterViewInit() {
    this.order$.pipe(take(1)).subscribe((order: Order) => {
      if (this.localStorage.getItem('bingConversionTracking')) {
        this.localStorage.removeItem('bingConversionTracking');
        this.executeBingConversionTracking(order);
      }

      this.trackOrderConfirmationPage(order);
    });
  }

  private isB2BCustomer(orderData: Order) {
    return (
      this.distOrderService.checkCustomerType(orderData, CustomerType.B2B) ||
      this.distOrderService.checkCustomerType(orderData, CustomerType.B2B_KEY_ACCOUNT)
    );
  }

  private handleB2ECustomer(): void {
    // HDLS-2495: Removing address from local storage for B2E, so when user enters checkout again, form will be empty
    if (this.isCustomerB2E) {
      this.localStorage.removeItem('addressId');
    }
  }

  private handleLocalStorageForIframe(): void {
    this.localStorage.removeItem('orderCode');
  }

  private handleProgressBarStatus(): void {
    this.checkoutService.checkoutPageSteps_.next({
      loginRegisterStep: 'passed',
      billingStep: 'passed',
      confirmStep: 'passed',
    });
  }

  private clearCart(): void {
    this.cartService.revokeCartEntries();
    this.cartService.removeCartToken();
  }

  private getOrder(orderFromStore: Order): Observable<Order> {
    return !isEmpty(orderFromStore) ? of(orderFromStore) : this.orderDetailsService.getOrderDetails();
  }

  private executeBingScript(): void {
    const microsoftTagID: string | boolean = this.getMicrosoftTagID();

    if (microsoftTagID) {
      this.scriptService.appendScript({
        innerHTML: `
          (function (w, d, t, r, u) {
            var f, n, i;
            w[u] = w[u] || [], f = function () {
                var o = {
                    ti: ${microsoftTagID}
                };
                o.q = w[u], w[u] = new UET(o), w[u].push("pageLoad")
            }, n = d.createElement(t), n.src = r, n.async = 1, n.onload = n.onreadystatechange = function () {
                var s = this.readyState;
                s && s !== "loaded" && s !== "complete" || (f(), n.onload = n.onreadystatechange = null)
            }, i = d.getElementsByTagName(t)[0], i.parentNode.insertBefore(n, i)
          })(window, document, "script", "//bat.bing.com/bat.js", "uetq");
        `,
      });
    }
  }

  private getMicrosoftTagID(): string | boolean {
    return (
      microsoftUetTagId.find((item) => item.isoCode === (isActiveSiteInternational(this.config) ? 'EX' : this.isoCode))
        .tagId ?? false
    );
  }

  private trackOrderConfirmationPage(order: Order) {
    this.eventHelper.trackOrderConfirmationPage(order);

    if (this.localStorage.getItem('dispatchPurchaseEvent')) {
      this.localStorage.removeItem('dispatchPurchaseEvent');
      this.eventService.dispatch(
        createFrom(PurchaseEvent, { orderData: order, isFastCheckout: this.checkoutService.isFastCheckout_.value }),
      );
    }
  }

  private executeBingConversionTracking(order: Order) {
    if (this.getMicrosoftTagID()) {
      this.scriptService.appendScript({
        innerHTML:
          `
        window.uetq = [];
        window.uetq.push('event', 'purchase',
          {
            "page_path": "/checkout/orderConfirmation/` +
          order.code +
          `",
            "revenue_value":` +
          order.totalPrice?.value +
          `,
            "currency": "` +
          order.totalPrice?.currencyIso +
          `"
          }
        );`,
      });
    }
  }
}
