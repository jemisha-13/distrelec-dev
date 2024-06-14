import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CountryCodesEnum, CountryService } from 'src/app/spartacus/site-context/services/country.service';
import { ProductDataService } from '@features/pages/product/core/services/product-data.service';
import { Observable } from 'rxjs';
import { createFrom, EventService, Product } from '@spartacus/core';
import { DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PDFType } from '@features/tracking/model/pdf-types';

@Component({
  selector: 'app-back-order-alternative-product-details-modal',
  templateUrl: './back-order-alternative-product-details-modal.component.html',
  styleUrls: ['./back-order-alternative-product-details-modal.component.scss'],
})
export class BackOrderAlternativeProductDetailsModalComponent implements OnInit {
  @Input() productCode;
  @Output() closeAlternative = new EventEmitter<void>();

  product$: Observable<any>;
  siteCountryCode$: Observable<string> = this.countryService.getActive();
  countryCodesEnum = CountryCodesEnum;

  constructor(
    private productData: ProductDataService,
    private countryService: CountryService,
    private eventService: EventService,
  ) {}

  onClose() {
    this.closeAlternative.emit();
  }

  configureUrl(images): string {
    return images.filter((image) => image.format === 'landscape_medium' && image.imageType === 'PRIMARY')[0].url;
  }

  ngOnInit() {
    this.product$ = this.productData.getProductData(this.productCode);
  }

  dispatchPDFEvent(product: Product): void {
    this.eventService.dispatch(
      createFrom(DownloadPDFEvent, {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        context: { pageType: ItemListEntity.BACKORDER, PDF_type: PDFType.BACKORDER, product },
      }),
    );
  }
}
