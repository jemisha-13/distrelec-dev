import {
  Component,
  ComponentFactoryResolver,
  ComponentRef,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  ViewContainerRef,
} from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { faAngleDown, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CheckoutService } from '@services/checkout.service';
import { LocalStorageService } from '@services/local-storage.service';
import { createFrom, EventService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { catchError, first, take, tap } from 'rxjs/operators';
import { PaymentFormComponent } from '../payment-form/payment-form.component';
import { SiteConfigService } from '@services/siteConfig.service';
import { CustomerType, SiteIdEnum } from '@model/site-settings.model';
import { CartStoreService } from '@state/cartState.service';
import { CheckoutEvent } from '@features/tracking/events/checkout-event';
import { HttpErrorResponse } from '@angular/common/http';
import { CheckoutGA4EventType } from '@features/tracking/model/event-checkout-type';
import { OrderActions, StateWithOrder } from '@spartacus/order/core';
import { Store } from '@ngrx/store';
import { PaymentMethod } from '@model/checkout.model';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-order-summary',
  templateUrl: './order-summary.component.html',
  styleUrls: ['./order-summary.component.scss'],
})
export class OrderSummaryComponent implements OnInit, OnDestroy {
  @Input() paymentForm: UntypedFormGroup;
  @Input() codiceForm?: UntypedFormGroup;
  @Input() cartData: Cart;
  @Input() isDisplayCardForm_: BehaviorSubject<boolean>;
  @Input() isPaymentLoading_: BehaviorSubject<boolean>;
  @Input() isExceededBudget_: BehaviorSubject<boolean>;
  @Input() customerType: CustomerType;
  @Input() newCreditCardSelected: boolean;

  isSummaryLoading_: BehaviorSubject<boolean> = this.checkoutService.isSummaryLoading_;
  isPaymentDetailsLoading_: BehaviorSubject<boolean> = this.checkoutService.isPaymentDetailsLoading_;
  isDisplayPaypalForm: boolean;

  doc: Document | undefined;
  compRef: ComponentRef<PaymentFormComponent>;
  currentSiteId: string = this.siteConfigService.getCurrentSiteId();
  displayTermsAndConditionsAcceptance: boolean =
    this.currentSiteId === SiteIdEnum.FR ||
    this.currentSiteId === SiteIdEnum.BELGIUM ||
    this.currentSiteId === SiteIdEnum.NETHERLANDS;

  summaryTermsTickChecked =
    this.currentSiteId !== SiteIdEnum.FR &&
    this.currentSiteId !== SiteIdEnum.BELGIUM &&
    this.currentSiteId !== SiteIdEnum.NETHERLANDS;
  // Dynamic height which we will receive from iframe itself upon its load
  iframeDynamicHeight = 0;
  hasLoadedTimes = 0;

  faAngleDown: IconDefinition = faAngleDown;

  highlightTermsAndConditions: boolean;
  isFastCheckoutRedirect = false;

  private subscriptions = new Subscription();

  constructor(
    private activatedRoute: ActivatedRoute,
    private cartStoreService: CartStoreService,
    private router: Router,
    private checkoutService: CheckoutService,
    private vcRef: ViewContainerRef,
    private resolver: ComponentFactoryResolver,
    private winRef: WindowRef,
    private localStorage: LocalStorageService,
    private siteConfigService: SiteConfigService,
    private eventService: EventService,
    private store: Store<StateWithOrder>,
  ) {}

  // Listen to the "message" event which will be triggered from iframe
  @HostListener('window:message', ['$event'])
  onMessageChange(event) {
    if (event.data?.dynamicHeight) {
      // Fetch dynamic height value from iframe
      this.iframeDynamicHeight = event.data.dynamicHeight;
    }
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.checkoutService.triggerPaymentIframe_.subscribe((isNewCreditCard) => {
        if (isNewCreditCard) {
          if (this.summaryTermsTickChecked) {
            this.displayCardForm();
            this.checkoutService.triggerPaymentIframe_.next(false);
          } else {
            // T&C not checked, we need to highlight T&C checkbox
            this.highlightTermsAndConditions = true;
          }
        }
        return isNewCreditCard;
      }),
    );

    this.activatedRoute.queryParamMap.pipe(take(1)).subscribe((params) => {
      this.isFastCheckoutRedirect = params.get('fastCheckoutRedirect') === 'true';

      if (this.isFastCheckoutRedirect) {
        this.eventService.dispatch(
          createFrom(CheckoutEvent, {
            isFastCheckout: this.checkoutService.isFastCheckout_.value,
            cart: this.cartData,
            checkoutEventType: CheckoutGA4EventType.BEGIN_CHECKOUT,
          } as CheckoutEvent),
        );
      }
    });
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  isInvoice(paymentForm: UntypedFormGroup): boolean {
    return paymentForm.value.payment.includes('Invoice') && !this.isExceededBudget_.getValue();
  }

  submitOrderApproval(): void {
    this.isSummaryLoading_.next(true);
    this.checkoutService
      .submitForApproval()
      .pipe(
        tap((orderData: Order) => {
          this.store.dispatch(new OrderActions.LoadOrderDetailsSuccess(orderData));
          this.localStorage.setItem('bingConversionTracking', true);
          this.localStorage.setItem('dispatchPurchaseEvent', true);
          this.router
            .navigate(['checkout/orderConfirmation/' + orderData.code])
            .then(() => this.isSummaryLoading_.next(false));
        }),
        catchError(() => of(this.isSummaryLoading_.next(false))),
      )
      .subscribe();
  }

  placeOrderWithInvoice(): void {
    this.isSummaryLoading_.next(true);
    this.checkoutService
      .placeOrder()
      .pipe(
        first(),
        tap((data) => {
          this.localStorage.setItem('bingConversionTracking', true);
          this.localStorage.setItem('dispatchPurchaseEvent', true);
          this.router
            .navigate(['checkout/orderConfirmation/' + data.code])
            .then(() => this.isSummaryLoading_.next(false));
        }),
        catchError((error: HttpErrorResponse) => of(this.handleInvoiceError(error.error?.errors[0]))),
      )
      .subscribe();
    this.dispatchGA4Events(PaymentMethod.INVOICE);
  }

  handleInvoiceError(error) {
    this.isSummaryLoading_.next(false);
    if (error.reason === 'PurchaseBlockedProducts') {
      const productCodes = error.message.replaceAll('-', '').split(',');
      this.localStorage.setItem('purchaseBlockedProducts', productCodes);
      this.localStorage.setItem('error', error.reason);
      this.router.navigate(['checkout/backorderDetails']);
    } else {
      this.localStorage.setItem('error', error.message);
    }
  }

  displayPaypalForm(): void {
    this.isSummaryLoading_.next(true);
    this.clearData();

    this.getHiddenForm();
    this.isPaymentLoading_.next(true);
    this.localStorage.setItem('paypal', true);
    this.dispatchGA4Events(PaymentMethod.PAYPAL);
  }

  displayCardForm(): void {
    this.isSummaryLoading_.next(true);
    this.isDisplayCardForm_.next(true);
    this.clearData();
    this.getHiddenForm();
    this.dispatchGA4Events(PaymentMethod.CREDIT_CARD);
  }

  clearData(): void {
    // clear the data if user has selected previously a different payment method and populated data
    this.hasLoadedTimes = 0;
    this.isPaymentLoading_.next(false);
  }

  getHiddenForm(): void {
    this.checkoutService
      .getHiddenPaymentForm()
      .pipe(
        first(),
        tap((data) => {
          // Create the reference to the payment form component and populate it with data from response
          const compFactory = this.resolver.resolveComponentFactory(PaymentFormComponent);
          this.compRef = this.vcRef.createComponent(compFactory);

          data.parameters.entry.forEach((entry) => {
            if (entry.key === 'Target') {
              this.compRef.instance.data = { target: entry.value, ...data };
            }
          });

          this.isSummaryLoading_.next(false);
          this.localStorage.setItem('bingConversionTracking', true);

          if (this.cartStoreService.isCartUserGuest()) {
            this.localStorage.setItem('guest', true);
          }
        }),
        catchError(() => of(this.isSummaryLoading_.next(false), this.isDisplayCardForm_.next(false))),
      )
      .subscribe();
  }

  redirectToCart(): void {
    this.router.navigate(['/cart']);
  }

  onLoad(iframeID: string): void {
    // if iframe has the same origin, it will return contentDocument
    // Otherwise if iframe has loaded on the evo side, it will return contentWindow and doc will be  undefined
    this.doc = (this.winRef.document.getElementById(iframeID) as HTMLObjectElement).contentDocument;

    this.hasLoadedTimes = ++this.hasLoadedTimes;

    if (this.hasLoadedTimes < 3 && this.doc) {
      this.doc = (this.winRef.document.getElementById(iframeID) as HTMLObjectElement).contentDocument;
      this.createComponent();
    }
  }

  createComponent(): void {
    this.compRef.location.nativeElement;
    this.doc?.body.appendChild(this.compRef.location.nativeElement);

    // only when frame has loaded first time and it contains hidden form
    if (this.hasLoadedTimes === 1) {
      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.text = `
        {
          document.forms["hiddenPaymentForm"].submit();
        }`;
      this.doc?.head.append(script);
    }
  }

  isCodiceFormValid(): boolean {
    const isCodiceValid: boolean =
      !this.codiceForm?.get('codiceDestinario')?.errors && this.codiceForm?.get('codiceDestinario')?.value;
    const isLegalEmailValid: boolean =
      !this.codiceForm?.get('legalEmail')?.errors && this.codiceForm?.get('legalEmail')?.value;
    return this.codiceForm?.get('codiceDestinario') || this.codiceForm?.get('legalEmail')
      ? isCodiceValid || isLegalEmailValid
      : true;
  }

  isCardSelectedAndSavedAndDisplayForm(): boolean {
    return (
      this.isDisplayCardForm_.getValue() &&
      this.paymentForm.value.payment === 'CreditCard' &&
      this.paymentForm.value.paymentCardType !== 'NewCreditCard'
    );
  }

  isPaymentCardAndNoCardSelected(): boolean {
    return this.paymentForm.value.payment === 'CreditCard' && !this.paymentForm.value.paymentCardType;
  }

  isPaymentCardAndCardSelected(): boolean {
    return this.paymentForm.value.payment === 'CreditCard' && this.paymentForm.value.paymentCardType;
  }

  isPaypalAndDisplayForm(): boolean {
    return this.isDisplayPaypalForm && this.paymentForm.value.payment === 'PayPal';
  }

  summaryTermsChanged(checked: boolean): void {
    this.summaryTermsTickChecked = checked;
    if (checked) {
      this.highlightTermsAndConditions = false;
    }
  }

  private dispatchGA4Events(paymentType: PaymentMethod): void {
    this.eventService.dispatch(
      createFrom(CheckoutEvent, {
        isFastCheckout: this.checkoutService.isFastCheckout_.value,
        cart: this.cartData,
        checkoutEventType: CheckoutGA4EventType.ADD_PAYMENT_INFO,
        paymentType,
      }),
    );

    if (this.isFastCheckoutRedirect) {
      this.eventService.dispatch(
        createFrom(CheckoutEvent, {
          isFastCheckout: this.checkoutService.isFastCheckout_.value,
          cart: this.cartData,
          checkoutEventType: CheckoutGA4EventType.ADD_SHIPPING_INFO,
        }),
      );
    }
  }
}
