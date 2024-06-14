import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { CheckoutService } from '@services/checkout.service';
import { UserIdService } from '@spartacus/core';
import { CartStoreService } from '@state/cartState.service';
import { BehaviorSubject, combineLatest, from, Observable } from 'rxjs';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { CartComponentService } from '../cart-component.service';
import { Cart } from '@spartacus/cart/base/root';
import { map, tap } from 'rxjs/operators';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-cart-total',
  templateUrl: './cart-total.component.html',
  styleUrls: ['./cart-total.component.scss'],
})
export class CartTotalComponent implements OnDestroy, OnInit {
  @Input() cartForm: UntypedFormGroup;
  @Input() recalculateCartAction$: BehaviorSubject<boolean>;
  @Input() isVoucherError_: BehaviorSubject<boolean>;
  @Input() isVoucherSuccess_: BehaviorSubject<boolean>;
  @Input() movLimit: number;
  @Input() extraStickyHeight_: BehaviorSubject<{ height: number }>;
  @Input() isLoading: boolean;
  @Input() isContinueDisabled_: BehaviorSubject<boolean>;
  @Output() triggerRecalculate = new EventEmitter<{ userId: string }>();

  isCartLoading_: BehaviorSubject<boolean> = this.cartService.isCartLoading_;
  isRecalculateInProgress_: BehaviorSubject<boolean> = this.cartService.isRecalculateInProgress_;

  userId$: Observable<string> = this.userIdService.getUserId();
  faSync = faCircleNotch;
  updateCart_: BehaviorSubject<boolean> = this.cartService.updateCart_;
  mobileCartTotalsOpened: boolean;
  cartData$: Observable<Cart> = this.cartService.getCartDataFromStore();
  isProceedToCheckoutDisabled$: Observable<boolean>;
  constructor(
    private appendComponentService: AppendComponentService,
    private cartComponentService: CartComponentService,
    private cartService: DistCartService,
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
    private userIdService: UserIdService,
    private router: Router,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnInit(): void {
    this.isProceedToCheckoutDisabled$ = combineLatest([
      this.isContinueDisabled_,
      this.distBaseSiteService.isAddToCartDisabledForActiveSite(),
    ]).pipe(map(([isContinueDisabled, isAddToCartDisabled]) => isContinueDisabled || isAddToCartDisabled));
  }

  ngOnDestroy(): void {
    this.appendComponentService.stopScreenLoading();
  }

  triggerRecalculateCart(userId: string) {
    this.triggerRecalculate.emit({ userId });
  }

  onCheckout(userId: string, cartData: Cart) {
    // Validate if total is greater than movLimit, otherwise display the pop up
    if (
      cartData.movLimit > cartData.subTotal.value &&
      (userId === 'current' || this.cartStoreService.isCartUserGuest())
    ) {
      this.appendComponentService.appendMOVpopup(
        cartData.movLimit,
        cartData.subTotal.value,
        cartData.subTotal.currencyIso,
      );
      this.appendComponentService.appendBackdropModal({ lightTheme: true });
    } else {
      this.appendComponentService.startScreenLoading();

      // If user is logged in and backorder flag is true, send them to backorder page
      // Otherwise call fast checkout and redirect accordingly
      // If user is guest and backorder is true, send them to backorder page
      // If user is guest and backorder is false, call fast checkout
      if (userId === 'current' || this.cartStoreService.isCartUserGuest()) {
        this.checkoutService
          .prepareForCheckout()
          .pipe(
            tap((cart: Cart) => {
              this.cartStoreService.setCartState(cart);
              this.handleRedirection(cart);
            }),
          )
          .subscribe({
            error: () => this.appendComponentService.stopScreenLoading(),
          });
      } else {
        // If user is not guest, redirect them to checkout login page regardless of backorder
        return from(this.router.navigate(['login/checkout']));
      }
    }
  }

  handleRedirection(cartData: Cart): void {
    // if user has restricted products, they will stay on the cart and the error message will be shown
    if (
      !this.cartComponentService.checkIfPhasedOutProduct(cartData) &&
      !this.cartComponentService.checkIfPunchedOutProduct(cartData) &&
      !this.cartComponentService.checkIfEOLProduct(cartData)
    ) {
      if (cartData.hasUnallowedBackorder) {
        this.router.navigate(['checkout/backorderDetails']);
      } else {
        this.navigateAfterPreparingCheckout(cartData);
      }
    } else {
      this.appendComponentService.stopScreenLoading();
    }
  }

  navigateAfterPreparingCheckout(data: Cart): Promise<boolean> {
    this.cartStoreService.setCartState(data);
    return data.eligibleForFastCheckout
      ? this.router.navigate(['checkout/review-and-pay'], { queryParams: { fastCheckoutRedirect: true } })
      : this.router.navigate(['checkout/delivery']);
  }
}
