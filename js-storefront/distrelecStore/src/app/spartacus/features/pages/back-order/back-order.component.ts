import { Component, OnDestroy, OnInit } from '@angular/core';
import { BackorderService } from '@services/backorder.service';
import { CheckoutService } from '@services/checkout.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppendComponentService } from '@services/append-component.service';
import { OrderEntryList } from '@model/order.model';

@Component({
  selector: 'app-back-order',
  templateUrl: './back-order.component.html',
  styleUrls: ['./back-order.component.scss'],
})
export class BackOrderComponent implements OnInit, OnDestroy {
  backOrderEntries$: Observable<OrderEntryList>;

  replacementProducts = [];
  emailSubmitted = false;

  errorMessage_: BehaviorSubject<string> = this.checkoutService.errorMessage_;
  successMessage_: BehaviorSubject<string> = this.checkoutService.successMessage_;

  constructor(
    private appendComponentService: AppendComponentService,
    private backorderService: BackorderService,
    private checkoutService: CheckoutService,
  ) {}

  ngOnInit(): void {
    this.backOrderEntries$ = this.backorderService.getBackorderItems();
  }

  ngOnDestroy(): void {
    this.checkoutService.errorMessage_.next(null);
    this.checkoutService.successMessage_.next(null);
  }

  handleSave(): void {
    if (this.replacementProducts.length > 0) {
      this.backorderService.saveBackorderChangesWithAlternative(this.replacementProducts);
    } else {
      this.backorderService.saveBackorderChangesWithoutAlternative();
    }
  }

  handleEmailSubmitted(): void {
    this.emailSubmitted = true;
  }

  updateReplacementEntries(data: any): void {
    if (this.replacementProducts.some((entry) => entry.productCode === data.productCode)) {
      // or replace the queued product with update quantity
      this.replacementProducts.splice(
        this.replacementProducts.map((product) => product.productCode).indexOf(data.productCode),
        1,
        data,
      );
    } else {
      // push product data into array if not already present
      this.replacementProducts.push(data);
    }
  }
}
