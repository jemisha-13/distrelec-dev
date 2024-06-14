import { Injectable } from '@angular/core';
import { groupBy, min } from 'lodash-es';
import { BehaviorSubject } from 'rxjs';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { CartQuotation } from '@model/cart.model';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
})
export class CartComponentService {
  showCartMessage_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  lowStockProductNo_: BehaviorSubject<string> = new BehaviorSubject<string>('');

  constructor(private globalMessageService: GlobalMessageService) {}

  checkIfPhasedOutProduct(cartData: Cart): boolean {
    if (cartData?.phasedOutProducts?.length) {
      this.lowStockProductNo_.next(cartData.phasedOutProducts[0]);
      this.showCartMessage_.next(true);
      return true;
    }
    return false;
  }

  checkIfPunchedOutProduct(cartData: Cart): boolean {
    if (cartData?.punchedOutProducts) {
      this.showPunchOutErrorGlobalMessage(cartData.punchedOutProducts);
      return true;
    }
    return false;
  }

  checkIfEOLProduct(cartData: Cart): boolean {
    if (cartData?.endOfLifeProducts?.length) {
      this.lowStockProductNo_.next(cartData.endOfLifeProducts[0]);
      this.showCartMessage_.next(true);
      return true;
    }
    return false;
  }

  resetErrorMessages(): void {
    this.lowStockProductNo_.next('');
    this.showCartMessage_.next(false);
  }

  sortCartItems(cartData: Cart): (OrderEntry | CartQuotation)[] {
    const entries = this.filterNonQuotationItems(cartData);
    const quotationEntries = this.groupQuotationEntries(cartData);

    // this flag is used to don't do the sorting if user added a product to cart and it needs to be preview on the first place
    const allEntries = [...entries, ...quotationEntries];
    return allEntries.sort((item1, item2) => this.getEntryNumber(item1) - this.getEntryNumber(item2));
  }

  showPunchOutErrorGlobalMessage(products: string, timeout = 7000): void {
    this.globalMessageService.add(
      {
        key: 'cart.punchout_error',
        params: { code: products },
      },
      GlobalMessageType.MSG_TYPE_ERROR,
      timeout,
    );
  }

  private filterNonQuotationItems(cart: Cart): OrderEntry[] {
    return cart.entries?.filter((item) => !item.isQuotation);
  }

  private groupQuotationEntries(cart: Cart): CartQuotation[] {
    const groupedQuotations = groupBy(
      cart.entries?.filter((entry) => entry.isQuotation),
      'quotationId',
    );

    return Object.entries(groupedQuotations).map((array) => {
      const [id, entries] = array;
      return {
        type: 'quotation',
        id,
        entries,
      };
    });
  }

  private getEntryNumber(entry: OrderEntry | CartQuotation) {
    if (entry.type === 'quotation') {
      return min((entry as CartQuotation).entries.map((e) => e.entryNumber));
    } else {
      return (entry as OrderEntry).entryNumber;
    }
  }
}
