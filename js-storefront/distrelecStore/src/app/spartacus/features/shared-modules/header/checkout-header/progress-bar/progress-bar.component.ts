import { Component } from '@angular/core';
import { RoutingService, UserIdService } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';

import { ProgressBarInterface } from '@model/checkout.model';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { CartStoreService } from '@state/cartState.service';
import { angleLock } from '@assets/icons/icon-index';

@Component({
  selector: 'app-progress-bar',
  templateUrl: './progress-bar.component.html',
  styleUrls: ['./progress-bar.component.scss'],
})
export class ProgressBarComponent {
  checkoutPageSteps_: BehaviorSubject<ProgressBarInterface> = this.checkoutService.checkoutPageSteps_;
  angleLock = angleLock;
  taskDoneCircleImageSrc = 'app/spartacus/assets/media/checkout/Tick.svg';
  userId$: Observable<string> = this.userIdService.getUserId();

  constructor(
    private checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
    private router: RoutingService,
    private userIdService: UserIdService,
  ) {}

  onWelcomeOrDeliveryClick(userId: string): void {
    if (userId === 'current') {
      this.router.go('checkout/delivery');
    } else {
      if (this.cartStoreService.isCartUserGuest()) {
        this.router.go('checkout/delivery');
      } else {
        this.router.go('login/checkout');
      }
    }
  }

  onPaymentClick(): void {
    this.router.go('checkout/review-and-pay', { state: { dispatchCheckoutEvent: true } });
  }
}
