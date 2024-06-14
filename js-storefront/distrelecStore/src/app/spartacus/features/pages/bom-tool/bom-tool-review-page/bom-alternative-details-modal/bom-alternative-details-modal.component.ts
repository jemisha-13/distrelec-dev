import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ModalComponent } from '@features/shared-modules/popups/modal/modal.component';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { pluck, take } from 'rxjs/operators';
import { createFrom, EventService } from '@spartacus/core';
import { DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PDFType } from '@features/tracking/model/pdf-types';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-bom-alternative-details-modal',
  templateUrl: './bom-alternative-details-modal.component.html',
  styleUrls: ['./bom-alternative-details-modal.component.scss'],
})
export class BomAlternativeDetailsModalComponent implements OnInit {
  @Input()
  product: ICustomProduct;

  @Output()
  close = new EventEmitter<void>();

  mediaDomain: string;

  @ViewChild(ModalComponent)
  private modal!: ModalComponent;

  constructor(
    private siteSettingsService: AllsitesettingsService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.siteSettingsService.currentChannelData$
      .pipe(take(1), pluck('mediaDomain'))
      .subscribe((mediaDomain) => (this.mediaDomain = mediaDomain));
  }

  get thumbnailUrl(): string {
    return this.product.images.find((image) => image.format === 'landscape_medium')?.url ?? '';
  }

  get manufacturerBrandLogoUrl(): string {
    return this.product.distManufacturer.image.find((image) => image.key === 'brand_logo')?.value.url ?? '';
  }

  dispatchPDFEvent(): void {
    this.eventService.dispatch(
      createFrom(DownloadPDFEvent, {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        context: { pageType: ItemListEntity.BOM, PDF_type: PDFType.BOMDETAILS, product: this.product },
      }),
    );
  }

  onClose() {
    this.modal.onClose();
    this.close.emit();
  }
}
