import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import {
  PopupAlignment,
  PopupPosition,
} from '@features/shared-modules/components/min-order-quantity-popup/min-order-quantity-popup.component';
import { ProductQuantityService } from '@services/product-quantity.service';
import { ProductAvailability } from '@model/product-availability.model';
import { DistrelecBasesitesService } from '@services/basesites.service';

@Component({
  selector: 'app-numeric-stepper',
  templateUrl: './numeric-stepper.component.html',
  styleUrls: ['./numeric-stepper.component.scss'],
})
export class NumericStepperComponent implements OnInit, OnDestroy {
  @Input() minimumQuantity: number;
  @Input() quantityStep: number;
  @Input() productCode: string;

  @Input() autoAdjust = true; // Auto-fix values that are manually input and outside the quantityStep
  @Input() popupAlignment: PopupAlignment = 'center';
  @Input() popupPosition?: PopupPosition = 'top';

  @Input() isCart?: boolean;
  @Input() disabled?: boolean;
  @Input() salesStatus: string;

  @Input() availabilityData?: ProductAvailability;

  isBTR: boolean;
  maximumQuantity: number;
  hasStock: boolean;

  @Input() ids: {
    minusButtonId: string;
    plusButtonId: string;
    inputId: string;
    popupId: string;
  };

  @Input() control?: UntypedFormControl;

  @Input() inputDelay?: number;

  isAddToCartDisabled$: Observable<boolean> = this.distBaseSiteService.isAddToCartDisabledForActiveSite();

  private subscriptions: Subscription = new Subscription();

  constructor(
    private productQuantityService: ProductQuantityService,
    private distBaseSiteService: DistrelecBasesitesService,
  ) {}

  ngOnInit() {
    this.subscriptions.add(this.productQuantityService.getCartState().subscribe());

    this.assignAvailability(this.availabilityData);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  assignAvailability(data: ProductAvailability): void {
    const result = this.productQuantityService.assignAvailability(data);

    this.isBTR = result.isBTR;
    this.hasStock = result.hasStock;
    this.maximumQuantity = result.maximumQuantity;

    this.isSalesStatus50();
  }

  // Run a special case scenario check for status 50's if control is not disabled yet
  private isSalesStatus50(): void {
    if (!this.disabled) {
      this.productQuantityService
        .status50CountryCheck(this.salesStatus, this.availabilityData)
        .pipe(take(1))
        .subscribe((disabled) => (this.disabled = disabled));
    }
  }

  get isMaximumReachable(): boolean {
    return this.productQuantityService.isMaximumReachable(this.isBTR, this.salesStatus);
  }

  get isMaximumSelectedError(): boolean {
    return this.control?.errors.find((error) => error.maxOrderQuantityDisplayed)?.maxOrderQuantityDisplayed;
  }
}
