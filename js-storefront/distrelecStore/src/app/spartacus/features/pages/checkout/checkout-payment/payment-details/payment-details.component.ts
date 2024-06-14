import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, switchMap, tap } from 'rxjs/operators';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { CartStoreService } from '@state/cartState.service';
import { PaymentDetailsResponse, PaymentModes } from '@model/checkout.model';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrls: ['./payment-details.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PaymentDetailsComponent implements OnInit, OnDestroy {
  @Input() paymentForm: UntypedFormGroup;
  @Input() isDisplayCardForm_: BehaviorSubject<boolean>;
  @Input() activeSiteId: string;
  @Output() isPaymentInfoLoaded = new EventEmitter<boolean>();
  @Output() isOrderRefEdited = new EventEmitter<boolean>();

  cartData: Cart = this.cartStoreService.getCartState().getValue();
  paymentDetails_: BehaviorSubject<PaymentDetailsResponse> = new BehaviorSubject<PaymentDetailsResponse>(null);
  isPaymentInfoLoaded_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isOrderRefPasted: boolean;

  paymentData_: BehaviorSubject<any> = this.checkoutService.paymentData_;
  showOrderReference = false;
  isPaymentDetailsLoading_: BehaviorSubject<boolean> = this.checkoutService.isPaymentDetailsLoading_;

  private subscriptions = new Subscription();

  constructor(
    private checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
  ) {}

  ngOnInit(): void {
    this.showOrderReference = this.cartStoreService.isCartUserB2B();
    this.getPaymentMethods();
    this.listenAndSaveReference();

    this.subscriptions.add(
      this.isPaymentInfoLoaded_.subscribe((isLoaded) => {
        this.isPaymentInfoLoaded.emit(isLoaded);
      }),
    );
  }

  ngOnDestroy(): void {
    this.checkoutService.errorMessage_.next(null);
    this.subscriptions.unsubscribe();
  }

  listenAndSaveReference(): void {
    this.subscriptions.add(
      this.orderReference()
        .valueChanges.pipe(
          debounceTime(1000),
          distinctUntilChanged(),
          tap(() => this.isOrderRefEdited.emit(!this.isOrderRefPasted)),
          switchMap((value) => this.checkoutService.setOrderReference(value)),
          debounceTime(500),
          catchError(() => of(null)),
        )
        .subscribe(() => {
          if (this.isOrderRefPasted) {
            this.isOrderRefPasted = false;
          }
          this.isOrderRefEdited.emit(false);
        }),
    );
  }

  onPasteProductRef(clipedText: string): void {
    this.isOrderRefPasted = true;
    this.isOrderRefEdited.emit(true);
    this.subscriptions.add(
      this.checkoutService.setOrderReference(clipedText).subscribe((response) => this.isOrderRefEdited.emit(false)),
    );
  }

  getPaymentMethods(): void {
    this.checkoutService.getPaymentMethods().subscribe(() => this.isPaymentInfoLoaded_.next(true));
  }

  requestInvoice() {
    this.isPaymentDetailsLoading_.next(true);
    this.checkoutService
      .requestInvoicePayment()
      .pipe(
        tap(() => {
          this.checkoutService.paymentData_.next({
            paymentOptions: this.paymentData_.getValue().paymentOptions,
            canRequestInvoicePaymentMode: true,
            invoicePaymentModeRequested: true,
          });
          this.isPaymentDetailsLoading_.next(false);
        }),
      )
      .subscribe();
  }

  isInvoice(options) {
    return options.code.includes('Invoice');
  }

  isPaypal(options) {
    return options.code === 'PayPal';
  }

  setPaymentMethodName(options) {
    return options.code.replace(/\S*_/, '');
  }

  onPaymentSelection(selectedValue: string) {
    this.isPaymentDetailsLoading_.next(true);

    this.checkoutService
      .setPaymentMode(selectedValue)
      .pipe(
        tap((data) => this.setPaymentOnForm(data)),
        catchError(() => of(this.setPaymentOnForm(selectedValue))),
      )
      .subscribe();
  }

  setPaymentOnForm(data) {
    this.cartStoreService.updateCartState('paymentMode', data);

    if (data.code === PaymentModes.CREDITCARD) {
      this.cartStoreService.updateCartState('paymentInfo', data.paymentDetails ?? null);
    }

    if (this.isDisplayCardForm_.getValue()) {
      this.isDisplayCardForm_.next(false);
    }

    this.isPaymentDetailsLoading_.next(false);
    this.paymentForm.patchValue({
      payment: data.code,
    });
  }

  orderReference(): AbstractControl {
    return this.paymentForm.get('checkoutReference');
  }

  getOrderReferenceLength(): number {
    return !!this.orderReference() && !!this.orderReference().value ? this.orderReference().value.length : 0;
  }
}
