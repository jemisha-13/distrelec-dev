import { Component, Input } from '@angular/core';
import { faEnvelope } from '@fortawesome/free-regular-svg-icons';
import { faPrint } from '@fortawesome/free-solid-svg-icons';
import { DistCartService } from 'src/app/spartacus/services/cart.service';
import { AngularCsv } from 'angular-csv-ext/dist/Angular-csv';
import { ExcelService } from 'src/app/spartacus/services/excel.service';
import { UntypedFormGroup } from '@angular/forms';
import { AppendComponentService } from 'src/app/spartacus/services/append-component.service';
import { User, UserIdService } from '@spartacus/core';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { first } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { DistrelecUserService } from '@services/user.service';
import { AvailableStockFileExport, ProductAvailability } from '@model/product-availability.model';

import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { Cart } from '@spartacus/cart/base/root';
import { ShoppingListPayloadItem } from '../../../../model/shopping-list.model';

@Component({
  selector: 'app-cart-toolbar',
  templateUrl: './cart-toolbar.component.html',
  styleUrls: ['./cart-toolbar.component.scss'],
})
export class CartToolbarComponent {
  @Input() cartData: Cart;
  @Input() cartForm: UntypedFormGroup;
  @Input() isVoucherSuccess: BehaviorSubject<boolean>;

  isRecalculateInProgress_: BehaviorSubject<boolean> = this.cartService.isRecalculateInProgress_;

  csvData = [];

  faPrint = faPrint;
  faEnvelope = faEnvelope;
  userId$: Observable<string> = this.userIdService.getUserId();
  userDetails_: BehaviorSubject<User> = this.distrelecUserService.userDetails_;

  constructor(
    private cartService: DistCartService,
    private distrelecUserService: DistrelecUserService,
    private excelService: ExcelService,
    private appendComponentService: AppendComponentService,
    private productAvailabilityService: ProductAvailabilityService,
    private userIdService: UserIdService,
  ) {}

  emptyCart() {
    this.cartService.emptyCart().pipe(first()).subscribe();
    this.removeVoucherMessageIfPresent();
  }

  removeVoucherMessageIfPresent() {
    if (this.isVoucherSuccess.getValue()) {
      this.isVoucherSuccess.next(false);
    }
  }

  pdfGenerationLink() {
    return this.cartService.pdfGenerationLink();
  }

  downloadProducts(type) {
    const options = {
      fieldSeparator: ',',
      quoteStrings: '"',
      decimalseparator: '.',
      showLabels: true,
      showTitle: true,
      userHeader: true,
      headers: [
        'Quantity',
        'Distrelec Article Number',
        'Reference',
        'Manufacturer',
        'Manufacturer Article Number',
        'Name',
        'Availability',
        'Stock',
        'Expired On',
        'My Single Price',
        'My Subtotal',
        'List Single Price',
        'List Subtotal',
      ],
      useHeader: false,
      nullToEmptyString: true,
    };
    // Clear the array from previous assignments if user downloads the same file multiple times
    this.csvData = [];
    this.cartData.entries.forEach((entry, index) => {
      this.assignDataForCsv(entry, index);
    });

    const date = new Date().toISOString().replace(/\D/g, '');
    if (type === 'csv') {
      return new AngularCsv(this.csvData, `cart__${date}`, options);
    } else {
      return this.excelService.exportAsExcelFile(this.csvData, `cart__${date}`);
    }
  }

  addToShoppingList(userId: string) {
    if (userId === 'current') {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendShoppingListModal(this.returnPayloadItems(), ItemListEntity.CART);
    } else {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendLoginModal('shoppingListModal', {
        payloadItems: this.returnPayloadItems(),
        itemListEntity: ItemListEntity.CART,
      });
    }
  }

  assignDataForCsv(entry, index) {
    const availableStock = this.getAvailableStock(index, entry?.quantity);
    const data = {
      Quantity: entry?.quantity,
      'Distrelec Article Number': entry?.product?.codeErpRelevant,
      Reference: entry?.customerReference,
      Manufacturer: entry?.product?.distManufacturer.name,
      'Manufacturer Article Number': entry?.product?.typeName,
      Name: entry?.product?.name,
      Availability: availableStock.deliveryTime,
      Stock: availableStock.quantityAvailable,
      'Expired On': entry?.product?.endOfLifeDate,
      'My Single Price': entry?.basePrice?.currencyIso + ' ' + entry?.basePrice?.value,
      'My Subtotal': entry?.totalPrice?.currencyIso + ' ' + entry?.totalPrice?.value,
      'List Single Price': entry?.baseListPrice?.currencyIso + ' ' + entry?.baseListPrice?.value,
      'List Subtotal': entry?.totalListPrice?.currencyIso + ' ' + entry?.totalListPrice?.value,
    };

    this.csvData.push(data);
  }

  shareViaEmailClick(): void {
    this.appendComponentService.appendFormModal({
      title: { key: 'form.share_cart_email', value: '' },
      senderName: { key: 'form.your_name', value: this.userDetails_.value?.name ?? '' },
      senderEmail: { key: 'form.your_email', value: this.userDetails_.value?.uid ?? '' },
      receiverName: { key: 'form.receiver_name', value: '' },
      receiverEmail: { key: 'form.receiver_email', value: '' },
      message: { key: 'form.message', value: '' },
      icon: 'app/spartacus/assets/media/Email.svg',
    });
  }

  private returnPayloadItems(): ShoppingListPayloadItem[] {
    return this.cartData.entries.map((entry) => ({
      product: { code: entry.product.code },
      desired: entry.quantity,
      comment: entry.customerReference,
    }));
  }

  private getAvailableStock(index: number, quantity: number): AvailableStockFileExport {
    const availability: ProductAvailability = this.productAvailabilityService.productsAvailability_.value[index];
    const availableStockLevel = availability?.stockLevels?.find(
      (stockLevel) => stockLevel.available > 0 && stockLevel.available > quantity,
    );
    if (!!availableStockLevel) {
      return { deliveryTime: availableStockLevel.deliveryTime, quantityAvailable: availableStockLevel.available };
    }
    // csv library won't return 0 as a number
    return { deliveryTime: availability.deliveryTimeBackorder, quantityAvailable: '0' };
  }
}
