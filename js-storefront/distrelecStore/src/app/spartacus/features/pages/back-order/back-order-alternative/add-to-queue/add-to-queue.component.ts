import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { faCheckCircle } from '@fortawesome/free-regular-svg-icons';
import { faCartPlus } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-add-to-queue',
  templateUrl: './add-to-queue.component.html',
  styleUrls: ['./add-to-queue.component.scss'],
})
export class AddToQueueComponent {
  @Input() productQuantity: number;
  @Input() minQuantity: number;
  @Input() productCode: string;
  @Output() backorderAddToCart? = new EventEmitter<number>();
  @Output() recalculatePrice?: EventEmitter<number> = new EventEmitter<number>();

  @ViewChild('qty')
  qty: ElementRef;

  displayPopup: boolean;
  productInQueue_ = new BehaviorSubject<{ isInQueue: boolean }>({ isInQueue: false });

  faCart = faCartPlus;
  faCheckCircle = faCheckCircle;

  constructor() {}

  increaseQuantity(): void {
    this.displayPopup = false;
    this.productQuantity = this.productQuantity + 1;
    this.setPrice();
  }

  decreaseQuantity(): void {
    this.productQuantity = this.productQuantity - 1;
    this.defaultToMinQuantity();
    this.setPrice();
  }

  updateInput(event): void {
    this.displayPopup = false;
    this.productQuantity = +event.target.value;
    this.defaultToMinQuantity();
    this.setPrice();
  }

  defaultToMinQuantity() {
    if (this.productQuantity < this.minQuantity) {
      this.displayPopup = true;
      this.productQuantity = this.minQuantity;
    }
  }

  handleBackorderAddToCart(): void {
    this.productInQueue_.next({ isInQueue: true });

    setTimeout(() => {
      this.productInQueue_.next({ isInQueue: false });
    }, 2000);

    this.backorderAddToCart.emit(this.productQuantity);
  }

  setPrice(): void {
    this.recalculatePrice.next(this.productQuantity);
  }

  numberOnly(event): boolean {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
}
