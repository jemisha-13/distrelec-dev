import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { Subscription, fromEvent } from 'rxjs';
import { debounceTime, filter, tap } from 'rxjs/operators';
import { quantitySelectorPlus } from '@assets/icons/icon-index';
import { quantitySelectorMinus } from '@assets/icons/icon-index';

@Component({
  selector: 'app-dist-quantity-selector-legacy',
  templateUrl: './quantity-selector-legacy.component.html',
  styleUrls: ['./quantity-selector-legacy.component.scss'],
  encapsulation: ViewEncapsulation.None,
})

/**
 * This will be deprecated once quick search uses new quantity selector
 * For new development and refactoring please use app-dist-quantity-selector
 * */
export class LegacyQuantitySelectorComponent implements AfterViewInit, OnDestroy {
  @ViewChild('input', { static: true }) input: ElementRef;
  @Input() quantity = 1;
  @Input() minQuantity = 1;
  // maxQuantity will be this check from parent: isWaldom || productStatus.includes('5') && availableQuantity
  @Input() maxQuantity?: number;
  @Input() quantityStep = 1;
  @Input() disabled: boolean;
  @Input() isIncreaseDisabled: boolean;
  @Input() inputDelay = 0;

  @Input() inputId: string;
  @Input() minusButtonId: string;
  @Input() plusButtonId: string;
  @Input() productCode?: string;
  @Input() shouldShowTooltip?: boolean = false;

  @Output() quantityChange = new EventEmitter<number>();

  hasErrors = false;
  minusIcon = quantitySelectorMinus;
  plusIcon = quantitySelectorPlus;

  private subscription = new Subscription();

  constructor() {}

  ngAfterViewInit(): void {
    this.onInput();
  }

  decrease(): void {
    this.hasErrors = false;

    this.ifNoInputAssigned();

    if (this.quantity > this.minQuantity) {
      if (this.quantityStep > 1) {
        this.quantity -= this.minQuantity;
      } else {
        this.quantity -= this.quantityStep;
      }
      this.quantityChange.emit(this.quantity);
    }
  }

  increase(): void {
    this.ifNoInputAssigned();
    // if user deletes the input and clicks increase button
    if (this.quantity < this.minQuantity) {
      // if the user deleted the value from the input field, and sets value that's less than minimum qty, and tries to increase, set the value back to minimum qty
      this.quantity = this.minQuantity;
    } else if (this.maxQuantity && this.quantity > this.maxQuantity) {
      this.quantity = this.maxQuantity;
    } else {
      this.quantity += this.quantityStep;
    }

    this.hasErrors = false;
    this.quantityChange.emit(this.quantity);
  }

  onInput(): void {
    this.subscription.add(
      fromEvent(this.input.nativeElement, 'input')
        .pipe(
          filter(Boolean),
          debounceTime(this.inputDelay),
          tap(() => {
            this.handleLogicWithMinQuantity();
          }),
        )
        .subscribe(),
    );
  }

  handleLogicWithMinQuantity(): void {
    let qty = parseInt(this.input.nativeElement.value, 10);
    if (qty < 1) {
      qty = this.minQuantity ?? 1;
    }
    if (this.maxQuantity && qty > this.maxQuantity) {
      qty = this.maxQuantity;
    }
    const remainder = qty % this.quantityStep;

    if (remainder !== 0) {
      qty += this.quantityStep - remainder;
    }
    this.quantity = qty;
    this.input.nativeElement.value = this.quantity;
    this.hasErrors = qty < this.minQuantity || qty % this.quantityStep !== 0;
    this.quantityChange.emit(this.quantity);
  }

  ifNoInputAssigned(): void {
    if (isNaN(this.quantity)) {
      this.quantity = this.minQuantity;
      this.quantityChange.emit(this.quantity);
    }
    return;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
