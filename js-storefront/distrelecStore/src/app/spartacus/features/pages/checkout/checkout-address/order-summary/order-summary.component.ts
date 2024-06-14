import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { faAngleDown, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CartStoreService } from '@state/cartState.service';
import { CheckoutService, DeliveryModeEnum } from '@services/checkout.service';
import { BehaviorSubject } from 'rxjs';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-order-summary',
  templateUrl: './order-summary.component.html',
  styleUrls: ['./order-summary.component.scss'],
})
export class OrderSummaryComponent {
  @Input() billingForm: UntypedFormGroup;
  @Input() products: OrderEntry[];
  @Input() isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();
  isSummaryLoading_: BehaviorSubject<boolean> = this.checkoutService.isSummaryLoading_;
  isBillingLoading_: BehaviorSubject<boolean> = this.checkoutService.isBillingLoading_;
  isDeliveryLoading_: BehaviorSubject<boolean> = this.checkoutService.isDeliveryLoading_;

  faAngleDown: IconDefinition = faAngleDown;

  constructor(
    private cartStoreService: CartStoreService,
    private router: Router,
    private checkoutService: CheckoutService,
  ) {}

  redirectToCart(): void {
    this.router.navigate(['/cart']);
  }

  isContinueButtonEnabled(billingForm: UntypedFormGroup): boolean {
    if (this.isPickupAndFormSaved(billingForm.get('delivery').value.code)) {
      return this.isBillingFormSaved_.getValue().isSaved;
    } else if (billingForm.get('isDeliverySame').value || this.cartStoreService.isCartUserGuest()) {
      return this.isBillingSavedDeliverySetAndNotLoading(billingForm);
    } else {
      return this.isBillingAndDeliveryAndDeliveryModeSet(billingForm);
    }
  }

  isPickupAndFormSaved(deliveryCode: DeliveryModeEnum): boolean {
    return (
      deliveryCode === DeliveryModeEnum.COLLECTION_PICKUP &&
      this.isBillingFormSaved_.getValue().isSaved &&
      !this.isLoadingInProgress()
    );
  }

  isBillingAndDeliveryAndDeliveryModeSet(billingForm: UntypedFormGroup): boolean {
    return (
      this.isBillingFormSaved_.value.isSaved &&
      this.isDetailsFormSaved_.value.isSaved &&
      billingForm.get('delivery').value.code &&
      this.setDeliveryAddressId_.value !== 'none' &&
      !this.isLoadingInProgress()
    );
  }

  isBillingSavedDeliverySetAndNotLoading(billingForm: UntypedFormGroup): boolean {
    return (
      this.isBillingFormSaved_.getValue().isSaved &&
      billingForm.get('delivery').value.code &&
      !this.isLoadingInProgress()
    );
  }

  isLoadingInProgress() {
    return this.isBillingLoading_.value || this.isDeliveryLoading_.value || this.isSummaryLoading_.value;
  }

  redirectToPaymentPage(): void {
    this.router.navigate(['/checkout/review-and-pay'], { state: { dispatchCheckoutEvent: true } });
  }
}
