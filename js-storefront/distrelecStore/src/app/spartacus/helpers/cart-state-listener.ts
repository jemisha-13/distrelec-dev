import { Injectable, OnDestroy } from '@angular/core';
import { CartStoreService } from '@state/cartState.service';
import { Subscription } from 'rxjs';
import { CartCalculationErrorHandler } from '@handlers/cart-calculation-error-handler';
import { Cart } from '@spartacus/cart/base/root';

/** This helper can be used to perform actions on cart state update, such as display messages, popups or trigger other updates. */
@Injectable({
  providedIn: 'root',
})
export class CartStateListener implements OnDestroy {
  private subscriptions = new Subscription();

  constructor(
    private cartStoreService: CartStoreService,
    private cartCalculationErrorHandler: CartCalculationErrorHandler,
  ) {}

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  listenToCartChanges(): void {
    this.subscriptions.add(
      this.cartStoreService.getCartState().subscribe((cart: Cart) => {
        this.cartCalculationErrorHandler.checkForCartErrors(cart);
      }),
    );
  }
}
