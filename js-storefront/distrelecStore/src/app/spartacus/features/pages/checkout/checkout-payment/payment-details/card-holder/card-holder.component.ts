import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CartStoreService } from '@state/cartState.service';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, of, Subscription } from 'rxjs';
import { catchError, first, switchMap, tap } from 'rxjs/operators';
import { WindowRef } from '@spartacus/core';
import { DistBreakpointService } from '@services/breakpoint.service';
import { PaymentDetailsResponse, PaymentModes } from '@model/checkout.model';
import { Cart } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-card-holder',
  templateUrl: './card-holder.component.html',
  styleUrls: ['./card-holder.component.scss'],
})
export class CardHolderComponent implements OnInit, OnDestroy {
  @Input() isDisplayCardForm_: BehaviorSubject<boolean>;
  @Input() paymentDetails_: BehaviorSubject<PaymentDetailsResponse>;
  @Input() paymentForm: UntypedFormGroup;

  @Output() newCreditCardSelected = new EventEmitter<boolean>();

  cartData: Cart = this.cartStoreService.getCartState().getValue();
  isPaymentDetailsLoading_: BehaviorSubject<boolean> = this.checkoutService.isPaymentDetailsLoading_;

  showNewCard: boolean;
  showSavedPaymentHeading: boolean;

  private subscriptions = new Subscription();

  constructor(
    private checkoutService: CheckoutService,
    private cartStoreService: CartStoreService,
    private winRef: WindowRef,
    private breakpointService: DistBreakpointService,
  ) {}

  ngOnInit(): void {
    this.preselectCardIfCCMethodSelected();
  }

  cardSelection(selectedValue: string, id?: string) {
    const isNewCreditCard = selectedValue === PaymentModes.NEWCREDITCARD;

    this.paymentFormPatchValueCreditCard(selectedValue, id ?? PaymentModes.NEWCREDITCARD);

    if (this.isDisplayCardForm_.getValue()) {
      this.isDisplayCardForm_.next(false);
    }

    this.checkoutService.isNewCardSelected = isNewCreditCard;
    this.isPaymentDetailsLoading_.next(true);
    this.newCreditCardSelected.emit(isNewCreditCard);
    this.checkoutService
      .setCreditDetails(id ?? PaymentModes.NEWCREDITCARD)
      .pipe(
        first(),
        tap((data) => {
          this.cartStoreService.updateCartState('paymentInfo', data);
          this.isPaymentDetailsLoading_.next(false);
          this.checkoutService.triggerPaymentIframe_.next(isNewCreditCard);
          this.subscriptions.add(
            this.breakpointService.isMobileOrTabletBreakpoint().subscribe((isMobile) => {
              const payWithCardBtn = this.winRef.document.getElementById('payWithCardBtn');
              if (isMobile && payWithCardBtn !== null) {
                payWithCardBtn.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
              }
            }),
          );
        }),
      )
      .subscribe();
  }

  setAsDefault($event: Event, id: string, index: number) {
    $event.stopPropagation();
    this.isPaymentDetailsLoading_.next(true);
    this.checkoutService
      .setAsDefaultCard(id)
      .pipe(
        first(),
        tap(() => {
          this.setSelectedCardDefaultAndDeselectPrevious(id);
          this.isPaymentDetailsLoading_.next(false);
        }),
      )
      .subscribe(() => this.isPaymentDetailsLoading_.next(false));
  }

  setSelectedCardDefaultAndDeselectPrevious(id: string): void {
    const cardData: PaymentDetailsResponse = this.paymentDetails_.value;
    cardData.payments.map((card) => {
      const entry = card;
      if (entry.defaultPayment) {
        entry.defaultPayment = false;
      }
      if (entry.id === id) {
        entry.defaultPayment = true;
      }
      return entry;
    });
    this.paymentDetails_.next(cardData);
  }

  deleteCard($event: Event, id: string) {
    $event.stopPropagation();
    this.isPaymentDetailsLoading_.next(true);
    this.checkoutService
      .deleteSavedCardDetails(id)
      .pipe(
        tap(() => {
          this.setCardOnForm('');
          this.selectCreditCard();
          if (this.isDisplayCardForm_.getValue()) {
            this.isDisplayCardForm_.next(false);
          }
        }),
        switchMap(() => this.checkoutService.getCreditCardDetails()),
        catchError(() => of(this.setCardOnForm(''))),
      )
      .subscribe();
  }

  selectCreditCard() {
    // Lazy load of credit cards
    if (!this.cartStoreService.isCartUserGuest()) {
      this.subscriptions.add(
        this.checkoutService
          .getCreditCardDetails()
          .pipe(
            first(),
            tap((cards) => {
              this.paymentDetails_.next(cards);
              // Show add new card when there is at least 1 card present
              this.showNewCard = cards.payments.length > 0 ? true : false;
              // Need to have seperate variable for heading as the logic defers with add new card button show hide.
              this.showSavedPaymentHeading = cards.payments.length > 0 ? true : false;

              // If CC is saved to cart, pre-select it
              if (this.cartData.paymentInfo && cards.payments.length > 0) {
                cards.payments.forEach((card) => {
                  if (card.id === this.cartData.paymentInfo.id && card.isValid) {
                    this.paymentFormPatchValueCreditCard(card.cardType.name, card.id);
                  }
                });
              } else if (cards.payments.length > 0) {
                const isDefaultIndex = cards.payments.map((card) => card.defaultPayment).indexOf(true);
                const isValidIndex = cards.payments.map((card) => card.isValid).indexOf(true);
                if (isDefaultIndex !== -1 && cards.payments[isDefaultIndex].isValid === true) {
                  this.paymentFormPatchValueCreditCard(
                    cards.payments[isDefaultIndex].cardType.name,
                    cards.payments[isDefaultIndex].id,
                  );
                  this.setCheckoutCredtDetails(cards.payments[isDefaultIndex].id);
                } else if (isValidIndex !== -1) {
                  this.paymentFormPatchValueCreditCard(
                    cards.payments[isValidIndex].cardType.name,
                    cards.payments[isValidIndex].id,
                  );
                  this.setCheckoutCredtDetails(cards.payments[isValidIndex].id);
                } else {
                  this.showNewCard = false;
                  this.setCheckoutCredtDetails(PaymentModes.NEWCREDITCARD);
                  this.paymentFormPatchValueCreditCard(PaymentModes.NEWCREDITCARD, PaymentModes.NEWCREDITCARD);
                }
              } else {
                this.paymentFormPatchValueCreditCard(PaymentModes.NEWCREDITCARD, PaymentModes.NEWCREDITCARD);
              }
            }),
          )
          .subscribe(),
      );
    }
  }

  setCheckoutCredtDetails(id: string) {
    this.checkoutService
      .setCreditDetails(id)
      .pipe(
        first(),
        tap((data) => {
          this.cartStoreService.updateCartState('paymentInfo', data);
          this.isPaymentDetailsLoading_.next(false);
        }),
      )
      .subscribe();
  }

  setCardOnForm(selectedValue) {
    this.paymentForm.patchValue({
      paymentCardType: selectedValue,
    });
    this.isPaymentDetailsLoading_.next(false);
  }

  paymentFormPatchValueCreditCard(paymentCardType: string, selectedCard: string): void {
    this.paymentForm.patchValue({
      paymentCardType,
      selectedCard,
    });
  }

  preselectCardIfCCMethodSelected(): void {
    // When CreditCard has been selected and user is not GUEST, fetch and preselect credit cards
    // For GUEST users, we are preselecting "New Card"
    if (this.paymentForm?.value.payment === PaymentModes.CREDITCARD) {
      if (this.cartStoreService.isCartUserGuest()) {
        this.paymentFormPatchValueCreditCard(PaymentModes.NEWCREDITCARD, PaymentModes.NEWCREDITCARD);
      } else {
        this.selectCreditCard();
      }
    }
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
