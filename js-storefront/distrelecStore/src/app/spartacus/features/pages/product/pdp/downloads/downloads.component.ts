import { Component, Input, OnInit } from '@angular/core';
import { DownloadData } from '@model/downloads.model';
import { pdf } from '@assets/icons/icon-index';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import { Observable } from 'rxjs';
import { DownloadPDFEvent } from '@features/tracking/events/download-pdf-event';
import { createFrom, EventService, Product } from '@spartacus/core';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';
import { PDFType } from '@features/tracking/model/pdf-types';

@Component({
  selector: 'app-downloads',
  templateUrl: './downloads.component.html',
  styleUrls: ['./downloads.component.scss'],
})
export class DownloadsComponent implements OnInit {
  @Input() downloads: DownloadData[];
  @Input() product: Product;
  mediaDomain$: Observable<string> = this.siteSettingsService.getMediaDomain();
  pdf = pdf;
  toggleAlternative = [];
  allDownloads: DownloadData[];

  constructor(
    private siteSettingsService: AllsitesettingsService,
    private eventService: EventService,
  ) {}

  ngOnInit() {
    this.allDownloads = this.downloads.map(({ downloads, alternativeDownloads, ...rest }: any) => ({
      downloads: downloads.concat(alternativeDownloads),
      ...rest,
    }));

    for (const index in this.allDownloads) {
      if (index) {
        this.toggleAlternative[index] = false;
      }
    }
  }

  dispatchPDFEvent(): void {
    this.eventService.dispatch(
      createFrom(DownloadPDFEvent, {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        context: { pageType: ItemListEntity.BACKORDER, PDF_type: PDFType.BACKORDER, product: this.product },
      }),
    );
  }

  toggleAlternativeDownloads(index: number, isVisible: boolean) {
    this.toggleAlternative[index] = isVisible;
  }
}
