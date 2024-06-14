import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { DistCartService } from '@services/cart.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { catchError, distinctUntilKeyChanged, first, take, tap } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { CartComponentService } from './cart-component.service';
import { createFrom, EventService } from '@spartacus/core';
import { CartViewEvent } from '@features/tracking/events/view-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { BulkProductData, CartQuotation } from '@model/cart.model';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { CartProductComponent } from './cart-product/cart-product.component';
import { CartQuotationListComponent } from './cart-quotation-list/cart-quotation-list.component';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChildren(CartQuotationListComponent) quotationEntries: QueryList<CartQuotationListComponent>;
  @ViewChildren(CartProductComponent) productEntries: QueryList<CartProductComponent>;

  focusedElementId: string;
  cartData_: Observable<Cart>;
  currentChannel_: BehaviorSubject<any> = this.siteSettingsService.currentChannelData$;
  isLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  cartForm: UntypedFormGroup;
  recalculateInProgress = false;

  // Boolean to make the call to cartService with the updated quantity for the specific product
  recalculateCartAction$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isVoucherError_ = new BehaviorSubject<boolean>(false);
  isVoucherSuccess_ = new BehaviorSubject<boolean>(false);
  // This behaviour subject is created to update cart stickiness when "Voucher" is expanded
  extraStickyHeight_ = new BehaviorSubject<{ height: number }>({ height: 0 });
  isContinueDisabled_ = new BehaviorSubject<boolean>(false);

  isCartLoading_: BehaviorSubject<boolean> = this.cartService.isCartLoading_;
  allEntries: (OrderEntry | CartQuotation)[];

  showCartMessage_: BehaviorSubject<boolean> = this.cartComponentService.showCartMessage_;
  lowStockProdNo_: BehaviorSubject<string> = this.cartComponentService.lowStockProductNo_;
  areProductsLoading_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  areProductAvailabilitiesLoading_: BehaviorSubject<boolean> = this.cartService.areProductAvailabilitiesLoading_;

  private assignCartSubscription = new Subscription();

  constructor(
    private eventService: EventService,
    private fb: UntypedFormBuilder,
    private cartComponentService: CartComponentService,
    private cartService: DistCartService,
    private siteSettingsService: AllsitesettingsService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.cartForm = this.fb.group({
      orderInput: new UntypedFormControl(''),
      voucherCode: new UntypedFormControl(''),
    });

    this.cartData_ = this.cartService.getCartDataFromStore().pipe(
      distinctUntilKeyChanged('entries'),
      tap((cartData) => {
        if (cartData.entries) {
          this.allEntries = this.cartComponentService.sortCartItems(cartData);
        }
      }),
    );

    this.assignCartSubscription.add(
      this.cartService
        .assignCartEntries()
        .pipe(
          tap((cartData: Cart) => {
            this.isTriggerRecalculate(cartData);
            this.cartComponentService.checkIfPhasedOutProduct(cartData);
            this.cartComponentService.checkIfPunchedOutProduct(cartData);
            this.cartComponentService.checkIfEOLProduct(cartData);
            this.areProductsLoading_.next(false);
            this.eventService.dispatch(
              createFrom(CartViewEvent, {
                cart: cartData,
              } as CartViewEvent),
            );
          }),
        )
        .subscribe(),
    );

    this.addBulkProductsToCartFromQueryParams();
  }

  ngAfterViewInit(): void {
    this.productEntries.changes.subscribe((entries) => {
      if (this.focusedElementId) {
        const prodElement = entries.map((entry: CartProductComponent) => entry.prodRefInput.refInput.nativeElement).find((element) => element.id === this.focusedElementId);
        if (prodElement) {
          prodElement.focus({ preventScroll: true });
        }
      }
    });

    this.quotationEntries.changes.subscribe((entries) => {
      if (this.focusedElementId) {
        const quoteElement = entries.map((entry: CartQuotationListComponent) => entry.quoteRefInput.nativeElement).find((element) => element.id === this.focusedElementId);
        if (quoteElement) {
          quoteElement.focus({ preventScroll: true });
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.cartComponentService.resetErrorMessages();
    this.assignCartSubscription.unsubscribe();
    this.areProductsLoading_.next(true);
    this.areProductAvailabilitiesLoading_.next(true);
  }

  onFocusRefInput(element: ElementRef): void {
    this.focusedElementId = element.nativeElement.id;
  }

  isTriggerRecalculate(cartData: Cart): void {
    if (!cartData?.calculated) {
      this.cartService.updateCart_.next(true);
    } else {
      this.cartService.updateCart_.next(false);
    }
  }

  triggerRecalculate($event): void {
    this.isContinueDisabled_.next(true);
    this.recalculateInProgress = true;
    // no subscription is needed since http request unsubscribes automatically
    this.cartService
      .recalculateCartEntries($event.userId)
      .pipe(
        catchError((error) => {
          this.cartService.updateCart_.next(true);
          this.recalculateInProgress = false;
          this.cartService.isRecalculateInProgress_.next(false);
          throw error;
        }),
        tap((cart: Cart) => {
          this.cartComponentService.resetErrorMessages();
          this.cartComponentService.checkIfPhasedOutProduct(cart);
          this.cartComponentService.checkIfPunchedOutProduct(cart);
          this.cartComponentService.checkIfEOLProduct(cart);
          this.allEntries = this.cartComponentService.sortCartItems(cart);
          this.cartService.isRecalculateInProgress_.next(false);
          this.isContinueDisabled_.next(false);
          this.recalculateInProgress = false;
        }),
      )
      .subscribe();
  }

  onContinueShopping() {
    this.router.navigate(['/']);
  }

  isDangerousProduct(cartData: Cart): boolean {
    return (
      cartData?.entries?.some((entry) => entry.product?.transportGroupData?.dangerous) &&
      this.cartService.isDangerousGoodMessageDisplay()
    );
  }

  removeQuotation(quotationId: string) {
    this.cartService.removeQuotationFromCart(quotationId).pipe(first()).subscribe();
  }

  fillExtAddedData(): BulkProductData[] | undefined {
    const { code, quantity } = this.route.snapshot.queryParams as { code: string; quantity: string };
    return code?.split(',').map((code, index) => ({
      itemNumber: '',
      productCode: code,
      quantity: Number(quantity?.split(',')[index]),
      reference: '',
      userId: '',
    }));
  }

  deleteCartQueryParams(): void {
    this.router.navigate([], {
      queryParams: {
        code: null,
        quantity: null,
      },
      queryParamsHandling: 'merge',
    });
  }

  addBulkProductsToCartFromQueryParams(): void {
    const externalAddedData = this.fillExtAddedData();
    if (externalAddedData) {
      this.cartService
        .addBulkProductsToCart(externalAddedData, ItemListEntity.CART)
        .pipe(
          take(1),
          tap(() => this.deleteCartQueryParams()),
        )
        .subscribe();
    }
  }
}
