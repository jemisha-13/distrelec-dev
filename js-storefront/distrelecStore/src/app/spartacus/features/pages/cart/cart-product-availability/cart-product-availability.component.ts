import { Component, Input, OnInit } from '@angular/core';
import { ProductAvailability, StockLevel, StockLevelPickup } from '@model/product-availability.model';
import { faCheckCircle, faClock } from '@fortawesome/free-solid-svg-icons';
import { DistCartService } from '@services/cart.service';
import { SiteIdEnum } from '@model/site-settings.model';
import { OrderEntry } from '@spartacus/cart/base/root';

@Component({
  selector: 'app-cart-product-availability',
  templateUrl: './cart-product-availability.component.html',
  styleUrls: ['./cart-product-availability.component.scss'],
})
export class CartProductAvailabilityComponent implements OnInit {
  @Input() cartItem: OrderEntry;
  @Input() productAvailability: ProductAvailability;

  activeSiteId: string = this.cartService.activeSiteId;
  isWarehouse7371: boolean;
  isWarehouse7374: boolean;
  isSwitzerland: boolean;
  mview: string;
  statusCode: string;
  productCode: string;
  showComingSoon: boolean;
  showPickup: boolean;
  showPickupValue: number;
  showInStock: boolean;
  showInStockValue: number;
  showAdditional: boolean;
  showAdditionalValue: number;

  faClock = faClock;
  faCheckCircle = faCheckCircle;

  constructor(private cartService: DistCartService) {}

  ngOnInit() {
    this.statusCode = this.cartItem?.product?.salesStatus;
    this.productCode = this.productAvailability?.productCode;
    this.mview = this.productAvailability.stockLevels[0].mview;
    this.isSwitzerland = this.activeSiteId === SiteIdEnum.CH;

    this.productAvailability?.stockLevels.map((stockLevel: StockLevel) => {
      if (stockLevel.warehouseId === '7371') {
        this.isWarehouse7371 = true;
      }

      if (stockLevel.warehouseId === '7374') {
        this.isWarehouse7374 = true;
      }

      if (this.productAvailability?.stockLevelTotal === 0 || this.statusCode === '21') {
        this.showComingSoon = true;
      } else if (stockLevel.available > 0) {
        if (this.isSwitzerland) {
          if (stockLevel.warehouseId === '7371' && this.productAvailability?.stockLevelPickup?.length > 0) {
            this.productAvailability?.stockLevelPickup.map((stockLevelPickup: StockLevelPickup) => {
              if (stockLevelPickup.stockLevel > 0) {
                this.showAdditional = true;
                this.showAdditionalValue = stockLevel.available;
              }
            });
          }
        }

        this.showInStock = true;
        this.showInStockValue = stockLevel.available;
      }

      return stockLevel;
    });

    if (this.isSwitzerland) {
      if (!this.isWarehouse7371 || !this.isWarehouse7374) {
        this.showAdditional = false;
      }

      this.productAvailability?.stockLevelPickup.map((stockLevelPickup: StockLevelPickup) => {
        if (stockLevelPickup.stockLevel > 0) {
          this.showPickup = true;
          this.showPickupValue = stockLevelPickup.stockLevel;
        }
      });
    }
  }
}
