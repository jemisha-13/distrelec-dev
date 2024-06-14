import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { DefaultImageService } from '@services/default-image.service';
import { CurrentSiteSettings } from '@model/site-settings.model';
import { Prices } from '@model/price.model';
import { CopyProductNumberService } from '@services/copy-product-number.service';
import { LabelConfigService } from '@services/label-config.service';
import { Image, Occ } from '@spartacus/core';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { map, take } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { ICustomProduct } from '@model/product.model';
import { ManufacturerData } from '@model/manufacturer.model';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { ProductAvailability } from '@model/product-availability.model';
import { UntypedFormGroup, UntypedFormControl } from '@angular/forms';
import { NumericStepperIds } from '@features/shared-modules/components/numeric-stepper/model/numeric-stepper-ids.model';

export type ManufacturerImageResult = {
  isText: boolean;
  value: string;
};

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class DistProductCardComponent implements OnInit {
  @Input() productData: ICustomProduct;
  @Input() priceData: Prices;
  @Input() channelData: CurrentSiteSettings;
  @Input() isTitle: string;
  @Input() isImage: string;
  @Input() isArticle: string;
  @Input() isPrice: string;
  @Input() productCode: string;
  @Input() orientation: string;
  @Input() customDescription: string;
  @Input() snippet: string;
  @Input() customTitle: string;
  @Input() buttonType: string;
  @Input() brandLogo: string;
  @Input() topDisplay: string;
  @Input() labelDisplay: string;
  @Input() brandAlternateText: string;
  @Input() addToCartText = 'Add to Cart';
  @Input() excVatText = 'Excluding VAT';
  @Input() incVatText = 'Including VAT';
  @Input() artNrText = 'ArtNr.';
  @Input() mpnText = 'MPN.';
  @Input() copiedText = 'copied';
  @Input() hasCampaign = false;
  @Input() promotionParameter: string;

  @Output() addToCartEvent = new EventEmitter<void>();
  @Output() quantityChange = new EventEmitter<number>();

  @ViewChild('productCard', { static: true }) productCardRef;

  public copiedState$ = this.copyProductNumberService.copiedState$;

  manufacturerImageClass = '';
  manufacturerImage: ManufacturerImageResult;
  productImage$: Observable<string>;
  quantity: number;

  availability$: Observable<ProductAvailability>;
  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });
  numericStepperID: NumericStepperIds;

  private mediaDomain$: Observable<string> = this.siteSettingsService.getMediaDomain();

  constructor(
    private defaultImageService: DefaultImageService,
    private copyProductNumberService: CopyProductNumberService,
    private labelConfigService: LabelConfigService,
    private siteSettingsService: AllsitesettingsService,
    private availabilityService: ProductAvailabilityService,
  ) {}

  ngOnInit(): void {
    this.manufacturerImage = this.buildManufacturerImage(this.productData.distManufacturer);
    this.productImage$ = this.buildProductImage(this.productData.images);
    this.numericStepperID = this.assignNumerStepperIds();

    this.addToCartForm.get('quantity').setValue(this.quantity ?? this.productData.orderQuantityMinimum);

    if (this.buttonType.toLowerCase() === 'add_to_cart') {
      this.availability$ = this.availabilityService.getAvailability(this.productCode);
    }
  }

  public getProductImage(images: Occ.Image[]): Observable<string> {
    return this.buildProductImage(images);
  }

  public getManufacturerImage(manufacturer: ManufacturerData): ManufacturerImageResult {
    return this.buildManufacturerImage(manufacturer);
  }

  getPromotionText(productData: ICustomProduct): string {
    if (!productData?.activePromotionLabels || productData?.activePromotionLabels?.length === 0) {
      return '';
    }

    return productData.activePromotionLabels[0].label.toUpperCase();
  }

  getLabelColor(label: string): string {
    return this.labelConfigService.getColorByLabel(label);
  }

  onQuantityChange(qty: number): void {
    this.quantity = qty;
    this.quantityChange.emit(qty);
  }

  handleCopy(copiedText, origin): void {
    this.copyProductNumberService.copyNumber(copiedText, `${origin}_${this.productCode}`, origin);
  }

  isB2B(): boolean {
    return this.channelData.channel === 'B2B';
  }

  private buildManufacturerImage(distManufacturer: ManufacturerData): ManufacturerImageResult {
    const findImages = distManufacturer.image?.find((image) => image.value.format === `landscape_small`);
    if (findImages) {
      this.manufacturerImageClass = 'manufacturer-image';
    }

    const brandLogos = distManufacturer.image?.find(
      (image) =>
        (image.value.format === `landscape_small` ||
          image.value.format === `brand_logo` ||
          image.key === 'brand_logo') &&
        image.value.url,
    );

    if (!brandLogos) {
      return { isText: true, value: this.brandAlternateText ? this.brandAlternateText : distManufacturer.name };
    }

    return { isText: false, value: brandLogos.value.url };
  }

  private buildProductImage(images: Occ.Image[]): Observable<string> {
    return this.mediaDomain$.pipe(
      take(1),
      map((mediaDomain) => {
        if (this.hasCampaign) {
          const findLandscapeMedium = images?.find((image: Image) => image.format === 'landscape_medium' && image.url);
          return findLandscapeMedium
            ? mediaDomain + findLandscapeMedium.url
            : this.defaultImageService.getDefaultImageMedium();
        }

        const findPortraitMedium = images?.find((image: Image) => image.format === 'portrait_medium' && image.url);
        return findPortraitMedium ? findPortraitMedium.url : this.defaultImageService.getDefaultImageMedium();
      }),
    );
  }

  private assignNumerStepperIds(): NumericStepperIds {
    return {
      inputId: 'product_card_quantity_selector_input_' + this.productCode,
      plusButtonId: 'product_card_quantity_selector_plus_button_' + this.productCode,
      minusButtonId: 'product_card_quantity_selector_minus_button_' + this.productCode,
    };
  }
}
