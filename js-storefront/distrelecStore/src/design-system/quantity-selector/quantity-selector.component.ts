import {
  ChangeDetectorRef,
  Component,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { quantitySelectorPlus } from '@assets/icons/icon-index';
import { quantitySelectorMinus } from '@assets/icons/icon-index';
import { ProductQuantityService } from '@services/product-quantity.service';
import { UntypedFormControl } from '@angular/forms';

@Component({
  selector: 'app-dist-quantity-selector',
  templateUrl: './quantity-selector.component.html',
  styleUrls: ['./quantity-selector.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class QuantitySelectorComponent implements OnDestroy, OnInit, OnChanges {
  @Input() minimumQuantity = 1;
  @Input() maximumQuantity?: number;
  @Input() quantityStep = 1;
  @Input() inputDelay = 0;

  @Input() ids: {
    minusButtonId: string;
    plusButtonId: string;
    inputId: string;
  };

  @Input() productCode?: string;
  @Input() shouldShowTooltip?: boolean = false;

  @Input() salesStatus?: string;
  @Input() isMaximumReachable?: boolean;
  @Input() hasStock?: boolean;

  @Input() isCart: boolean;
  @Input() control?: UntypedFormControl;
  // this input can be passed from the parent to manually disable quantity selector, e.g. if cart recalculation in progress
  @Input() disabled: boolean;

  minusIcon = quantitySelectorMinus;
  plusIcon = quantitySelectorPlus;

  remainderQuantityDisplayed: boolean;
  maxOrderQuantityDisplayed: boolean;

  private timeout: ReturnType<typeof setTimeout>;

  private subscription = new Subscription();

  constructor(
    private productQuantityService: ProductQuantityService,
    private cdRef: ChangeDetectorRef,
  ) {}

  // block user from accidentally updating input when scrolling
  @HostListener('wheel', ['$event'])
  onWheel(event: Event) {
    event.preventDefault();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.disableControlOnChange(changes);
  }

  ngOnInit(): void {
    this.control?.markAsUntouched();
    this.control?.setErrors(null);

    if (this.isSelectorsDisabled || this.disabled) {
      this.control?.disable();
    } else {
      this.control?.enable();
    }

    if (!this.isCart) {
      this.emitQuantityChange(this.control?.value);
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  decrease(): void {
    let quantity = this.control.value;
    quantity -= this.quantityStep;

    this.emitQuantityChange(quantity);
  }

  increase(): void {
    let quantity = this.control.value;
    quantity += this.quantityStep;

    this.emitQuantityChange(quantity);
  }

  onInput($event): void {
    $event.stopPropagation();

    this.handleDelayBeforeEmit(this.control?.value);
  }

  handleDelayBeforeEmit(quantity: number): void {
    clearTimeout(this.timeout);

    this.control.setErrors([{ quantityInputInProgress: true }]);

    // Use setTimeout instead of debounce on valueChanges
    // as it will cause an infinite loop of changes where statusChanges is subscribed to
    this.timeout = setTimeout(() => {
      this.emitQuantityChange(quantity);
    }, this.inputDelay);
  }

  emitQuantityChange(quantity: number): void {
    quantity = this.checkIfQuantityIsRemainder(quantity);

    const { quantitySelected, maxOrderQuantityDisplayed, minOrderQuantityDisplayed } =
      this.productQuantityService.onNumericStepperChange(
        quantity,
        this.maximumQuantity,
        this.minimumQuantity,
        this.isMaximumReachable,
        this.hasStock,
        this.productCode,
        this.isCart,
      );

    this.control.setValue(quantitySelected);

    this.control.setErrors([
      { maxOrderQuantityDisplayed },
      { minOrderQuantityDisplayed },
      { remainderQuantityDisplayed: this.remainderQuantityDisplayed },
      {
        isMaximumQuantityReached: this.productQuantityService.isMaximumQuantityReached(
          this.isMaximumReachable,
          this.productCode,
          this.maximumQuantity,
        ),
      },
      { quantityInputInProgress: false },
    ]);

    this.control.markAsTouched();
    this.maxOrderQuantityDisplayed = maxOrderQuantityDisplayed;
    this.cdRef.markForCheck();
  }

  get isSelectorsDisabled(): boolean {
    return this.productQuantityService.isSelectorsDisabled(this.salesStatus, this.hasStock, this.isMaximumReachable);
  }

  get getMaxOrderQuantityDisplayed(): boolean {
    return this.isMaximumReachable && (this.maxOrderQuantityDisplayed || this.isMaximumQuantityReached);
  }

  private get isMaximumQuantityReached(): boolean {
    return this.productQuantityService.isMaximumQuantityReached(
      this.isMaximumReachable,
      this.productCode,
      this.maximumQuantity,
      this.control?.value,
    );
  }

  private disableControlOnChange(changes: SimpleChanges): void {
    if (changes?.disabled?.currentValue) {
      this.control?.disable();
    }
  }

  private checkIfQuantityIsRemainder(quantity: number): number {
    const remainder = quantity % this.quantityStep;

    if (remainder !== 0) {
      quantity = this.quantityStep;
      this.remainderQuantityDisplayed = true;
    } else {
      this.remainderQuantityDisplayed = false;
    }

    return quantity;
  }
}
