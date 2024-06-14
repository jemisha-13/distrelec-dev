import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Cart, DeliveryMode, OrderEntry } from '@spartacus/cart/base/root';
import { UntypedFormGroup } from '@angular/forms';
import { catchError, first, map, switchMap, tap } from 'rxjs/operators';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { BaseSiteService } from '@spartacus/core';
import { CheckoutService, DeliveryModeEnum } from '@services/checkout.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability, StockLevelPickup } from '@model/product-availability.model';
import { CartStoreService } from '@state/cartState.service';
import { faCircleInfo, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-delivery-mode',
  templateUrl: './delivery-mode.component.html',
  styleUrls: ['./delivery-mode.component.scss'],
})
export class DeliveryModeComponent implements OnInit {
  @Input() mode: DeliveryMode;
  @Input() billingForm: UntypedFormGroup;
  @Input() activeSiteId: string;
  @Input() setDeliveryAddressId_: BehaviorSubject<string>;
  @Input() isDetailsFormSaved_: BehaviorSubject<{ isSaved: boolean }>;
  @Input() products: OrderEntry[];
  @Input() deliveryModesWarehouses;
  @Input() dateFormat: string;
  @Output() isLoading = new EventEmitter<boolean>();

  isDeliveryLoading_: BehaviorSubject<boolean> = this.checkoutService.isDeliveryLoading_;

  showDeliveryModePrice$ = new Observable<boolean>();

  deliveryModeEnum = DeliveryModeEnum;

  deliveryModeIconClass = 'is-standard';
  deliveryModeIconName = 'Standard.svg';

  isModePickup: boolean;
  isModePickupToPlace: boolean;
  isModePickupEconomy: boolean;
  isModeExpress: boolean;
  isFranceSite: boolean;

  availableForImmediatePickup: boolean;

  pickUpDate: Date;

  faCircleInfo: IconDefinition = faCircleInfo;

  constructor(
    private availabilityService: ProductAvailabilityService,
    private baseSiteService: BaseSiteService,
    private cartStoreService: CartStoreService,
    private checkoutService: CheckoutService,
  ) {}

  ngOnInit(): void {
    this.isModePickup = this.mode?.code === this.deliveryModeEnum.COLLECTION_PICKUP;
    this.isModePickupToPlace = this.mode?.code === this.deliveryModeEnum.PICKUP_TO_PLACE;
    this.isModePickupEconomy = this.mode?.code === this.deliveryModeEnum.ECONOMY_PICKUP;
    this.isFranceSite = this.activeSiteId === 'FR';
    this.isModeExpress =
      this.mode?.code === this.deliveryModeEnum.EXPRESS || this.mode?.code === this.deliveryModeEnum.EXPRESS_ON_DEMAND;

    if (this.isModePickup || this.isModePickupToPlace || this.isModePickupEconomy) {
      this.deliveryModeIconClass = 'is-pickup';
      this.deliveryModeIconName = 'Pickup.svg';
    } else if (this.isModeExpress) {
      this.deliveryModeIconClass = 'is-express';
      this.deliveryModeIconName = 'Express.svg';
    }

    this.assignDeliveryModePrices();
  }

  assignDeliveryModePrices() {
    this.showDeliveryModePrice$ = this.baseSiteService.getActive().pipe(
      switchMap((data) => this.baseSiteService.get(data)),
      map((data: any) => data.checkoutDeliveryMethodPricesShown),
    );
  }

  checkIfModeIsSelected(): boolean {
    return this.mode?.code === this.billingForm.value.delivery.code;
  }

  // As user selects different mode, the cart must be updated to display the correct delivery costs
  onDeliverySelection(selectedValue: DeliveryMode): void {
    const wareHouseId = this.isModePickup ? this.deliveryModesWarehouses[0].code : '';

    this.isLoading.emit(true);

    this.checkoutService
      .setDeliveryMode(selectedValue.code, wareHouseId)
      .pipe(
        first(),
        tap((data: Cart) => {
          if (!data.calculationFailed) {
            this.setDeliveryModeToForm(selectedValue);
            this.updateScheduledDeliveryIfExpress(selectedValue);
            this.assignDeliveryIfPickup(selectedValue, wareHouseId);
            this.setDeliveryAddressResponseRemovedDelivery(data);
          }
          this.cartStoreService.setCartState(data);
          this.cancelLoading();
        }),
        catchError(() => of(this.cancelLoading())),
      )
      .subscribe();
  }

  setDeliveryAddressResponseRemovedDelivery(data: Cart): void {
    if (!data.deliveryAddress) {
      this.setDeliveryAddressId_.next('none');
      this.billingForm.patchValue({
        isDeliverySame: false,
      });
      this.checkoutService.isDeliverySame_.next(false);
      this.isDeliveryLoading_.next(false);
    }
  }

  setDeliveryModeToForm(selectedValue): void {
    this.billingForm.patchValue({
      delivery: {
        code: selectedValue.code,
        name: selectedValue.name,
      },
    });
  }

  updateScheduledDeliveryIfExpress(selectedValue): void {
    if (selectedValue.code === this.deliveryModeEnum.EXPRESS) {
      this.billingForm.patchValue({
        laterDelivery: false,
        selectedDate: '',
      });
    }
  }

  assignDeliveryIfPickup(selectedValue, wareHouseId: string): void {
    if (selectedValue.code === this.deliveryModeEnum.COLLECTION_PICKUP) {
      // do not call availability endpoint if data already exists
      if (this.availabilityService.productsAvailability_.getValue()) {
        this.getPickupDeliveryDates(this.availabilityService.productsAvailability_.getValue(), wareHouseId);
      } else {
        this.getProductAvailability(wareHouseId);
      }
    }
  }

  getPickupDeliveryDates(availabilities: ProductAvailability[], pickupWarehouseId: string): void {
    const availableForImmediatePickup: boolean = availabilities.every((availability) => {
      const pickupData = this.getPickupWarehouseAvailability(availability, pickupWarehouseId);
      return pickupData[0].stockLevel >= availability.requestedQuantity;
    });

    this.availableForImmediatePickup = availableForImmediatePickup;

    // If pickup warehouse contains pickup quantity
    if (availableForImmediatePickup) {
      this.pickUpDate = new Date();
      this.checkIfPickupDateIsWorkingDay();
    } else {
      // check if backorder available products are greater than requested quantity
      const backorderGreater = availabilities.some(
        (availability) =>
          availability.backorderQuantity >
          availability.requestedQuantity -
            this.getPickupWarehouseAvailability(availability, pickupWarehouseId)[0].stockLevel,
      );
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
      }
      // if any of the backorder products have backorder delivery date
      else {
        this.getNextDeliveryDay();
      }
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

  getPickupWarehouseAvailability(availability: ProductAvailability, pickupWarehouseId: string): StockLevelPickup[] {
    return availability.stockLevelPickup.filter((stock) => stock.warehouseCode === pickupWarehouseId);
  }

  cancelLoading(): void {
    this.isLoading.emit(false);
    this.checkoutService.isSummaryLoading_.next(false);
  }

  checkIfPickupDateIsWorkingDay(): void {
    if (this.pickUpDate.getDay() === 6) {
      this.pickUpDate.setDate(this.pickUpDate.getDate() + 2);
    } else if (this.pickUpDate.getDay() === 0) {
      this.pickUpDate.setDate(this.pickUpDate.getDate() + 1);
    }
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
}
