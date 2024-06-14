import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, WritableSignal, signal } from '@angular/core';
import { faCheckCircle } from '@fortawesome/free-regular-svg-icons';
import { createFrom, EventService, ImageGroup, Occ, ProductImageNormalizer } from '@spartacus/core';
import { BomFileEntry } from '@features/pages/bom-tool/model/bom-file';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { Observable, Subscription, take } from 'rxjs';
import { BomToolReviewService } from '@features/pages/bom-tool/bom-tool-review.service';
import { DefaultImageService } from '@services/default-image.service';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { Channel } from '@model/site-settings.model';
import { PriceService } from '@services/price.service';
import { ICustomProduct } from '@model/product.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormGroup, UntypedFormControl, AbstractControl } from '@angular/forms';
import { ProductQuantityService } from '@services/product-quantity.service';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-bom-product',
  templateUrl: './bom-product.component.html',
  styleUrls: ['./bom-product.component.scss'],
})
export class BomProductComponent implements OnInit, OnDestroy {
  @Input() entry: BomFileEntry;
  @Input() index: number;
  @Output() remove = new EventEmitter<BomFileEntry>();

  showDetailedView = false;
  activeChannel: Channel;
  productImage: ImageGroup;
  itemListEntity = ItemListEntity;
  price: number;

  isNumericStepperValid = true;

  faCheckCircle = faCheckCircle;
  numericStepperID: NumericStepperIds;
  availability$: Observable<ProductAvailability>;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });

  isMaximumQuantityReached: WritableSignal<boolean> = signal(false);

  private subscriptions = new Subscription();

  constructor(
    private bomToolReviewService: BomToolReviewService,
    private imageNormalizer: ProductImageNormalizer,
    private channelService: ChannelService,
    private defaultImage: DefaultImageService,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private eventService: EventService,
    public priceService: PriceService,
    private productAvailability: ProductAvailabilityService,
    private productQuantityService: ProductQuantityService,
  ) {}

  ngOnInit() {
    this.availability$ = this.productAvailability.getAvailability(this.product.code);
    this.addToCartForm.get('quantity').setValue(this.product.orderQuantityMinimum);

    this.bomToolReviewService.setEntry(this.entry);
    this.productImage = this.imageNormalizer.normalize(this.product.images as unknown as Occ.Image[])
      .PRIMARY as ImageGroup;

    this.subscriptions.add(this.channelService.getActive().subscribe((channel) => (this.activeChannel = channel)));

    this.subscriptions.add(
      this.bomToolReviewService.getAllSelected().subscribe((areAllSelected) => {
        this.isInputValid();
        return (this.entry.isSelected = areAllSelected && this.entry.isValid);
      }),
    );

    this.subscriptions.add(
      this.bomToolReviewService
        .getDetailedView()
        .subscribe((showDetailedView) => (this.showDetailedView = showDetailedView)),
    );

    if (this.entry.selectedAlternative) {
      this.entry.isSelected = true;
      this.onSelectChange();
    }

    this.price = this.priceService.getPriceBasedOnChannel(this.product.price, this.activeChannel);
    this.numericStepperID = this.assignNumericStepperID();

    this.subscriptions.add(this.addToCartForm.get('quantity').statusChanges.subscribe(() => this.onChange()));

    this.subscriptions.add(
      this.bomToolReviewService
        .getSelectSignal()
        .pipe()
        .subscribe(() => this.loadMaximumQuantityReached()),
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  get product() {
    return this.entry.selectedAlternative ?? this.entry.product;
  }

  get position() {
    return this.entry.position;
  }

  get productUrl() {
    return this.product.url;
  }

  get landscapeSmallJpg() {
    return this.productImage?.landscape_small?.url ?? this.defaultImage.getDefaultImage();
  }

  get landscapeSmallWebP() {
    return this.productImage?.landscape_small_webp?.url ?? this.landscapeSmallJpg;
  }

  onRemoveClick() {
    this.remove.emit(this.entry);
  }

  onChange(): void {
    this.disableCheckboxWhenMaximumReached(this.addToCartForm.get('quantity'));

    if (this.entry.quantity !== this.addToCartForm.get('quantity').value) {
      this.entry.quantity = this.addToCartForm.get('quantity').value;
      this.bomToolReviewService.setChanged();
    }
  }

  isInputValid() {
    const isValid =
      !this.productQuantityService.hasErrors(this.addToCartForm.get('quantity')) &&
      !this.addToCartForm.get('quantity').disabled;
    this.isNumericStepperValid = isValid;
    this.entry.isValid = isValid;

    if (isValid === false) {
      this.entry.isSelected = false;
    }
  }

  onSelectChange() {
    this.bomToolReviewService.triggerSelectChange();
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    return this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(eelImageMap);
  }

  trackProductClick(product: ICustomProduct, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: this.itemListEntity.BOM,
        index,
      } as ProductClickEvent),
    );
  }

  assignNumericStepperID(): NumericStepperIds {
    return {
      minusButtonId: 'bom_product_numeric_minus_btn_' + this.product.code,
      plusButtonId: 'bom_product_numeric_plus_btn_' + this.product.code,
      inputId: 'bom_product_numeric_input_' + this.product.code,
      popupId: 'bom_product_numeric_popup_' + this.product.code,
    };
  }

  private loadMaximumQuantityReached(): void {
    this.availability$.pipe(take(1)).subscribe((data) => {
      const { isBTR, hasStock, maximumQuantity } = this.productQuantityService.assignAvailability(data);
      const isMaximumReachable = this.productQuantityService.isMaximumReachable(
        isBTR,
        this.entry?.product?.salesStatus,
      );
      const isMaximumQuantityReached = this.productQuantityService.isMaximumQuantityReached(
        isMaximumReachable,
        this.entry.product.code,
        maximumQuantity,
      );

      this.isMaximumQuantityReached.set(isMaximumQuantityReached);

      const { quantitySelected } = this.productQuantityService.onNumericStepperChange(
        this.addToCartForm?.get('quantity').value,
        maximumQuantity,
        this.entry?.product?.orderQuantityMinimum,
        isMaximumReachable,
        hasStock,
        this.entry.product.code,
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
}
