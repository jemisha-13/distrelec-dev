import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
  signal,
} from '@angular/core';
import { UseWebpImage } from '@helpers/useWebpImage';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { Prices } from '@model/price.model';
import { Observable, Subscription, filter, take, tap } from 'rxjs';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormGroup, UntypedFormControl, AbstractControl } from '@angular/forms';
import { ProductQuantityService } from '@services/product-quantity.service';
import { ShoppingListComponentService } from '../core/services/shopping-list-component.service';
import { Product } from '@spartacus/core';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-shopping-list-item',
  templateUrl: './shopping-list-item.component.html',
  styleUrls: ['./shopping-list-item.component.scss'],
})
export class ShoppingListItemComponent implements OnInit, OnDestroy {
  @Input() listId: string;
  @Input() item: { product: Product; desired: number };
  @Input() itemIndex: number;
  @Input() prices: Prices;

  @Output() quantityChanged = new EventEmitter();
  @Output() removeProduct = new EventEmitter();
  @Output() selectProduct = new EventEmitter();

  @ViewChild('selectedProduct') selectedProduct: ElementRef;

  isItemRemoved = false;
  overrideMoq = false;

  ids: NumericStepperIds;

  availability$: Observable<ProductAvailability>;
  isMaximumQuantityReached = signal(false);

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });

  useWebpImg = UseWebpImage;

  private subscriptions = new Subscription();

  constructor(
    private productAvailabilityService: ProductAvailabilityService,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private productQuantityService: ProductQuantityService,
    private shoppingListComponentService: ShoppingListComponentService,
  ) {
    toObservable(this.shoppingListComponentService.addedToCartSignal)
      .pipe(takeUntilDestroyed())
      .subscribe(() => {
        if (this.isProductAddedToCart) {
          this.loadMaximumQuantityReached();
        }
      });
  }

  ngOnInit() {
    this.availability$ = this.productAvailabilityService.getAvailability(this.item.product?.code);

    this.ids = {
      minusButtonId: 'shopping-list-minusButtonId-' + this.itemIndex,
      plusButtonId: 'shopping-list-plusButtonId-' + this.itemIndex,
      inputId: 'shopping-list-popup-' + this.itemIndex,
      popupId: 'shopping-list-' + this.itemIndex,
    };

    this.setMinimumQuantity();
    this.onQuantityChange();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onSelected(event, productData) {
    this.selectProduct.emit({ event, productData });
  }

  onQuantityChange(): void {
    const control: AbstractControl<number> = this.addToCartForm.get('quantity');

    this.subscriptions.add(
      this.addToCartForm
        .get('quantity')
        .statusChanges.pipe(
          filter(
            () =>
              this.addToCartForm.get('quantity').errors !== null &&
              !this.productQuantityService.isQuantityInProgress(control),
          ),
          tap(() => {
            this.disableCheckboxWhenMaximumReached(control);
            if (
              control.value >= this.item.product.orderQuantityMinimum &&
              this.item.desired &&
              control.value !== this.item.desired
            ) {
              this.item.desired = control.value;

              this.quantityChanged.emit({
                quantity: control.value,
                listId: this.listId,
                productCode: this.item.product.code,
              });
            }
          }),
        )
        .subscribe(),
    );
  }

  remove(listId: string, productCode: string): void {
    //display the loading spinner when item is on delete stage
    this.isItemRemoved = true;
    this.removeProduct.emit({ listId, productCode, index: this.itemIndex });
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    return this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(eelImageMap);
  }

  private loadMaximumQuantityReached(): void {
    this.availability$.pipe(take(1)).subscribe((data) => {
      const { isBTR, hasStock, maximumQuantity } = this.productQuantityService.assignAvailability(data);
      const isMaximumReachable = this.productQuantityService.isMaximumReachable(isBTR, this.item?.product?.salesStatus);
      const isMaximumQuantityReached = this.productQuantityService.isMaximumQuantityReached(
        isMaximumReachable,
        this.item.product.code,
        maximumQuantity,
      );
      this.isMaximumQuantityReached.set(isMaximumQuantityReached);

      const { quantitySelected } = this.productQuantityService.onNumericStepperChange(
        this.addToCartForm?.get('quantity').value,
        maximumQuantity,
        this.item?.product?.orderQuantityMinimum,
        isMaximumReachable,
        hasStock,
        this.item.product.code,
        false,
      );

      this.addToCartForm?.get('quantity').setValue(quantitySelected, { emitEvent: true });
      this.addToCartForm.updateValueAndValidity();
    });
  }

  private disableCheckboxWhenMaximumReached(control: AbstractControl<number>): void {
    if (control.errors.length > 0) {
      this.isMaximumQuantityReached.set(
        control.errors?.find((err) => err.isMaximumQuantityReached)?.isMaximumQuantityReached,
      );
    }
  }

  private get isProductAddedToCart(): boolean | undefined {
    return this.shoppingListComponentService.addedToCartSignal().find((code) => code === this.item.product.code);
  }

  private setMinimumQuantity(): void {
    this.addToCartForm.get('quantity')?.setValue(this.item.desired);
  }
}
