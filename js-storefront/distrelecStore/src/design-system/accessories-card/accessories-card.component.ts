import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DefaultImageService } from '@services/default-image.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PriceService } from '@services/price.service';
import { createFrom, EventService, Product } from '@spartacus/core';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { addToCart } from '@assets/icons/icon-index';
import { ProductAccessoryType } from '@model/product-reference.model';
import { UntypedFormGroup, UntypedFormControl } from '@angular/forms';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability } from '@model/product-availability.model';
import { Observable } from 'rxjs';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

@Component({
  selector: 'app-dist-accessories-card',
  templateUrl: './accessories-card.component.html',
  styleUrls: ['./accessories-card.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DistAccessoriesCardComponent implements OnInit {
  @Input() product: Product;
  @Input() index = 0;
  @Input() currentChannel: CurrentSiteSettings;
  @Input() itemListEntity: ItemListEntity;
  @Input() excVatText = 'exc. VAT';
  @Input() incVatText = 'incl. VAT';
  @Input() addToCartText = 'Add to cart';
  @Input() addText = 'Add';
  @Input() accessoryType: ProductAccessoryType;

  currency: number;
  price: number;
  quantity: number;

  addToCart = addToCart;
  ids: NumericStepperIds;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1),
  });

  availability$: Observable<ProductAvailability>;

  constructor(
    private defaultImageService: DefaultImageService,
    private eventService: EventService,
    public priceService: PriceService,
    private productAvailabilityService: ProductAvailabilityService,
  ) {}

  ngOnInit(): void {
    this.price = this.priceService.getPriceForQuantity(
      this.product.volumePricesMap,
      this.product.orderQuantityMinimum,
      this.currentChannel.channel,
    );

    this.ids = this.assignIds();
    this.addToCartForm.get('quantity').setValue(this.product.orderQuantityMinimum);
    this.availability$ = this.productAvailabilityService.getAvailability(this.product.code);
  }

  onQuantityChange(qty: number): void {
    // use this value when user clicks on add to cart button
    this.price = this.priceService.getPriceForQuantity(this.product.volumePricesMap, qty, this.currentChannel.channel);
    this.quantity = qty;
  }

  findImage(type: string, images): string {
    const portraitSmallImage = images?.filter((image) => image.format === `portrait_${type}` && image.url)[0];
    if (portraitSmallImage) {
      return portraitSmallImage?.url ?? null;
    }
    return this.defaultImageService.getDefaultImageMedium();
  }

  trackProductClick(product: Product, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: this.itemListEntity,
        index,
      } as ProductClickEvent),
    );
  }

  assignIds(): NumericStepperIds {
    return {
      inputId: "'accessories_product_card_input_' + index",
      minusButtonId: "'accessories_product_card_minus_' + index",
      plusButtonId: "'accessories_product_card_plus_' + index",
    };
  }
}
