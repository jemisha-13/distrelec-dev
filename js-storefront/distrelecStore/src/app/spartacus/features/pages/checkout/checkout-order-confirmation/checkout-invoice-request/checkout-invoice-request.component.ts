import { Component, Input } from '@angular/core';
import { Order } from '@spartacus/order/root';
import { catchError, take, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { AppendComponentService } from '@services/append-component.service';
import { CheckoutService } from '@services/checkout.service';
import { successTick } from '@assets/icons/icon-index';
import { OrderActions, StateWithOrder } from '@spartacus/order/core';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-checkout-invoice-request',
  templateUrl: './checkout-invoice-request.component.html',
  styleUrls: ['./checkout-invoice-request.component.scss'],
})
export class CheckoutInvoiceRequestComponent {
  @Input() order: Order;

  successTick = successTick;

  constructor(
    private appendComponentService: AppendComponentService,
    private checkoutService: CheckoutService,
    private store: Store<StateWithOrder>,
  ) {}

  handleRequestToPayWithInvoice(): void {
    this.appendComponentService.startScreenLoading();

    this.checkoutService
      .requestInvoicePaymentModeForCurrentUser()
      .pipe(
        take(1),
        tap(() => {
          this.updateOrderStateAndStopLoadingScreen(true);
        }),
        catchError(() => {
          this.updateOrderStateAndStopLoadingScreen(false);
          return of(false);
        }),
      )
      .subscribe();
  }

  updateOrderStateAndStopLoadingScreen(invoicePaymentModeRequested: boolean) {
    this.store.dispatch(new OrderActions.LoadOrderDetailsSuccess({ ...this.order, invoicePaymentModeRequested }));
    this.appendComponentService.stopScreenLoading();
  }
}
