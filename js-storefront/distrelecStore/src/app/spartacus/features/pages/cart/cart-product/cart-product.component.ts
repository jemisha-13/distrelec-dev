import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  NgZone,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { faInfoCircle, faTruck } from '@fortawesome/free-solid-svg-icons';
import { UseWebpImage } from '@helpers/useWebpImage';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, filter, first, switchMap, tap } from 'rxjs/operators';
import { ProductAvailability } from '@model/product-availability.model';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { createFrom, EventService, GlobalMessageService, GlobalMessageType, Translatable } from '@spartacus/core';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { DefaultImageService } from '@services/default-image.service';
import { AbstractControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { ReferenceInputComponent } from '@features/shared-modules/reference-input/reference-input.component';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-cart-product',
  templateUrl: './cart-product.component.html',
  styleUrls: ['./cart-product.component.scss'],
})
export class CartProductComponent implements OnDestroy, OnInit {
  @Input() cartData: Cart;
  @Input() cartItem: OrderEntry;
  @Input() recalculateCartAction$: BehaviorSubject<boolean>;
  @Input() isLoading_?: BehaviorSubject<boolean>;
  @Input() isContinueDisabled_: BehaviorSubject<boolean>;
  @Input() quantityChangeable: boolean;
  @Input() removable: boolean;
  @Input() referenceEditable: boolean;
  @Input() groupedEntry: boolean;
  @Input() isVoucherSuccess: BehaviorSubject<boolean>;
  @Output() focusRefInput = new EventEmitter<ElementRef>();
  @ViewChild(ReferenceInputComponent) prodRefInput: ReferenceInputComponent;

  availability$: Observable<ProductAvailability>;
  entryNumber: number;
  isRecalculateInProgress_: BehaviorSubject<boolean> = this.cartService.isRecalculateInProgress_;
  isThisProductLoading: boolean;
  isCalibrationLoading: boolean;
  isDisplayMOQPopup: boolean;
  isDisplayStepPopup: boolean;
  isProductWaldom: boolean;
  availableQuantity: number;
  pendingQuantity: number;
  energyEfficiencyLabelImageUrl: string;
  isDiscount: boolean;
  isRsProduct: boolean;
  numericStepperID: NumericStepperIds;

  faTruck = faTruck;
  faInfoCircle = faInfoCircle;

  timeout: ReturnType<typeof setTimeout>;
  inputDelay = 1000;

  useWebpImg = UseWebpImage;
  missingImgSrc = this.defaultImage.getDefaultImage();
  cartProductReferenceForm: UntypedFormGroup;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });

  cartReferenceCodeToggleState: { [refCode: string]: boolean } = {};

  invalidRefMessage: Translatable = {
    // using translation from checkout as we are using similar message for order reference validation.
    key: 'checkout.support_failed',
  };
  itemListEntity = ItemListEntity;

  availabilityData: ProductAvailability;

  private subscriptions = new Subscription();

  constructor(
    private cartService: DistCartService,
    private eventService: EventService,
    public productAvailabilityService: ProductAvailabilityService,
    private ngZone: NgZone,
    private defaultImage: DefaultImageService,
    private fb: UntypedFormBuilder,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private globalMessageService: GlobalMessageService,
  ) {}

  ngOnInit(): void {
    this.addToCartForm.get('quantity').setValue(this.cartItem.quantity);
    this.isDisplayMOQPopup = this.cartItem.moqAdjusted;
    this.isDisplayStepPopup = this.cartItem.stepAdjusted;
    this.isRsProduct = this.cartItem.mview === 'RSP';
    this.entryNumber = this.cartItem.entryNumber;
    this.cartProductReferenceForm = this.fb.group({
      cartProductReference: new UntypedFormControl(this.cartItem.customerReference ?? '', { updateOn: 'change' }),
    });
    this.listenAndSaveReference();
    this.availability$ = this.productAvailabilityService.getAvailability(this.cartItem.product.code).pipe(
      tap((availability) => {
        this.pendingQuantity = this.calculateCreditBlockedPendingQuantity(availability, this.cartItem);
      }),
    );
    this.energyEfficiencyLabelImageUrl = this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(
      this.cartItem.product?.energyEfficiencyLabelImage,
    );
    this.isDiscount = this.isPriceWithDiscount();
    this.numericStepperID = this.assignNumericStepperID();

    this.onQuantityChange();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  listenAndSaveReference(): void {
    this.subscriptions.add(
      this.getCartProductReference()
        .valueChanges.pipe(
          debounceTime(1500),
          distinctUntilChanged(),
          switchMap((value) => {
            this.updateCartProductReference(value);
            this.focusRefInput.emit(this.prodRefInput.refInput);
            return of(value);
          }),
          catchError((error) => {
            this.updateCartProductReference('');
            return error;
          }),
        )
        .subscribe(),
    );
  }

  onPasteProductRef(clipedText: string): void {
    this.subscriptions.add(this.cartService.updateEntryReference(this.entryNumber, clipedText).subscribe());
  }

  onQuantityChange(): void {
    this.subscriptions.add(
      this.addToCartForm.get('quantity')?.statusChanges.subscribe(() => {
        this.setTimeoutForQuantityCall(this.addToCartForm.get('quantity').value);
      }),
    );
  }

  removeProduct() {
    this.isLoading_.next(true);
    this.isThisProductLoading = true;

    // If user triggers to remove the last entry, delete the cart completely
    if (this.cartData.entries?.length < 2) {
      this.cartService.emptyCart(this.cartItem).subscribe(() => this.isLoading_.next(false));
      this.removeVoucherMessageIfPresent();
    } else {
      this.cartService
        .removeProductFromCart(this.cartItem.entryNumber, this.entryNumber, this.cartItem)
        .subscribe(() => this.isLoading_.next(false));
    }
  }

  onCalibrationSelect(sourceCode: string, targetCode: string, quantity: string) {
    this.cartService.isCartLoading_.next(true);
    this.isCalibrationLoading = true;
    this.cartService
      .calibrateProduct(sourceCode, targetCode, this.cartItem.entryNumber, quantity)
      .pipe(
        tap(() => {
          this.cartService.isCartLoading_.next(false);
          this.isCalibrationLoading = false;
          this.cartItem.product.calibrated = !this.cartItem.product.calibrated;
        }),
        catchError(() => {
          this.cartService.isCartLoading_.next(false);
          this.isCalibrationLoading = false;
          return of();
        }),
      )
      .subscribe();
  }

  toggleMobileReferenceCode(refCode: number, isVisible: boolean) {
    this.cartReferenceCodeToggleState[refCode] = isVisible;
  }

  private calculateCreditBlockedPendingQuantity(availability: ProductAvailability, cartEntry: OrderEntry): number {
    const cartQuantity = cartEntry.quantity;
    if (!availability) {
      return cartQuantity;
    }

    return cartQuantity - availability.stockLevelTotal;
  }

  private isPriceWithDiscount(): boolean {
    return (
      this.cartItem.baseListPrice?.value > this.cartItem.basePrice?.value ||
      this.cartItem.totalListPrice?.value > this.cartItem.totalPrice?.value
    );
  }

  private updateCartProductReference(value: string) {
    this.cartService
      .updateEntryReference(this.entryNumber, value)
      .pipe(first())
      .subscribe(
        (response) => response,
        (error) => {
          this.globalMessageService.add(this.invalidRefMessage, GlobalMessageType.MSG_TYPE_ERROR);
        },
      );
  }

  private removeVoucherMessageIfPresent() {
    if (this.isVoucherSuccess.getValue()) {
      this.isVoucherSuccess.next(false);
    }
  }

  private setTimeoutForQuantityCall(value: number) {
    // wait 1 second until user completes changing the quantity and then make the request
    if (value !== this.cartItem.quantity) {
      clearTimeout(this.timeout);
      this.timeout = setTimeout(() => {
        this.updateQuantityIfDifferent(value);
      }, 1000);
    }
  }

  private updateQuantityIfDifferent(qty: number): void {
    if (!this.isQuantitySelectedError()) {
      this.cartService.updateCartQuantity(qty, this.cartItem.entryNumber).subscribe();
    }
  }

  private getCartProductReference(): AbstractControl {
    return this.cartProductReferenceForm.get('cartProductReference');
  }

  private trackProductClick(cartItem, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        entry: cartItem,
        listType: ItemListEntity.CART,
        index,
      } as ProductClickEvent),
    );
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      minusButtonId: 'cartProductDesktopNumericStepperMinusButton_' + this.entryNumber,
      plusButtonId: 'cartProductDesktopNumericStepperPlusButton_' + this.entryNumber,
      inputId: 'cartProductDesktopNumericStepperInput_' + this.entryNumber,
      popupId: 'cartProductDesktopNumericStepperPopup_' + this.entryNumber,
    };
  }

  private isQuantitySelectedError(): boolean {
    if (this.addToCartForm.get('quantity').errors?.length) {
      return this.addToCartForm.get('quantity').errors?.find((err) => err.quantityInputInProgress)
        ?.quantityInputInProgress;
    }
    return false;
  }
}
