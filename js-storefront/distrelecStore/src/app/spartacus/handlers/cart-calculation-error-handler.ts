import { Injectable } from '@angular/core';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { Cart } from '@spartacus/cart/base/root';

/** Handle the display of recalculation error message banner on cart state update */
@Injectable({
  providedIn: 'root',
})
export class CartCalculationErrorHandler {
  constructor(private globalMessageService: GlobalMessageService) {}

  checkForCartErrors(cart: Cart): void {
    if (cart.calculationFailed || cart.customerBlockedInErp) {
      this.displayGlobalError(this.getErrorMessage(cart));
    } else if (this.hasProductCodeMisalignmentAndMoq(cart)) {
      this.displayGlobalError(this.getErrorMessage(cart), { 0: cart.productCodeMisalignment, 1: cart.moq });
    } else if (cart.productCodeMisalignment) {
      this.displayGlobalError(this.getErrorMessage(cart), { code: cart.productCodeMisalignment });
    } else if (cart.punchedOutProducts) {
      this.displayGlobalError(this.getErrorMessage(cart), { code: cart.punchedOutProducts });
    } else if (!!cart.endOfLifeProducts?.length) {
      this.displayGlobalError(this.getErrorMessage(cart), { productCode: cart.endOfLifeProducts });
    } else if (!!cart.phasedOutProducts?.length) {
      this.displayGlobalError(this.getErrorMessage(cart), { productCode: cart.phasedOutProducts });
    } else if (cart.blockedProducts) {
      this.displayGlobalError(this.getErrorMessage(cart), { code: cart.blockedProducts });
    }
  }

  getErrorMessage(cart: Cart): string {
    if (cart.customerBlockedInErp) {
      return 'account.signout.active.error';
    }

    if (this.hasProductCodeMisalignmentAndMoq(cart)) {
      return 'validation.error.min.order.quantity';
    }

    if (cart.productCodeMisalignment || cart.punchedOutProducts || cart.blockedProducts) {
      return 'cart.punchout_error';
    }

    if (!!cart.endOfLifeProducts?.length || !!cart.phasedOutProducts?.length) {
      return 'basket.phaseout.quantity.reducedNumberOfItemsAdded.lowStock';
    }

    return 'cart.erp.error';
  }

  displayGlobalError(message: string, params?: { [key: string]: any }): void {
    const messageParams = params || {};
    this.globalMessageService.add({ key: message, params: messageParams }, GlobalMessageType.MSG_TYPE_ERROR);
  }

  hasProductCodeMisalignmentAndMoq(cart: Cart): boolean {
    return !!cart.productCodeMisalignment && !!cart.moq;
  }
}
