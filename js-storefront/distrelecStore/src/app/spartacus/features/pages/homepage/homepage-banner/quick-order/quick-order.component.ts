import { Component } from '@angular/core';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { QuickOrderEntry } from '@features/pages/homepage/homepage-banner/quick-order/quick-order.model';
import { RoutingService, UserIdService } from '@spartacus/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AppendComponentService } from '@services/append-component.service';
import { DistCartService } from '@services/cart.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-quick-order',
  templateUrl: './quick-order.component.html',
  styleUrls: ['./quick-order.component.scss'],
})
export class QuickOrderComponent {
  faPlus = faPlus;
  rowLength = 3;
  userId$: Observable<string> = this.userIdService.getUserId();
  quickOrderData = [];
  triggerSignal_: BehaviorSubject<null> = new BehaviorSubject<null>(null);
  emptyOrderData = false;

  constructor(
    private appendComponentService: AppendComponentService,
    private cartService: DistCartService,
    private routingService: RoutingService,
    private userIdService: UserIdService,
  ) {}

  addRow(): void {
    if (this.rowLength < 10) {
      this.rowLength++;
    }
  }

  addQuickOrderEntry(data: QuickOrderEntry) {
    this.quickOrderData.push({
      itemNumber: '',
      product: {
        code: data.productCode,
      },
      quantity: data.selectedQuantity,
      reference: '',
      userId: '',
    });
  }

  addToCart(userId: string): void {
    this.triggerSignal_.next(null);
    this.quickOrderData.forEach((data: any) => {
      data.userId = userId;
    });

    if (this.quickOrderData.length > 0) {
      this.addBulk();
    } else {
      this.emptyOrderData = true;
    }
  }

  addBulk(): void {
    const subscription: Subscription = this.cartService
      .addBulkProductsToCart(this.quickOrderData, ItemListEntity.QUICK_ORDER)
      .pipe(
        tap((products) => {
          this.routingService.goByUrl('/cart').then(() => {
            if (products.phaseOutProducts.length > 0) {
              this.appendNoLongerStockPopup();
            }
          });
          subscription.unsubscribe();
        }),
      )
      .subscribe();
  }

  appendNoLongerStockPopup(): void {
    this.appendComponentService.appendWarningPopup(null, null, null, 'cart.nonstock.phaseout.product', 'error');
  }

  displayLoginModule(userId: string) {
    if (userId === 'current') {
      // Call the service here to add the product to shopping list
      this.routingService.goByUrl('/shopping');
    } else {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendLoginModal('shoppingList');
    }
  }
}
