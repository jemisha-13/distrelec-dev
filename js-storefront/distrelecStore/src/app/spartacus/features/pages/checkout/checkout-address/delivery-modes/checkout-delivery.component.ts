import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, of } from 'rxjs';
import { SiteContextConfig } from '@spartacus/core';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { faSquare } from '@fortawesome/free-regular-svg-icons';
import { faCircleExclamation, faSquareCheck, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { catchError, first, map, tap } from 'rxjs/operators';
import { CheckoutService } from 'src/app/spartacus/services/checkout.service';
import { DeliveryModeEnum } from '@services/checkout.service';
import { CartStoreService } from '@state/cartState.service';
import { ProductAvailability, StockLevelPickup } from '@model/product-availability.model';
import { isActiveSiteInternational } from '../../../../../site-context/utils';
import { DeliveryModes } from '@model/cart.model';

@Component({
  selector: 'app-checkout-delivery',
  templateUrl: './checkout-delivery.component.html',
  styleUrls: ['./checkout-delivery.component.scss'],
})
export class CheckoutDeliveryComponent implements OnInit {
  @Input() billingForm: UntypedFormGroup;
  @Input() products: OrderEntry[];
  @Input() activeSiteId: string;
  @Input() isBillingFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() userDetails_: BehaviorSubject<any>;
  @Input() isDisplayPickupInfo_: BehaviorSubject<boolean>;
  @Input() dateFormat: string;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;

  cartData_: BehaviorSubject<Cart> = this.cartStoreService.getCartState();

  deliveryModeEnum = DeliveryModeEnum;

  deliveryModes_: BehaviorSubject<DeliveryModes> = this.checkoutService.deliveryModes_;

  isStock0_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(this.cartData_.getValue().completeDelivery);
  isSummaryLoading_: BehaviorSubject<boolean> = this.checkoutService.isSummaryLoading_;
  isDeliveryOptLoading_: BehaviorSubject<boolean> = this.checkoutService.isDeliveryOptLoading_;

  deliveryOptions = [];

  availableForImmediatePickup: boolean;
  pickUpDate: Date;

  isExportShop: boolean;

  isScheduleDisplayed: boolean;

  faCircleExclamation: IconDefinition = faCircleExclamation;
  faSquare: IconDefinition = faSquare;
  faSquareCheck: IconDefinition = faSquareCheck;

  constructor(
    private cdr: ChangeDetectorRef,
    private checkoutService: CheckoutService,
    private availabilityService: ProductAvailabilityService,
    private cartStoreService: CartStoreService,
    private config: SiteContextConfig,
  ) {}

  ngOnInit(): void {
    this.defineAvailableDeliveryModes();
    this.checkIfEntriesHave0Stock();
    this.isExportShop = isActiveSiteInternational(this.config);

    this.isScheduleDisplayed = this.checkoutService.isScheduledDeliveryDisplayed();
  }

  onCombineStockClick(): void {
    this.isDeliveryOptLoading_.next(true);
    this.billingForm.patchValue({
      combineOutOfStock: !this.billingForm.value.combineOutOfStock,
    });

    this.checkoutService.onCompleteDelivery(this.billingForm.value.combineOutOfStock).subscribe((data: Cart) => {
      this.cartStoreService.setCartState(data);
      this.isDeliveryOptLoading_.next(false);
    });
  }

  changeCalendarDate(): void {
    this.isDeliveryOptLoading_.next(true);
    this.billingForm.patchValue({
      selectedDate: '',
    });
  }

  onLaterDeliveryClick(): void {
    this.billingForm.patchValue({
      laterDelivery: !this.billingForm.value.laterDelivery,
    });

    if (!this.billingForm.value.laterDelivery) {
      this.isDeliveryOptLoading_.next(true);
      // If the date has been selected before, then call an endpoint to unselect it
      if (this.billingForm.get('selectedDate').value) {
        this.checkoutService
          .scheduleDelivery()
          .pipe(catchError(() => of(this.isDeliveryOptLoading_.next(false))))
          .subscribe(() => {
            this.isDeliveryOptLoading_.next(false);
            this.billingForm.patchValue({
              selectedDate: '',
            });
          });
      } else {
        this.isDeliveryOptLoading_.next(false);
      }
    } else {
      this.isDeliveryOptLoading_.next(true);
    }
  }

  canHave0StockMsg(cartData: Cart): boolean {
    return (
      cartData.entries.length >= 2 &&
      !cartData.creditBlocked &&
      !this.products.some(
        (entry) =>
          entry.product.calibrated || entry.product.itemCategoryGroup === 'BANS' || this.isWaldomOrRsProduct(entry),
      )
    );
  }

  isWaldomOrRsProduct(entry: OrderEntry): boolean {
    return entry.mview === 'BTR' || entry.mview === 'RSP';
  }

  // Checks to display the yellow banner for 0 stock and later delivery available
  checkIfEntriesHave0Stock(): void {
    if (this.canHave0StockMsg(this.cartData_.getValue())) {
      const isBackOrder: boolean = this.products.some((entry) => entry.isBackOrder);
      if (isBackOrder) {
        // Display banner with option for later delivery for out of stock products
        this.isStock0_.next(true);
      }
    }
  }

  defineAvailableDeliveryModes(): void {
    this.checkoutService
      .getDeliveryOptions()
      .pipe(
        map((deliveryData: DeliveryModes) => {
          this.deliveryModes_.next(deliveryData);
          const cartData: Cart = this.cartData_.getValue();
          if ((cartData.deliveryMode, deliveryData)) {
            this.setScheduledDeliveryIfPresent(cartData);
            this.setDeliveryModeToForm(cartData);
            if (
              cartData.deliveryMode.code === this.deliveryModeEnum.COLLECTION_PICKUP &&
              !this.availabilityService.productsAvailability_.getValue()
            ) {
              this.getProductAvailability(deliveryData.warehouses[0].code);
            }
            this.isDeliveryOptLoading_.next(false);
          }
        }),
      )
      .subscribe();
  }

  setDeliveryModeToForm(cartData: Cart): void {
    this.billingForm.patchValue({
      delivery: {
        code: cartData.deliveryMode.code,
        name: cartData.deliveryMode.name,
      },
    });
  }

  setScheduledDeliveryIfPresent(cartData: Cart): void {
    if (cartData.scheduledDeliveryDate) {
      this.billingForm.patchValue({
        laterDelivery: true,
        selectedDate: cartData.scheduledDeliveryDate,
      });
    }
  }

  getProductAvailability(pickupWarehouseId: string): void {
    const codesArray = [];
    const quantityArray = [];
    this.isDetailsFormSaved_.next({ isSaved: true });
    this.products.forEach((entry: any) => {
      codesArray.push(entry.product.code);
      quantityArray.push(entry.quantity);
    });

    this.availabilityService
      .getAvailabilityWithQuantity(codesArray, quantityArray)
      .pipe(
        first(),
        tap((availabilities: any) => {
          this.getPickupDeliveryDates(availabilities, pickupWarehouseId);
        }),
      )
      .subscribe();
  }

  getPickupDeliveryDates(availabilities: ProductAvailability[], pickupWarehouseId: string): void {
    const availableForImmediatePickup: boolean = availabilities.every((availability) => {
      const pickupData = this.getPickupWarehouseAvailability(availability, pickupWarehouseId);
      return pickupData && pickupData.stockLevel >= availability.requestedQuantity;
    });

    this.availableForImmediatePickup = availableForImmediatePickup;

    // If pickup warehouse contains pickup quantity
    if (availableForImmediatePickup) {
      this.pickUpDate = new Date();
      this.checkIfPickupDateIsWorkingDay();
    } else {
      // check for backorder data
      // check if backorder available products are greater than requested quantity
      const backorderGreater = availabilities.some((availability) => {
        const pickupData = this.getPickupWarehouseAvailability(availability, pickupWarehouseId);
        return pickupData && availability.backorderQuantity > availability.requestedQuantity - pickupData.stockLevel;
      });
      // if backorder is greater than requested quantity
      if (backorderGreater) {
        // if backorder has delivery dates
        if (availabilities.some((availability) => availability.backorderDeliveryDate)) {
          // sort by the greatest backorder delivery date
          // @ts-ignore
          const backorderDeliveryDatesArray: string[] = availabilities
            .map((availability) => availability.backorderDeliveryDate)
            .sort(function (a: string, b: string) {
              // @ts-ignore
              return a - b;
            });
          this.getNextDeliveryDay(backorderDeliveryDatesArray[0]);
        } else {
          this.getNextDeliveryDay();
        }
      } else {
        // if any of the backorder producsts have backorder delivery date
        this.getNextDeliveryDay();
      }
    }
  }

  getPickupWarehouseAvailability(availability: ProductAvailability, pickupWarehouseId: string): StockLevelPickup {
    return availability.stockLevelPickup.find((stock) => stock.warehouseCode === pickupWarehouseId);
  }

  getNextDeliveryDay(date?: string): void {
    if (date) {
      this.pickUpDate = new Date(date);
    } else {
      this.pickUpDate = new Date();
      this.pickUpDate.setDate(this.pickUpDate.getDate() + 10);
    }
    // If next delivery date falls on Saturday or Sunday, then set delivery for the next working day
    this.checkIfPickupDateIsWorkingDay();
  }

  checkIfPickupDateIsWorkingDay(): void {
    if (this.pickUpDate.getDay() === 6) {
      this.pickUpDate.setDate(this.pickUpDate.getDate() + 2);
    } else if (this.pickUpDate.getDay() === 0) {
      this.pickUpDate.setDate(this.pickUpDate.getDate() + 1);
    }
  }

  isLaterDeliveryDisabled(billingForm: UntypedFormGroup): boolean {
    if (this.isExportShop) {
      return false;
    }

    return billingForm.value.delivery.code === this.deliveryModeEnum.COLLECTION_PICKUP;
  }

  deliveryOptLoading(isLoading: boolean): void {
    this.isDeliveryOptLoading_.next(isLoading);
    this.cdr.detectChanges();
  }
}
