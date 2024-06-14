import { Injectable, OnDestroy } from '@angular/core';
import { DistCartService } from './cart.service';
import { filter, map, tap } from 'rxjs/operators';
import { Observable, Subscription } from 'rxjs';
import { Cart } from '@spartacus/cart/base/root';
import { ProductAvailability, StockLevel } from '@model/product-availability.model';
import { AbstractControl } from '@angular/forms';
import { AllsitesettingsService } from './allsitesettings.service';
import { CountryCodesEnum } from '@context-services/country.service';
import { WarehouseEnum } from './checkout.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { SalesStatusService } from './sales-status.service';
import {
  SALES_STATUS_PURCHASING_NEWPRODUCT_COMINGSOON_IDENTIFIER,
  SALES_STATUS_PURCHASING_SUSPENDED_IDENTIFIER,
} from '@helpers/constants';

@Injectable({
  providedIn: 'root',
})
export class ProductQuantityService implements OnDestroy {
  cartState: Cart;
  country: CountryCodesEnum;

  private subscriptions = new Subscription();

  constructor(
    private cartService: DistCartService,
    private allSiteSettingsService: AllsitesettingsService,
    private salesStatusService: SalesStatusService,
  ) {
    this.subscriptions.add(this.getCartState().subscribe());
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  maxQuantity(isMaximumReachable: boolean, hasStock: boolean, maximumQuantity: number): number {
    return isMaximumReachable && hasStock ? maximumQuantity : 0;
  }

  isMaxOrderQuantityDisplayed(
    quantity: number,
    maximumQuantity: number,
    isMaximumReachable: boolean,
    hasStock: boolean,
  ): boolean {
    return quantity === maximumQuantity && isMaximumReachable && hasStock;
  }

  isPlusDisabled(isMaximumReachable: boolean, hasStock: boolean, quantity: number, maximumQuantity: number): boolean {
    return isMaximumReachable && hasStock && quantity > maximumQuantity;
  }

  isQuantityValid(quantitySelected: number, minimumQuantity: number, quantityStep: number): boolean {
    return quantitySelected >= minimumQuantity && quantitySelected % quantityStep === 0;
  }

  isMaximumQuantityReached(
    isMaximumReachable: boolean,
    productCode: string,
    maximumQuantity: number,
    quantitySelected?: number,
  ): boolean {
    const productQuantityInCart = this.getProductQuantityFromCart(productCode);
    if (isMaximumReachable) {
      return productQuantityInCart >= maximumQuantity || quantitySelected >= maximumQuantity;
    }
    return false;
  }

  onNumericStepperChange(
    quantitySelected: number,
    maximumQuantity: number,
    minimumQuantity: number,
    isMaximumReachable: boolean,
    hasStock: boolean,
    productCode: string,
    isCart: boolean,
  ) {
    let maxOrderQuantityDisplayed = false;
    let minOrderQuantityDisplayed = false;

    if (!quantitySelected || quantitySelected < minimumQuantity) {
      minOrderQuantityDisplayed = true;
      quantitySelected = minimumQuantity;
    }

    if (maximumQuantity) {
      if (isMaximumReachable && hasStock) {
        const result = this.returnQuantityIfMaximumReachable(
          this.getProductQuantityFromCart(productCode),
          maximumQuantity,
          quantitySelected,
          minimumQuantity,
          maxOrderQuantityDisplayed,
          isCart,
        );

        quantitySelected = result.quantitySelected;
        maxOrderQuantityDisplayed = result.maxOrderQuantityDisplayed;
      }
    }

    return { quantitySelected, maxOrderQuantityDisplayed, minOrderQuantityDisplayed };
  }

  returnQuantityIfMaximumReachable(
    productQuantityInCart: number,
    maximumQuantity: number,
    quantitySelected: number,
    minimumQuantity: number,
    maxOrderQuantityDisplayed: boolean,
    isCart: boolean,
  ): { quantitySelected: number; maxOrderQuantityDisplayed: boolean } {
    let result;

    if (isCart) {
      result = this.returnMaximumQuantityForCart(quantitySelected, maximumQuantity, maxOrderQuantityDisplayed);
    } else {
      result = this.returnMaximumQuantity(
        quantitySelected,
        productQuantityInCart,
        maximumQuantity,
        minimumQuantity,
        maxOrderQuantityDisplayed,
      );
    }

    quantitySelected = result.quantitySelected;
    maxOrderQuantityDisplayed = result.maxOrderQuantityDisplayed;

    return { quantitySelected, maxOrderQuantityDisplayed };
  }

  returnMaximumQuantityForCart(
    quantitySelected: number,
    maximumQuantity: number,
    maxOrderQuantityDisplayed: boolean,
  ): { quantitySelected: number; maxOrderQuantityDisplayed: boolean } {
    if (quantitySelected > maximumQuantity) {
      quantitySelected = maximumQuantity;
      maxOrderQuantityDisplayed = true;
    }

    return { quantitySelected, maxOrderQuantityDisplayed };
  }

  returnMaximumQuantity(
    quantitySelected: number,
    productQuantityInCart: number,
    maximumQuantity: number,
    minimumQuantity: number,
    maxOrderQuantityDisplayed: boolean,
  ): { quantitySelected: number; maxOrderQuantityDisplayed: boolean } {
    if (productQuantityInCart === maximumQuantity) {
      quantitySelected = minimumQuantity;
      maxOrderQuantityDisplayed = true;
    } else if (productQuantityInCart) {
      if (this.isQuantityMoreThanMaximumWithCart(productQuantityInCart, quantitySelected, maximumQuantity)) {
        quantitySelected = maximumQuantity - productQuantityInCart;
        maxOrderQuantityDisplayed = true;
      } else if (quantitySelected < minimumQuantity) {
        quantitySelected = minimumQuantity;
      }
    } else if (this.isQuantityMoreThanMaximum(quantitySelected, maximumQuantity)) {
      quantitySelected = maximumQuantity;
      maxOrderQuantityDisplayed = true;
    }

    return { quantitySelected, maxOrderQuantityDisplayed };
  }

  getCartState(): Observable<Cart> {
    return this.cartService.getCartDataFromStore().pipe(
      filter(Boolean),
      tap((cart: Cart) => (this.cartState = cart)),
    );
  }

  isProductFromWaldom(availability: ProductAvailability): boolean {
    return availability?.stockLevels.filter((item: StockLevel) => item.waldom || item.mview === 'BTR')?.length > 0;
  }

  getProductmaximumQuantity(availability: ProductAvailability): number {
    if (this.isProductFromWaldom(availability)) {
      return availability?.stockLevels.map((item: StockLevel) =>
        item.waldom && item.mview === 'BTR' ? item.available : null,
      )[0];
    } else {
      return availability?.stockLevelTotal;
    }
  }

  isSelectorsDisabled(salesStatus: string, hasStock: boolean, isMaximumReachable: boolean): boolean {
    return (
      this.salesStatusService.alwaysBlockedSalesStatus(salesStatus) ||
      (!hasStock &&
        (isMaximumReachable ||
          this.salesStatusService.endOfStockSalesStatus(salesStatus) ||
          salesStatus?.startsWith(SALES_STATUS_PURCHASING_NEWPRODUCT_COMINGSOON_IDENTIFIER)))
    );
  }

  isMaximumReachable(isBTR: boolean, salesStatus: string): boolean {
    return isBTR || this.salesStatusService.endOfStockSalesStatus(salesStatus);
  }

  assignAvailability(data: ProductAvailability) {
    let hasStock: boolean;

    const isBTR = this.isProductFromWaldom(data);
    if (data?.stockLevels.find((stock) => stock.available > 0)) {
      hasStock = true;
    }

    const maximumQuantity = data?.stockLevels[0].available;

    return { isBTR, hasStock, maximumQuantity };
  }

  getInitialProductQuantity(productCode: string, minimumQuantity: number): Observable<number> {
    return this.getCartState().pipe(map(() => this.getProductQuantityFromCart(productCode) ?? minimumQuantity));
  }

  hasErrors(control: AbstractControl): boolean {
    if (control.errors?.length > 0) {
      return (
        control?.errors?.filter((err) => err.maxOrderQuantityDisplayed)?.maxOrderQuantityDisplayed > 0 ||
        control?.errors?.filter((err) => err.minOrderQuantityDisplayed)?.minOrderQuantityDisplayed > 0
      );
    }
  }

  isQuantityInProgress(control: AbstractControl): boolean {
    if (control.errors?.length > 0) {
      return control.errors?.find((err) => err.quantityInputInProgress)?.quantityInputInProgress;
    }
  }

  isNoStockInSwitzerland(availability: ProductAvailability): boolean {
    return availability.stockLevels.find((warehouse) => warehouse.warehouseId === WarehouseEnum.NANIKON)?.available < 1;
  }

  /** This check is needed as CH webshop has its own warehouse and for status 50, availability is checked from there */
  status50CountryCheck(salesStatus: string, availability: ProductAvailability): Observable<boolean> {
    return this.allSiteSettingsService
      .getCurrentChannelData()
      .pipe(
        map(
          (channelData: CurrentSiteSettings) =>
            channelData.country === CountryCodesEnum.SWITZERLAND &&
            salesStatus?.startsWith(SALES_STATUS_PURCHASING_SUSPENDED_IDENTIFIER) &&
            this.isNoStockInSwitzerland(availability),
        ),
      );
  }

  private isQuantityMoreThanMaximumWithCart(
    productQuantityInCart: number,
    quantitySelected: number,
    maximumQuantity: number,
  ): boolean {
    return productQuantityInCart + quantitySelected > maximumQuantity;
  }

  private isQuantityMoreThanMaximum(quantitySelected: number, maximumQuantity: number): boolean {
    return quantitySelected > maximumQuantity;
  }

  private getProductQuantityFromCart(productCode: string): number {
    return this.cartState?.entries?.find((entry) => entry.product.code === productCode)?.quantity;
  }
}
