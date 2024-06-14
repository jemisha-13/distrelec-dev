import { Component, Input, OnInit } from '@angular/core';
import { AppendComponentService } from '@services/append-component.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { ProductAvailabilityService } from '@features/pages/product/core/services/product-availability.service';
import { Observable } from 'rxjs';

import { faFilePdf } from '@fortawesome/free-regular-svg-icons';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { UseWebpImage } from '@helpers/useWebpImage';

import { AttributeItem } from '@features/pages/compare/components/compare-list/compare-list.component';
import { createFrom, EventService, Price, Product, UserIdService } from '@spartacus/core';
import { EnergyEfficiencyLabelService } from '@features/shared-modules/energy-efficiency-label/energy-efficiency-label.service';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { ProductClickEvent } from '@features/tracking/events/ga4/product-click-event';
import { PriceService } from '@services/price.service';
import { CompareService } from '@services/feature-services';
import { ShoppingListPayloadItem } from '../../../../../model/shopping-list.model';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ProductQuantityService } from '@services/product-quantity.service';
import { take } from 'rxjs/operators';
import { DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { PDFType } from '@features/tracking/model/pdf-types';

@Component({
  selector: 'app-compare-list-item',
  templateUrl: './compare-list-item.component.html',
  styleUrls: ['./compare-list-item.component.scss'],
})
export class CompareListItemComponent implements OnInit {
  @Input() product: Product;
  @Input() index: number;
  @Input() isEnergyLabelPresent: boolean;
  @Input() attributes: AttributeItem[] = [];

  currentChannel_ = this.siteSettingsService.currentChannelData$;

  availability$;

  faFilePdf = faFilePdf;
  faTimes = faTimes;
  useWebpImg = UseWebpImage;
  missingImgSrc = '/app/spartacus/assets/media/img/missing_landscape_small.png';
  userId$: Observable<string> = this.userIdService.getUserId();
  itemListEntity = ItemListEntity;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(null),
  });

  constructor(
    private compareService: CompareService,
    private appendComponentService: AppendComponentService,
    private siteSettingsService: AllsitesettingsService,
    private availabilityService: ProductAvailabilityService,
    private userIdService: UserIdService,
    private energyEfficiencyService: EnergyEfficiencyLabelService,
    private eventService: EventService,
    private productQuantityService: ProductQuantityService,
    private priceService: PriceService,
  ) {}

  ngOnInit(): void {
    this.setMinimumQuantity();
    this.availability$ = this.availabilityService.getAvailability(this.product.code);
  }

  dispatchPDFEvent(): void {
    this.eventService.dispatch(
      createFrom(DownloadPDFEvent, {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        context: { pageType: ItemListEntity.COMPARE, PDF_type: PDFType.DATASHEET, product: this.product },
      }),
    );
  }

  addToShoppingList(product: Product, userId: string): void {
    if (userId === 'current') {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendShoppingListModal(this.returnPayloadItems(product), ItemListEntity.COMPARE);
    } else {
      this.appendComponentService.appendBackdropModal();
      this.appendComponentService.appendLoginModal('shoppingListModal', {
        payloadItems: this.returnPayloadItems(product),
        itemListEntity: ItemListEntity.COMPARE,
      });
    }
  }

  deleteCompareProduct(productCode: string): void {
    this.compareService.removeCompareProduct([productCode]).subscribe();
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    return this.energyEfficiencyService.getEnergyEfficiencyLabelImageUrl(eelImageMap);
  }

  trackProductClick(product: Product, index: number) {
    this.eventService.dispatch(
      createFrom(ProductClickEvent, {
        product,
        listType: this.itemListEntity.COMPARE,
        index,
      } as ProductClickEvent),
    );
  }

  getPriceWithoutVatForB2B(price: Price): number {
    return this.priceService.getPriceWithoutVatForB2B(price);
  }

  getPriceWithVatForB2C(price: Price): number {
    return this.priceService.getPriceWithVatForB2C(price);
  }

  private returnPayloadItems(product: Product): ShoppingListPayloadItem[] {
    return [
      {
        product: { code: product.productCode ?? product.code },
      },
    ];
  }

  private setMinimumQuantity(): void {
    this.productQuantityService
      .getInitialProductQuantity(this.product.code, this.product.orderQuantityMinimum)
      .pipe(take(1))
      .subscribe((quantity) => this.addToCartForm.get('quantity').setValue(quantity));
  }
}
