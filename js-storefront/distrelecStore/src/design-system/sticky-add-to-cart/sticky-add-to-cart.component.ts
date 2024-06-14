import { Component, Input, NgZone, OnDestroy, OnInit } from '@angular/core';
import { filter, finalize, map, take, tap } from 'rxjs/operators';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { Observable, Subscription } from 'rxjs';
import { DistBreakpointService } from '@services/breakpoint.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { Prices, VolumePriceMap } from '@model/price.model';
import { PriceService } from '@services/price.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ActivatedRoute } from '@angular/router';
import { ProductAvailability } from '@model/product-availability.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { DistCartService } from '@services/cart.service';
import { BREAKPOINT } from '@spartacus/storefront';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-dist-sticky-add-to-cart',
  templateUrl: './sticky-add-to-cart.component.html',
  styleUrls: ['./sticky-add-to-cart.component.scss'],
})
export class StickyAddToCartComponent implements OnInit, OnDestroy {
  @Input() productCode: string;
  @Input() title: string;
  @Input() manNumber: string;
  @Input() orderQuantityMinimum: number;
  @Input() quantityStep: number;
  @Input() currentChannelData$: Observable<CurrentSiteSettings>;
  @Input() salesStatus: string;

  isButtonDisabled: boolean;

  pricing$: Observable<Prices>;
  availability$: Observable<ProductAvailability>;

  subscriptions: Subscription = new Subscription();
  volumePricesMap: VolumePriceMap[];
  itemListEntity: ItemListEntity;

  addToCartForm = this.productDataService.addToCartForm;

  numericStepperID: NumericStepperIds;

  inputDelay = 1000;

  constructor(
    private activatedRoute: ActivatedRoute,
    private cartService: DistCartService,
    private productDataService: ProductDataService,
    private distBreakPointService: DistBreakpointService,
    private slideDrawerService: SlideDrawerService,
    private ngZone: NgZone,
    private priceService: PriceService,
    private productAvailabilityService: ProductAvailabilityService,
  ) {}

  ngOnInit(): void {
    this.onQuantityChange();

    this.subscriptions.add(
      this.activatedRoute.queryParamMap.pipe(take(1)).subscribe((params) => {
        this.itemListEntity = params.get('itemList') as ItemListEntity;
      }),
    );

    this.numericStepperID = this.assignNumericStepperID();
    this.availability$ = this.productAvailabilityService.getAvailability(this.productCode);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  addToCartClick(qty: number = this.orderQuantityMinimum): void {
    this.cartService
      .addProductToCart(this.productCode, qty, false, this.itemListEntity)
      .pipe(finalize(() => this.openPanel(event)))
      .subscribe();
  }

  openPanel(event): void {
    this.distBreakPointService
      .isUp(BREAKPOINT.lg)
      .pipe(take(1))
      .subscribe((isTabletPlus) => {
        if (isTabletPlus === true) {
          event.stopPropagation();
          this.slideDrawerService.openPanel(event, 'cart-tray');
          this.ngZone.run(() => {
            setTimeout(() => {
              this.closePanel();
            }, 3000);
          });
        }
      });
  }

  closePanel(): void {
    this.slideDrawerService.closePanel();
  }

  updatePrices(quantity: number): void {
    quantity = this.setMinimumQuantityForPrices(quantity);

    this.pricing$ = this.productDataService.prices$.pipe(
      filter((prices) => prices?.volumePricesMap?.length > 0),
      map((prices: Prices) => ({
        price: prices.price,
        basePrice: this.priceService.getPriceForQuantity(prices.volumePricesMap, quantity, 'B2B'),
        priceWithVat: this.priceService.getPriceForQuantity(prices.volumePricesMap, quantity, 'B2C'),
        ...prices,
      })),
    );
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      minusButtonId: 'sticky-add-to-cart-quantity-selector-minus',
      plusButtonId: 'sticky-add-to-cart-quantity-selector-plus',
      inputId: 'sticky-add-to-qart-quantity-selector-input',
      popupId: 'sticky-add-to-cart-quantity-popup',
    };
  }

  private onQuantityChange(): void {
    this.subscriptions.add(
      this.addToCartForm.get('quantity')?.statusChanges.subscribe(() => {
        this.updatePrices(this.addToCartForm.get('quantity')?.value);
      }),
    );
  }

  private setMinimumQuantityForPrices(quantity: number): number {
    return quantity < this.orderQuantityMinimum ? this.orderQuantityMinimum : quantity;
  }
}
