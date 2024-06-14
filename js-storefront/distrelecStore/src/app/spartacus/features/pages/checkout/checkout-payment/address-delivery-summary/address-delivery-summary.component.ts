import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faPen, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { CheckoutService, DeliveryModeEnum, WarehouseEnum } from '@services/checkout.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability, StockLevelPickup } from '@model/product-availability.model';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { getDateFormat } from '@helpers/date-helper';
import { Warehouse } from '@model/order.model';
import { Cart, DeliveryMode, OrderEntry } from '@spartacus/cart/base/root';
import { Address } from '@spartacus/core';

@Component({
  selector: 'app-address-delivery-summary',
  templateUrl: './address-delivery-summary.component.html',
})
export class AddressDeliverySummaryComponent implements OnInit, OnDestroy {
  @Input() cartData: Cart;
  @Input() activeSiteId: string;
  @Input() userDetails_: BehaviorSubject<any>;

  billingAddress: Address;
  deliveryAddress: Address;
  deliveryMode: DeliveryMode;
  scheduledDeliveryDate: Date;
  pickupLocation: Warehouse;
  availableForImmediatePickup: boolean;
  pickUpDate: Date;
  dateFormat: string;
  countryCode: string;
  languageCode: string;
  products: OrderEntry[];
  isDeliveryModeExpress: boolean;

  deliveryModeEnum = DeliveryModeEnum;

  faPen: IconDefinition = faPen;

  private subscriptions = new Subscription();

  constructor(
    public checkoutService: CheckoutService,
    private availabilityService: ProductAvailabilityService,
    private router: Router,
    private allSiteSettingsService: AllsitesettingsService,
  ) {}

  ngOnInit(): void {
    this.billingAddress = this.cartData.billingAddress;
    this.deliveryAddress = this.cartData.deliveryAddress;
    this.deliveryMode = this.cartData.deliveryMode;
    this.scheduledDeliveryDate = this.cartData.scheduledDeliveryDate;
    this.pickupLocation = this.cartData.pickupLocation;
    this.products = this.cartData.entries;
    this.isDeliveryModeExpress = this.deliveryMode?.code === this.deliveryModeEnum.EXPRESS;

    this.subscriptions.add(
      this.allSiteSettingsService.getCurrentChannelData().subscribe((data) => {
        this.countryCode = data.country;
        this.languageCode = data.language;
        this.dateFormat = getDateFormat(this.countryCode, this.languageCode);
      }),
    );

    if (this.isPickUpAndCH()) {
      this.getProductAvailability();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  getProductAvailability(): void {
    const codesArray = [];
    const quantityArray = [];

    this.products.forEach((entry: any) => {
      codesArray.push(entry.product.code);
      quantityArray.push(entry.quantity);
    });

    this.availabilityService
      .getAvailabilityWithQuantity(codesArray, quantityArray)
      .subscribe((availabilities: any) => this.getPickupDeliveryDates(availabilities));
  }

  getPickupDeliveryDates(availabilities: ProductAvailability[]): void {
    const availableForImmediatePickup: boolean = availabilities.every((availability) => {
      const pickupData = this.getPickupWarehouseAvailability(availability);
      return pickupData && pickupData.stockLevel >= availability.requestedQuantity;
    });

    this.availableForImmediatePickup = availableForImmediatePickup;

    // If pickup warehouse contains pickup quantity
    if (availableForImmediatePickup) {
      this.pickUpDate = new Date();
      this.checkIfPickupDateIsWorkingDay();
    } // check for backorder data
    else {
      // check if backorder available products are greater than requested quantity
      const backorderGreater = availabilities.some((availability) => {
        const pickupData = this.getPickupWarehouseAvailability(availability);
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
      }
      // if any of the backorder producsts have backorder delivery date
      else {
        this.getNextDeliveryDay();
      }
    }
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

  getPickupWarehouseAvailability(availability: ProductAvailability): StockLevelPickup {
    return availability.stockLevelPickup.find((stock) => stock.warehouseCode === this.pickupLocation.code);
  }

  onAddressesEdit(type?: string): void {
    this.checkoutService.setIsAddressEditClicked(type);
    this.router.navigate(['/checkout/delivery']);
  }

  isPickUp(): boolean {
    return this.deliveryMode?.code === this.deliveryModeEnum.COLLECTION_PICKUP;
  }

  areBillingAndDeliveryAddressTheSame(): boolean {
    return this.billingAddress?.id === this.deliveryAddress?.id;
  }

  isPickUpAndCH(): boolean {
    return this.isPickUp() && this.pickupLocation?.code === WarehouseEnum.NANIKON;
  }
}
